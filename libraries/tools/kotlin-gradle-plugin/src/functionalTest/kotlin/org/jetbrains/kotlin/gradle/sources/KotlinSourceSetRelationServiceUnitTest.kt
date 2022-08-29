/*
 * Copyright 2010-2022 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("FunctionName")

package org.jetbrains.kotlin.gradle.sources

import org.jetbrains.kotlin.gradle.buildProjectWithMPP
import org.jetbrains.kotlin.gradle.dsl.multiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.sources.KotlinSourceSetRelationRegistryImpl
import kotlin.test.Test
import kotlin.test.assertEquals

class KotlinSourceSetRelationServiceUnitTest {

    private val service = KotlinSourceSetRelationRegistryImpl()

    private val project = buildProjectWithMPP()

    private val sourceSets = project.multiplatformExtension.sourceSets

    @Test
    fun `test simple dependsOn edge`() {
        val sourceSetA = sourceSets.create("a")
        val sourceSetB = sourceSets.create("b")

        assertEquals(emptySet(), service.getDependsOnSourceSets(sourceSetA))
        assertEquals(emptySet(), service.getDependsOnSourceSets(sourceSetB))
        assertEquals(emptySet(), service.getDependsOnSourceSetsClosure(sourceSetA))
        assertEquals(emptySet(), service.getDependsOnSourceSetsClosure(sourceSetB))

        assertEquals(emptySet(), service.getReverseDependsOnSourceSets(sourceSetA))
        assertEquals(emptySet(), service.getReverseDependsOnSourceSets(sourceSetB))
        assertEquals(emptySet(), service.getReverseDependsOnSourceSetsClosure(sourceSetA))
        assertEquals(emptySet(), service.getReverseDependsOnSourceSetsClosure(sourceSetB))

        service.registerDependsOnEdge(sourceSetB, sourceSetA)

        assertEquals(emptySet(), service.getDependsOnSourceSets(sourceSetA))
        assertEquals(setOf(sourceSetA), service.getDependsOnSourceSets(sourceSetB))
    }

    @Test
    fun `add multiple dependsOn edges`() {
        val sourceSetA = sourceSets.create("a")
        val sourceSetB = sourceSets.create("b")
        val sourceSetC = sourceSets.create("c")

        /* c -> b -> a */
        service.registerDependsOnEdge(sourceSetB, sourceSetA)
        service.registerDependsOnEdge(sourceSetC, sourceSetB)

        /* Check dependsOn */
        assertEquals(emptySet(), service.getDependsOnSourceSets(sourceSetA))
        assertEquals(setOf(sourceSetA), service.getDependsOnSourceSets(sourceSetB))
        assertEquals(setOf(sourceSetB), service.getDependsOnSourceSets(sourceSetC))

        /* Check dependsOn closure */
        assertEquals(emptySet(), service.getDependsOnSourceSetsClosure(sourceSetA))
        assertEquals(setOf(sourceSetA), service.getDependsOnSourceSetsClosure(sourceSetB))
        assertEquals(setOf(sourceSetA, sourceSetB), service.getDependsOnSourceSetsClosure(sourceSetC))

        /* Check reverse dependsOn */
        assertEquals(setOf(sourceSetB), service.getReverseDependsOnSourceSets(sourceSetA))
        assertEquals(setOf(sourceSetC), service.getReverseDependsOnSourceSets(sourceSetB))
        assertEquals(emptySet(), service.getReverseDependsOnSourceSets(sourceSetC))

        /* Check reverse dependsOn closure */
        assertEquals(setOf(sourceSetB, sourceSetC), service.getReverseDependsOnSourceSetsClosure(sourceSetA))
        assertEquals(setOf(sourceSetC), service.getReverseDependsOnSourceSetsClosure(sourceSetB))
        assertEquals(emptySet(), service.getReverseDependsOnSourceSetsClosure(sourceSetC))
    }

    @Test
    fun `compilations - after adding extra dependsOn edge`() {
        val sourceSetA = sourceSets.create("a")
        val sourceSetB = sourceSets.create("b")
        val sourceSetC = sourceSets.create("c")
        val compilation = project.multiplatformExtension.jvm().compilations.getByName("main")

        service.registerDependsOnEdge(sourceSetC, sourceSetB)
        service.registerSourceSet(compilation, sourceSetC)

        assertEquals(setOf(sourceSetC, sourceSetB), service.getSourceSetsClosure(compilation))
        assertEquals(setOf(compilation), service.getCompilationsClosure(sourceSetC))
        assertEquals(setOf(compilation), service.getCompilationsClosure(sourceSetB))

        /* Register dependsOn edge in hindsight */
        service.registerDependsOnEdge(sourceSetB, sourceSetA)

        assertEquals(setOf(sourceSetC, sourceSetB, sourceSetA), service.getSourceSetsClosure(compilation))
        assertEquals(setOf(compilation), service.getCompilationsClosure(sourceSetC))
        assertEquals(setOf(compilation), service.getCompilationsClosure(sourceSetB))
        assertEquals(setOf(compilation), service.getCompilationsClosure(sourceSetA))
    }
}
