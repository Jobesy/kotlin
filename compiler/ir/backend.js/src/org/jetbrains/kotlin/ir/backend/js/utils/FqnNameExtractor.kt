/*
 * Copyright 2010-2022 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.backend.js.utils

import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable

class FqnNameExtractor(private val keep: Set<String>) {
    fun shouldKeep(declaration: IrDeclarationWithName): Boolean {
        when (declaration) {
            is IrSimpleFunction -> return shouldKeepFunction(declaration)
            is IrField -> return isInKeep(declaration)
        }

        return when (val parent = declaration.parent) {
            is IrDeclarationWithName -> shouldKeep(parent)
            else -> false
        }
    }

    private fun shouldKeepFunction(function: IrSimpleFunction): Boolean {
        val correspondingPropertySymbol = function.correspondingPropertySymbol
            ?: return isInKeep(function)

        return isInKeep(correspondingPropertySymbol.owner)
    }

    private fun isInKeep(declaration: IrDeclarationWithName): Boolean {
        return declaration.fqNameWhenAvailable?.asString() in keep
    }
}
