/*
 * Copyright 2010-2022 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

@file:Suppress("FunctionName")

package org.jetbrains.kotlin.gradle.sources

import org.jetbrains.kotlin.gradle.buildProjectWithMPP
import org.jetbrains.kotlin.gradle.dsl.multiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.sources.kotlinSourceSetRelationService
import kotlin.test.Test
import kotlin.test.assertEquals

class KotlinSourceSetRelationServiceFunctionalTest {
    private val project = buildProjectWithMPP()
    private val kotlin = project.multiplatformExtension
    private val service = project.kotlinSourceSetRelationService

    @Test
    fun `test jvm and linux project`() {
        val jvm = kotlin.jvm()
        val linuxX64 = kotlin.linuxX64()
        val linuxArm64 = kotlin.linuxArm64()
        val metadata = kotlin.metadata()

        val commonMain = kotlin.sourceSets.getByName("commonMain")
        val linuxMain = kotlin.sourceSets.create("linuxMain")
        val jvmMain = kotlin.sourceSets.getByName("jvmMain")
        val linuxArm64Main = kotlin.sourceSets.getByName("linuxArm64Main")
        val linuxX64Main = kotlin.sourceSets.getByName("linuxX64Main")

        /* Before setting up linuxMain */
        assertEquals(emptySet(), service.getDependsOnSourceSets(commonMain))
        assertEquals(emptySet(), service.getDependsOnSourceSets(linuxMain))
        assertEquals(setOf(commonMain), service.getDependsOnSourceSets(jvmMain))
        assertEquals(setOf(commonMain), service.getDependsOnSourceSets(linuxArm64Main))
        assertEquals(setOf(commonMain), service.getDependsOnSourceSets(linuxX64Main))

        assertEquals(
            setOf(jvmMain, linuxArm64Main, linuxX64Main), service.getReverseDependsOnSourceSets(commonMain)
        )

        assertEquals(
            setOf(jvmMain, linuxArm64Main, linuxX64Main), service.getReverseDependsOnSourceSetsClosure(commonMain)
        )

        assertEquals(
            setOf(
                metadata.compilations.getByName("main"),
                jvm.compilations.getByName("main"),
                linuxArm64.compilations.getByName("main"),
                linuxX64.compilations.getByName("main"),
            ),
            service.getCompilationsClosure(commonMain)
        )

        assertEquals(setOf(jvm.compilations.getByName("main")), service.getCompilationsClosure(jvmMain))
        assertEquals(setOf(linuxArm64.compilations.getByName("main")), service.getCompilationsClosure(linuxArm64Main))
        assertEquals(setOf(linuxX64.compilations.getByName("main")), service.getCompilationsClosure(linuxX64Main))

        /* Setup linuxMain */

        linuxMain.dependsOn(commonMain)
        linuxArm64Main.dependsOn(linuxMain)
        linuxX64Main.dependsOn(linuxMain)

        /* Assertions after setup */

        assertEquals(emptySet(), service.getDependsOnSourceSets(commonMain))
        assertEquals(setOf(commonMain), service.getDependsOnSourceSets(linuxMain))
        assertEquals(setOf(commonMain), service.getDependsOnSourceSets(jvmMain))
        assertEquals(setOf(commonMain, linuxMain), service.getDependsOnSourceSets(linuxArm64Main))
        assertEquals(setOf(commonMain, linuxMain), service.getDependsOnSourceSets(linuxX64Main))

        assertEquals(
            setOf(linuxMain, jvmMain,  linuxArm64Main, linuxX64Main), service.getReverseDependsOnSourceSets(commonMain)
        )

        assertEquals(
            setOf(linuxMain, jvmMain, linuxArm64Main, linuxX64Main), service.getReverseDependsOnSourceSetsClosure(commonMain)
        )

        assertEquals(
            setOf(
                metadata.compilations.getByName("main"),
                jvm.compilations.getByName("main"),
                linuxArm64.compilations.getByName("main"),
                linuxX64.compilations.getByName("main"),
            ),
            service.getCompilationsClosure(commonMain)
        )

        assertEquals(
            setOf(
                linuxX64.compilations.getByName("main"),
                linuxArm64.compilations.getByName("main")
            ),
            service.getCompilationsClosure(linuxMain)
        )


        assertEquals(setOf(jvm.compilations.getByName("main")), service.getCompilationsClosure(jvmMain))
        assertEquals(setOf(linuxArm64.compilations.getByName("main")), service.getCompilationsClosure(linuxArm64Main))
        assertEquals(setOf(linuxX64.compilations.getByName("main")), service.getCompilationsClosure(linuxX64Main))

        assertEquals(
            setOf(commonMain, linuxMain, linuxX64Main), service.getSourceSetsClosure(linuxX64.compilations.getByName("main"))
        )

        assertEquals(
            setOf(commonMain, linuxMain, linuxArm64Main), service.getSourceSetsClosure(linuxArm64.compilations.getByName("main"))
        )
    }
}
