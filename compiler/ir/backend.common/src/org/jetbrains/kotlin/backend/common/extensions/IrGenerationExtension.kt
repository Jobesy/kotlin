/*
 * Copyright 2010-2018 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.backend.common.extensions

import org.jetbrains.org.objectweb.asm.tree.InsnList
import org.jetbrains.org.objectweb.asm.tree.MethodInsnNode
import org.jetbrains.kotlin.backend.common.BackendContext
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.extensions.ProjectExtensionDescriptor
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.linkage.IrDeserializer
import org.jetbrains.kotlin.ir.symbols.IrFunctionSymbol
import org.jetbrains.kotlin.ir.types.IrType

interface IrGenerationExtension : IrDeserializer.IrLinkerExtension {
    companion object :
        ProjectExtensionDescriptor<IrGenerationExtension>(
            "org.jetbrains.kotlin.irGenerationExtension", IrGenerationExtension::class.java
        )

    fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext)

    // TODO: normal dependency & typing
    fun retrieveIntrinsic(symbol: IrFunctionSymbol): Any? = null


    fun applyPluginDefinedReifiedOperationMarker(
        insn: MethodInsnNode,
        instructions: InsnList,
        type: IrType,
        jvmBackendContext: BackendContext,
    ): Int = -1
}
