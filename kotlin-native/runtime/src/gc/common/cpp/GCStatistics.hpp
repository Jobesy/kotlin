/*
 * Copyright 2010-2022 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

#pragma once

#include <cstdint>

namespace kotlin::gc {
void reportGCStart(uint64_t epoch);
void reportGCFinish();
void reportFinalizersDone(uint64_t epoch);
void reportPauseStart();
void reportPauseEnd();
void reportRootSet(uint64_t threadLocalReferences, uint64_t stackReferences, uint64_t globalReferences, uint64_t stableReferences);
void reportHeapUsageBefore(uint64_t objectsCount, uint64_t totalObjectSize);
void reportHeapUsageAfter(uint64_t objectsCount, uint64_t totalObjectSize);
}