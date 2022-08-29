/*
 * Copyright 2010-2022 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.gradle.plugin.sources

import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet

internal class KotlinSourceSetRelationRegistryImpl : KotlinSourceSetRelationRegistry, KotlinSourceSetRelationService {

    private val dependsOn = mutableMapOf<KotlinSourceSet, MutableSet<KotlinSourceSet>>()

    private val dependsOnClosure = mutableMapOf<KotlinSourceSet, MutableSet<KotlinSourceSet>>()

    private val reverseDependsOn = mutableMapOf<KotlinSourceSet, MutableSet<KotlinSourceSet>>()

    private val reverseDependsOnClosure = mutableMapOf<KotlinSourceSet, MutableSet<KotlinSourceSet>>()

    private val compilationsBySourceSet = mutableMapOf<KotlinSourceSet, MutableSet<KotlinCompilation<*>>>()

    private val sourceSetsByCompilation = mutableMapOf<KotlinCompilation<*>, MutableSet<KotlinSourceSet>>()


    override fun registerDependsOnEdge(from: KotlinSourceSet, to: KotlinSourceSet) {
        val leftSide = getReverseDependsOnSourceSets(from) + from
        val rightSide = getDependsOnSourceSets(to) + to

        dependsOn.add(from, to)
        reverseDependsOn.add(to, from)

        leftSide.forEach { left ->
            rightSide.forEach { right ->
                dependsOnClosure.add(left, right)
                reverseDependsOnClosure.add(right, left)
            }
        }

        getCompilationsClosure(from).forEach { compilation ->
            rightSide.forEach { right ->
                compilationsBySourceSet.add(right, compilation)
                sourceSetsByCompilation.add(compilation, right)
            }
        }
    }

    override fun registerSourceSet(compilation: KotlinCompilation<*>, sourceSet: KotlinSourceSet) {
        (getDependsOnSourceSetsClosure(sourceSet) + sourceSet).forEach { dependsOnSourceSet ->
            compilationsBySourceSet.add(dependsOnSourceSet, compilation)
            sourceSetsByCompilation.add(compilation, dependsOnSourceSet)
        }
    }

    override fun getDependsOnSourceSets(sourceSet: KotlinSourceSet): Set<KotlinSourceSet> {
        return dependsOn[sourceSet].orEmpty()
    }

    override fun getDependsOnSourceSetsClosure(sourceSet: KotlinSourceSet): Set<KotlinSourceSet> {
        return dependsOnClosure[sourceSet].orEmpty()
    }

    override fun getReverseDependsOnSourceSets(sourceSet: KotlinSourceSet): Set<KotlinSourceSet> {
        return reverseDependsOn[sourceSet].orEmpty()
    }

    override fun getReverseDependsOnSourceSetsClosure(sourceSet: KotlinSourceSet): Set<KotlinSourceSet> {
        return reverseDependsOnClosure[sourceSet].orEmpty()
    }

    override fun getCompilationsClosure(sourceSet: KotlinSourceSet): Set<KotlinCompilation<*>> {
        return compilationsBySourceSet[sourceSet].orEmpty()
    }

    override fun getSourceSetsClosure(compilation: KotlinCompilation<*>): Set<KotlinSourceSet> {
        return sourceSetsByCompilation[compilation].orEmpty()
    }
}

private fun <K, V> MutableMap<K, MutableSet<V>>.add(key: K, value: V): Boolean = getOrPut(key) { mutableSetOf() }.add(value)
