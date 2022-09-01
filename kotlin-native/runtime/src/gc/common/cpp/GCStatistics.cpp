/*
 * Copyright 2010-2022 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

#include "GCStatistics.hpp"
#include "Mutex.hpp"
#include "Porting.h"

#include "Types.h"
#include <optional>


extern "C" {
void Kotlin_Internal_GC_GCInfoBuilder_setEpoch(KRef thiz, KLong value);
void Kotlin_Internal_GC_GCInfoBuilder_setStartTime(KRef thiz, KLong value);
void Kotlin_Internal_GC_GCInfoBuilder_setEndTime(KRef thiz, KLong value);
void Kotlin_Internal_GC_GCInfoBuilder_setPauseStartTime(KRef thiz, KLong value);
void Kotlin_Internal_GC_GCInfoBuilder_setPauseEndTime(KRef thiz, KLong value);
void Kotlin_Internal_GC_GCInfoBuilder_setFinalizersDoneTime(KRef thiz, KLong value);
void Kotlin_Internal_GC_GCInfoBuilder_setRootSet(KRef thiz, KLong threadLocalReferences, KLong stackReferences, KLong globalReferences, KLong stableReferences);
void Kotlin_Internal_GC_GCInfoBuilder_setMemoryUsageBefore(KRef thiz, KNativePtr name, KLong objectsCount, KLong totalObjectsSize);
void Kotlin_Internal_GC_GCInfoBuilder_setMemoryUsageAfter(KRef thiz, KNativePtr name, KLong objectsCount, KLong totalObjectsSize);
}

namespace {

struct MemoryUsage {
    KLong objectsCount;
    KLong totalObjectsSize;
};

struct MemoryUsageMap {
    std::optional<MemoryUsage> heapUsage;
    
    void build(KRef builder, void (*add)(KRef, KNativePtr, KLong, KLong)) {
        if (heapUsage) {
            add(builder, const_cast<KNativePtr>(static_cast<const void*>("heap")), heapUsage->objectsCount, heapUsage->totalObjectsSize);
        }
    }
};

struct RootSetStatistics {
    KLong threadLocalReferences;
    KLong stackReferences;
    KLong globalReferences;
    KLong stableReferences;
};

struct GCInfo {
    KLong epoch = -1;
    KLong startTime = -1; // time since process start
    std::optional<KLong> endTime;
    std::optional<KLong> pauseStartTime;
    std::optional<KLong> pauseEndTime;
    std::optional<KLong> finalizersDoneTime;
    std::optional<RootSetStatistics> rootSet;
    MemoryUsageMap memoryUsageBefore;
    MemoryUsageMap memoryUsageAfter;
    
    void build(KRef builder) {
        if (epoch == -1) return;
        Kotlin_Internal_GC_GCInfoBuilder_setEpoch(builder, epoch);
        Kotlin_Internal_GC_GCInfoBuilder_setStartTime(builder, startTime);
        if (endTime) Kotlin_Internal_GC_GCInfoBuilder_setEndTime(builder, *endTime);
        if (pauseStartTime) Kotlin_Internal_GC_GCInfoBuilder_setPauseStartTime(builder, *pauseStartTime);
        if (pauseEndTime) Kotlin_Internal_GC_GCInfoBuilder_setPauseEndTime(builder, *pauseEndTime);
        if (finalizersDoneTime) Kotlin_Internal_GC_GCInfoBuilder_setFinalizersDoneTime(builder, *finalizersDoneTime);
        if (rootSet)  Kotlin_Internal_GC_GCInfoBuilder_setRootSet(builder,
                                                                 rootSet->threadLocalReferences, rootSet->stackReferences,
                                                                 rootSet->globalReferences, rootSet->stableReferences);
        memoryUsageBefore.build(builder, Kotlin_Internal_GC_GCInfoBuilder_setMemoryUsageBefore);
        memoryUsageAfter.build(builder, Kotlin_Internal_GC_GCInfoBuilder_setMemoryUsageAfter);
    }
};

GCInfo last;
GCInfo current;
kotlin::SpinLock<kotlin::MutexThreadStateHandling::kSwitchIfRegistered> lock;

}

extern "C" void Kotlin_Internal_GC_GCInfoBuilder_Fill(KRef builder, int id) {
    GCInfo copy;
    {
        std::lock_guard guard(lock);
        if (id == 0) {
            copy = last;
        } else if (id == 1) {
            copy = current;
        } else {
            return;
        }
    }
    copy.build(builder);
}



namespace kotlin::gc {
void reportGCStart(uint64_t epoch) {
    std::lock_guard guard(lock);
    current.epoch = static_cast<KLong>(epoch);
    current.startTime = static_cast<KLong>(konan::getTimeNanos());
}
void reportGCFinish() {
    std::lock_guard guard(lock);
    current.endTime = static_cast<KLong>(konan::getTimeNanos());
    last = current;
    current = {};
}
void reportPauseStart() {
    std::lock_guard guard(lock);
    current.pauseStartTime = static_cast<KLong>(konan::getTimeNanos());
}
void reportPauseEnd() {
    std::lock_guard guard(lock);
    current.pauseEndTime = static_cast<KLong>(konan::getTimeNanos());
}
void reportFinalizersDone(uint64_t epoch) {
    std::lock_guard guard(lock);
    if (current.epoch == static_cast<KLong>(epoch)) current.pauseEndTime = static_cast<KLong>(konan::getTimeNanos());
    if (last.epoch == static_cast<KLong>(epoch)) last.pauseEndTime = static_cast<KLong>(konan::getTimeNanos());
}
void reportRootSet(uint64_t threadLocalReferences, uint64_t stackReferences, uint64_t globalReferences, uint64_t stableReferences) {
    std::lock_guard guard(lock);
    if (!current.rootSet) {
        current.rootSet = RootSetStatistics{0, 0, 0, 0};
    }
    current.rootSet->threadLocalReferences += static_cast<KLong>(threadLocalReferences);
    current.rootSet->stackReferences += static_cast<KLong>(stackReferences);
    current.rootSet->globalReferences += static_cast<KLong>(globalReferences);
    current.rootSet->threadLocalReferences += static_cast<KLong>(stackReferences);
}
void reportHeapUsageBefore(uint64_t objectsCount, uint64_t totalObjectsSize) {
    std::lock_guard guard(lock);
    current.memoryUsageBefore.heapUsage = MemoryUsage{static_cast<KLong>(objectsCount), static_cast<KLong>(totalObjectsSize)};
}
void reportHeapUsageAfter(uint64_t objectsCount, uint64_t totalObjectsSize) {
    std::lock_guard guard(lock);
    current.memoryUsageAfter.heapUsage = MemoryUsage{static_cast<KLong>(objectsCount), static_cast<KLong>(totalObjectsSize)};
}
}