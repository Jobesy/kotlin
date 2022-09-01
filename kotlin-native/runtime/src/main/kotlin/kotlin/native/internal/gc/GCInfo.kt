/*
 * Copyright 2010-2022 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package kotlin.native.internal.gc

import kotlin.native.internal.*
import kotlin.native.internal.NativePtr
import kotlin.native.concurrent.*
import kotlin.time.*
import kotlin.time.Duration.Companion.nanoseconds
import kotlinx.cinterop.*
import kotlin.system.*


@ExperimentalStdlibApi
data class MemoryUsage(
        val objectsCount: Long?,
        val totalObjectsSize: Long?,
)

/**
 * Number of nodes in root set for garbage collector run, separated by root set pools.
 * This nodes are assumed to be used, even if there are no references for it.
 *
 * @property threadLocalReferences Number of objects in global variables with @ThreadLocal annotation.
 *                                 Object is counted for each thread it was initialized.
 * @property stableReferences Number of objects referenced from stack of any thread.
 *                            This are function local variables, and different temporary values, as function call
 *                            arguments and return values. They would be automatically removed from root set,
 *                            when function call is finished.
 * @property globalReferences Number of objects in global variables. Object is counted only if variable is initialized.
 * @property stableReferences Number of objects referenced by [kotlinx.cinterop.StableRef]. It includes both explicit usages
 *                            of this API, and internal usages, e.g. indside interops and Worker API.
 */
@ExperimentalStdlibApi
data class RootSetStatistics(
        val threadLocalReferences: Long,
        val stackReferences: Long,
        val globalReferences: Long,
        val stableReferences: Long
)

@ExperimentalStdlibApi
private class GCInfoBuilder() {
    var epoch: Long? = null
    var startTimeNs: Long? = null
    var endTimeNs: Long? = null
    var pauseStartTimeNs: Long? = null
    var pauseEndTimeNs: Long? = null
    var finalizersDoneTimeNs: Long? = null
    var rootSet: RootSetStatistics? = null
    var memoryUsageBefore: MutableMap<String, MemoryUsage>? = null
    var memoryUsageAfter: MutableMap<String, MemoryUsage>? = null

    @ExportForCppRuntime("Kotlin_Internal_GC_GCInfoBuilder_setEpoch")
    private fun setEpoch(value: Long) {
        epoch = value
    }

    @ExportForCppRuntime("Kotlin_Internal_GC_GCInfoBuilder_setStartTime")
    private fun setStartTime(value: Long) {
        startTimeNs = value
    }

    @ExportForCppRuntime("Kotlin_Internal_GC_GCInfoBuilder_setEndTime")
    private fun setEndTime(value: Long) {
        endTimeNs = value
    }

    @ExportForCppRuntime("Kotlin_Internal_GC_GCInfoBuilder_setPauseStartTime")
    private fun setPauseStartTime(value: Long) {
        pauseStartTimeNs = value
    }

    @ExportForCppRuntime("Kotlin_Internal_GC_GCInfoBuilder_setPauseEndTime")
    private fun setPauseEndTime(value: Long) {
        pauseEndTimeNs = value
    }

    @ExportForCppRuntime("Kotlin_Internal_GC_GCInfoBuilder_setFinalizersDoneTime")
    private fun setFinalizersDoneTime(value: Long) {
        finalizersDoneTimeNs = value
    }

    @ExportForCppRuntime("Kotlin_Internal_GC_GCInfoBuilder_setRootSet")
    private fun setRootSet(threadLocalReferences: Long, stackReferences: Long, globalReferences: Long, stableReferences: Long) {
        rootSet = RootSetStatistics(threadLocalReferences, stackReferences, globalReferences, stableReferences)
    }

    @ExportForCppRuntime("Kotlin_Internal_GC_GCInfoBuilder_setMemoryUsageBefore")
    private fun setMemoryUsageBefore(name: NativePtr, objectsCount: Long, totalObjectsSize: Long) {
        val nameString = interpretCPointer<ByteVar>(name)!!.toKString()
        val memoryUsage = MemoryUsage(objectsCount.takeIf { it >= 0 }, totalObjectsSize.takeIf { it >= 0 })
        if (memoryUsageBefore == null) {
            memoryUsageBefore = mutableMapOf(nameString to memoryUsage)
        } else {
            memoryUsageBefore!!.put(nameString, memoryUsage)
        }
    }

    @ExportForCppRuntime("Kotlin_Internal_GC_GCInfoBuilder_setMemoryUsageAfter")
    private fun setMemoryUsageAfter(name: NativePtr, objectsCount: Long, totalObjectsSize: Long) {
        val nameString = interpretCPointer<ByteVar>(name)!!.toKString()
        val memoryUsage = MemoryUsage(objectsCount.takeIf { it >= 0 }, totalObjectsSize.takeIf {it >= 0 })
        if (memoryUsageAfter == null) {
            memoryUsageAfter = mutableMapOf(nameString to memoryUsage)
        } else {
            memoryUsageAfter!!.put(nameString, memoryUsage)
        }
    }

    fun build(): GCInfo? {
        return if (epoch == null || startTimeNs == null)
            null
        else GCInfo(
                epoch!!, startTimeNs!!, endTimeNs, pauseStartTimeNs, pauseEndTimeNs, finalizersDoneTimeNs, rootSet, memoryUsageBefore?.toMap(), memoryUsageAfter?.toMap()
        )
    }

    @GCUnsafeCall("Kotlin_Internal_GC_GCInfoBuilder_Fill")
    external fun fill(id: Int)
}

/**
 * This is class representing statistics about one run of garbage collector.
 * It is supposed to be used for testing and debug purposes only. Not all values can be availiable for all gc implementations.
 *
 * @property epoch  ID of gc run
 * @property startTimeNs Time, when gc run is started, meausered by [kotlin.system.getTimeNanos]
 * @property endTimeNs Time, when gc run is ended, measured by [kotlin.system.getTimeNanos]. After this point, most of memory is reclaimed,
 *                   and new GC run can start.
 * @property duration Difference between [endTimeNs] and [startTimeNs]. This is best estimation of how long is gc run.
 * @property pauseStartTimeNs Time, when mutator threads are suspended, mesured by [kotlin.system.getTimeNanos].
 * @property pauseEndTimeNs Time, when mutator threads are unsuspended, mesured by [kotlin.system.getTimeNanos].
 * @property pauseDuration Difference between [pauseEndTimeNs] and [pauseStartTimeNs]. This is best estimation of how long no application
 *           operations can happen because of gc run.
 * @property finilisersDoneTimeNs Time, when all memory is reclaimed, measured by [kotlin.system.getTimeNanos].
 *           If null, memory reclaiming is still in progress.
 * @property rootSet Number of objects in each rootSet pool. Check [RootSetStatistics] doc for details.
 * @property memoryUsageAfter Memory usage at start of gc run, separated by memory pools. Set of memory pools depends on collector.
 * @property memoryUsageBefore Memory usage at end of gc run, excluding all memory scheduled to reclaim, separated by memory pools. Set of memory pools depends on collector.
 */
@ExperimentalStdlibApi
data class GCInfo(
        val epoch: Long,
        val startTimeNs: Long, // time since process start
        val endTimeNs: Long?,
        val pauseStartTimeNs: Long?,
        val pauseEndTimeNs: Long?,
        val finilisersDoneTimeNs: Long?,
        val rootSet: RootSetStatistics?,
        val memoryUsageBefore: Map<String, MemoryUsage>?, // key is memory pool, probably only "heap" now
        val memoryUsageAfter: Map<String, MemoryUsage>?,
) {
    val duration: Duration?
        get() = endTimeNs?.let { (it - startTimeNs).nanoseconds }
    val pauseDuration: Duration?
        get() = if (pauseEndTimeNs != null && pauseStartTimeNs != null) (pauseEndTimeNs - pauseStartTimeNs).nanoseconds else null
    val durationWithFinalizers: Duration?
        get() = finilisersDoneTimeNs?.let { (it - startTimeNs).nanoseconds }

    internal companion object {
        val lastGCInfo: GCInfo?
            get() = getGcInfo(0)
        val runningGCInfo: GCInfo?
            get() = getGcInfo(1)

        private fun getGcInfo(id: Int) = GCInfoBuilder().apply { fill(id) }.build();
    }
}