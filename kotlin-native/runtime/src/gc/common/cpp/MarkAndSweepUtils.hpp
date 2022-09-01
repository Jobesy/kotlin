/*
 * Copyright 2010-2021 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */

#ifndef RUNTIME_GC_COMMON_MARK_AND_SWEEP_UTILS_H
#define RUNTIME_GC_COMMON_MARK_AND_SWEEP_UTILS_H

#include "ExtraObjectData.hpp"
#include "FinalizerHooks.hpp"
#include "GlobalData.hpp"
#include "Logging.hpp"
#include "Memory.h"
#include "ObjectOps.hpp"
#include "ObjectTraversal.hpp"
#include "RootSet.hpp"
#include "Runtime.h"
#include "StableRefRegistry.hpp"
#include "ThreadData.hpp"
#include "Types.h"

namespace kotlin {
namespace gc {

struct MarkStats {
    // How many objects are alive.
    size_t aliveHeapSet = 0;
    // How many objects are alive in bytes. Note: this does not include overhead of malloc/mimalloc itself.
    size_t aliveHeapSetBytes = 0;
    // How many roots of each type were marked.
    size_t threadLocalRoots = 0;
    size_t stackRoots = 0;
    size_t globalRoots = 0;
    size_t stableRoots = 0;

    void merge(MarkStats other) {
        aliveHeapSet += other.aliveHeapSet;
        aliveHeapSetBytes += other.aliveHeapSetBytes;
        threadLocalRoots += other.threadLocalRoots;
        stackRoots += other.stackRoots;
        globalRoots += other.globalRoots;
        stableRoots += other.stableRoots;
    }

    size_t rootSetSize() const {
        return threadLocalRoots + stackRoots + globalRoots + stableRoots;
    }
};

template <typename Traits>
MarkStats Mark(typename Traits::MarkQueue& markQueue) noexcept {
    MarkStats stats;
    auto timeStart = konan::getTimeMicros();
    while (!Traits::isEmpty(markQueue)) {
        ObjHeader* top = Traits::dequeue(markQueue);

        RuntimeAssert(!isNullOrMarker(top), "Got invalid reference %p in mark queue", top);
        RuntimeAssert(top->heap(), "Got non-heap reference %p in mark queue, permanent=%d stack=%d", top, top->permanent(), top->local());

        stats.aliveHeapSet++;
        stats.aliveHeapSetBytes += mm::GetAllocatedHeapSize(top);

        traverseReferredObjects(top, [&](ObjHeader* field) noexcept {
            if (!isNullOrMarker(field) && field->heap()) {
                Traits::enqueue(markQueue, field);
            }
        });

        if (auto* extraObjectData = mm::ExtraObjectData::Get(top)) {
            auto weakCounter = extraObjectData->GetWeakReferenceCounter();
            if (!isNullOrMarker(weakCounter)) {
                RuntimeAssert(
                        weakCounter->heap(), "Weak counter must be a heap object. object=%p counter=%p permanent=%d local=%d", top,
                        weakCounter, weakCounter->permanent(), weakCounter->local());
                Traits::enqueue(markQueue, weakCounter);
            }
        }
    }
    auto timeEnd = konan::getTimeMicros();
    RuntimeLogDebug({kTagGC}, "Marked %zu objects in %" PRIu64 " microseconds in thread %d", stats.aliveHeapSet, timeEnd - timeStart, konan::currentThreadId());
    return stats;
}

template <typename Traits>
void SweepExtraObjects(typename Traits::ExtraObjectsFactory& objectFactory) noexcept {
    objectFactory.ProcessDeletions();
    auto iter = objectFactory.LockForIter();
    for (auto it = iter.begin(); it != iter.end();) {
        auto &extraObject = *it;
        if (!extraObject.getFlag(mm::ExtraObjectData::FLAGS_IN_FINALIZER_QUEUE) && !Traits::IsMarkedByExtraObject(extraObject)) {
            extraObject.ClearWeakReferenceCounter();
            if (extraObject.HasAssociatedObject()) {
                extraObject.DetachAssociatedObject();
                extraObject.setFlag(mm::ExtraObjectData::FLAGS_IN_FINALIZER_QUEUE);
                ++it;
            } else {
                extraObject.Uninstall();
                objectFactory.EraseAndAdvance(it);
            }
        } else {
            ++it;
        }
    }
}

template <typename Traits>
typename Traits::ObjectFactory::FinalizerQueue Sweep(typename Traits::ObjectFactory::Iterable& objectFactoryIter) noexcept {
    typename Traits::ObjectFactory::FinalizerQueue finalizerQueue;

    for (auto it = objectFactoryIter.begin(); it != objectFactoryIter.end();) {
        if (Traits::TryResetMark(*it)) {
            ++it;
            continue;
        }
        auto* objHeader = it->GetObjHeader();
        if (HasFinalizers(objHeader)) {
            objectFactoryIter.MoveAndAdvance(finalizerQueue, it);
        } else {
            objectFactoryIter.EraseAndAdvance(it);
        }
    }

    return finalizerQueue;
}

template <typename Traits>
typename Traits::ObjectFactory::FinalizerQueue Sweep(typename Traits::ObjectFactory& objectFactory) noexcept {
    auto iter = objectFactory.LockForIter();
    return Sweep<Traits>(iter);
}

template <typename Traits>
MarkStats collectRootSetForThread(typename Traits::MarkQueue& markQueue, mm::ThreadData& thread) {
    thread.gc().OnStoppedForGC();
    MarkStats stats;
    for (auto value : mm::ThreadRootSet(thread)) {
        auto* object = value.object;
        if (!isNullOrMarker(object)) {
            if (object->heap()) {
                Traits::enqueue(markQueue, object);
            } else {
                traverseReferredObjects(object, [&](ObjHeader* field) noexcept {
                    // Each permanent and stack object has own entry in the root set.
                    if (field->heap() && !isNullOrMarker(field)) {
                        Traits::enqueue(markQueue, field);
                    }
                });
                RuntimeAssert(!object->has_meta_object(), "Non-heap object %p may not have an extra object data", object);
            }
            switch (value.source) {
                case mm::ThreadRootSet::Source::kStack:
                    ++stats.stackRoots;
                    break;
                case mm::ThreadRootSet::Source::kTLS:
                    ++stats.threadLocalRoots;
                    break;
            }
        }
    }
    RuntimeLogDebug({kTagGC}, "Collected root set for thread stack=%zu tls=%zu", stats.stackRoots, stats.threadLocalRoots);
    return stats;
}

template <typename Traits>
MarkStats collectRootSetGlobals(typename Traits::MarkQueue& markQueue) noexcept {
    mm::StableRefRegistry::Instance().ProcessDeletions();
    MarkStats stats;
    for (auto value : mm::GlobalRootSet()) {
        auto* object = value.object;
        if (!isNullOrMarker(object)) {
            if (object->heap()) {
                Traits::enqueue(markQueue, object);
            } else {
                traverseReferredObjects(object, [&](ObjHeader* field) noexcept {
                    // Each permanent and stack object has own entry in the root set.
                    if (field->heap() && !isNullOrMarker(field)) {
                        Traits::enqueue(markQueue, field);
                    }
                });
                RuntimeAssert(!object->has_meta_object(), "Non-heap object %p may not have an extra object data", object);
            }
            switch (value.source) {
                case mm::GlobalRootSet::Source::kGlobal:
                    ++stats.globalRoots;
                    break;
                case mm::GlobalRootSet::Source::kStableRef:
                    ++stats.stableRoots;
                    break;
            }
        }
    }
    RuntimeLogDebug({kTagGC}, "Collected global root set global=%zu stableRef=%zu", stats.globalRoots, stats.stableRoots);
    return stats;
}

// TODO: This needs some tests now.
template <typename Traits, typename F>
MarkStats collectRootSet(typename Traits::MarkQueue& markQueue, F&& filter) noexcept {
    MarkStats stats;
    Traits::clear(markQueue);
    for (auto& thread : mm::GlobalData::Instance().threadRegistry().LockForIter()) {
        if (!filter(thread))
            continue;
        thread.Publish();
        stats.merge(collectRootSetForThread<Traits>(markQueue, thread));
    }
    stats.merge(collectRootSetGlobals<Traits>(markQueue));
    return stats;
}

} // namespace gc
} // namespace kotlin

#endif // RUNTIME_GC_COMMON_MARK_AND_SWEEP_UTILS_H
