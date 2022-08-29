/*
 * Copyright 2010-2022 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.plugin.sources

import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

internal interface KotlinSourceSetRelationRegistry {
    fun registerDependsOnEdge(from: KotlinSourceSet, to: KotlinSourceSet)
    fun registerSourceSet(compilation: KotlinCompilation<*>, sourceSet: KotlinSourceSet)
}

internal interface KotlinSourceSetRelationsService {
    fun getDependsOnSourceSets(sourceSet: KotlinSourceSet): Set<KotlinSourceSet>
    fun getDependsOnSourceSetsClosure(sourceSet: KotlinSourceSet): Set<KotlinSourceSet>

    fun getReverseDependsOnSourceSets(sourceSet: KotlinSourceSet): Set<KotlinSourceSet>
    fun getReverseDependsOnSourceSetsClosure(sourceSet: KotlinSourceSet): Set<KotlinSourceSet>

    fun getCompilationsClosure(sourceSet: KotlinSourceSet): Set<KotlinCompilation<*>>
    fun getSourceSetsClosure(compilation: KotlinCompilation<*>): Set<KotlinSourceSet>
}


