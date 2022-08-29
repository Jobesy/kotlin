/*
 * Copyright 2010-2022 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.plugin.sources

import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.utils.getOrPut

private val Project.kotlinSourceSetRelationRegistryImpl: KotlinSourceSetRelationRegistryImpl
    get() = this.extensions.extraProperties.getOrPut("kotlinSourceSetRelationRegistryImpl") {
        KotlinSourceSetRelationRegistryImpl()
    }

internal val Project.kotlinSourceSetRelationRegistry: KotlinSourceSetRelationRegistry
    get() = kotlinSourceSetRelationRegistryImpl

internal val Project.kotlinSourceSetRelationService: KotlinSourceSetRelationService
    get() = kotlinSourceSetRelationRegistryImpl

internal interface KotlinSourceSetRelationRegistry {
    fun registerDependsOnEdge(from: KotlinSourceSet, to: KotlinSourceSet)
    fun registerSourceSet(compilation: KotlinCompilation<*>, sourceSet: KotlinSourceSet)
}

internal interface KotlinSourceSetRelationService {
    fun getDependsOnSourceSets(sourceSet: KotlinSourceSet): Set<KotlinSourceSet>
    fun getDependsOnSourceSetsClosure(sourceSet: KotlinSourceSet): Set<KotlinSourceSet>

    fun getReverseDependsOnSourceSets(sourceSet: KotlinSourceSet): Set<KotlinSourceSet>
    fun getReverseDependsOnSourceSetsClosure(sourceSet: KotlinSourceSet): Set<KotlinSourceSet>

    fun getCompilationsClosure(sourceSet: KotlinSourceSet): Set<KotlinCompilation<*>>
    fun getSourceSetsClosure(compilation: KotlinCompilation<*>): Set<KotlinSourceSet>
}


