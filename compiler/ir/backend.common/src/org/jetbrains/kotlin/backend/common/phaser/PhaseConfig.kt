/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.backend.common.phaser

fun CompilerPhase<*, *, *>.toPhaseMap(): MutableMap<String, AnyNamedPhase> =
    getNamedSubphases().fold(mutableMapOf()) { acc, (_, phase) ->
        check(phase.name !in acc) { "Duplicate phase name '${phase.name}'" }
        acc[phase.name] = phase
        acc
    }

class PhaseConfigBuilder(private val compoundPhase: CompilerPhase<*, *, *>) {
    val enabled = mutableSetOf<AnyNamedPhase>()
    val verbose = mutableSetOf<AnyNamedPhase>()
    val toDumpStateBefore = mutableSetOf<AnyNamedPhase>()
    val toDumpStateAfter = mutableSetOf<AnyNamedPhase>()
    var dumpToDirectory: String? = null
    var dumpOnlyFqName: String? = null
    val toValidateStateBefore = mutableSetOf<AnyNamedPhase>()
    val toValidateStateAfter = mutableSetOf<AnyNamedPhase>()
    val namesOfElementsExcludedFromDumping = mutableSetOf<String>()
    var needProfiling = false
    var checkConditions = false
    var checkStickyConditions = false

    fun build() = PhaseConfig(
        compoundPhase, compoundPhase.toPhaseMap(), enabled,
        verbose, toDumpStateBefore, toDumpStateAfter, dumpToDirectory, dumpOnlyFqName,
        toValidateStateBefore, toValidateStateAfter,
        namesOfElementsExcludedFromDumping,
        needProfiling, checkConditions, checkStickyConditions
    )
}

class PhaseConfig(
    private val compoundPhase: CompilerPhase<*, *, *>,
    private val phases: Map<String, AnyNamedPhase> = compoundPhase.toPhaseMap(),
    private val initiallyEnabled: Set<AnyNamedPhase> = phases.values.toSet(),
    val verbose: Set<AnyNamedPhase> = emptySet(),
    val toDumpStateBefore: Set<AnyNamedPhase> = emptySet(),
    val toDumpStateAfter: Set<AnyNamedPhase> = emptySet(),
    val dumpToDirectory: String? = null,
    val dumpOnlyFqName: String? = null,
    val toValidateStateBefore: Set<AnyNamedPhase> = emptySet(),
    val toValidateStateAfter: Set<AnyNamedPhase> = emptySet(),
    val namesOfElementsExcludedFromDumping: Set<String> = emptySet(),
    val needProfiling: Boolean = false,
    val checkConditions: Boolean = false,
    val checkStickyConditions: Boolean = false
) {
    fun toBuilder() = PhaseConfigBuilder(compoundPhase).also {
        it.enabled.addAll(initiallyEnabled)
        it.verbose.addAll(verbose)
        it.toDumpStateBefore.addAll(toDumpStateBefore)
        it.toDumpStateAfter.addAll(toDumpStateAfter)
        it.dumpToDirectory = dumpToDirectory
        it.dumpOnlyFqName = dumpOnlyFqName
        it.toValidateStateBefore.addAll(toValidateStateBefore)
        it.toValidateStateAfter.addAll(toValidateStateAfter)
        it.namesOfElementsExcludedFromDumping.addAll(namesOfElementsExcludedFromDumping)
        it.needProfiling = needProfiling
        it.checkConditions = checkConditions
        it.checkStickyConditions = checkStickyConditions
    }

    private val enabledMut = initiallyEnabled.toMutableSet()

    val enabled: Set<AnyNamedPhase> get() = enabledMut

    fun known(name: String): String {
        if (phases[name] == null) {
            error("Unknown phase: $name. Use -Xlist-phases to see the list of phases.")
        }
        return name
    }

    fun list() {
        compoundPhase.getNamedSubphases().forEach { (depth, phase) ->
            val enabled = if (phase in enabled) "(Enabled)" else ""
            val verbose = if (phase in verbose) "(Verbose)" else ""

            println(String.format("%1$-50s %2$-50s %3$-10s", "${"    ".repeat(depth)}${phase.name}:", phase.description, "$enabled $verbose"))
        }
    }

    fun enable(phase: AnyNamedPhase) {
        enabledMut.add(phase)
    }

    fun disable(phase: AnyNamedPhase) {
        enabledMut.remove(phase)
    }

    fun switch(phase: AnyNamedPhase, onOff: Boolean) {
        if (onOff) {
            enable(phase)
        } else {
            disable(phase)
        }
    }
}
