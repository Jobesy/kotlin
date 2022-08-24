/*
 * Copyright 2010-2022 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.backend.js.utils

import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable

class FqNameExtractor(private val keep: Set<String>) {

    enum class TraverseDirection {
        UP,
        DOWN
    }

    private val keptSignatures: MutableSet<String> = mutableSetOf()

    private val additionalKeep: MutableSet<IrDeclaration> = mutableSetOf()

    fun shouldKeep(declaration: IrDeclaration): Boolean {
        if (declaration in additionalKeep) return true
        return when (declaration) {
            is IrDeclarationContainer -> declaration.declarations.any {
                (it as? IrDeclarationWithName)?.let { shouldKeep(it, null, TraverseDirection.UP) } ?: false
            }

            else -> (declaration as? IrDeclarationWithName)?.let { shouldKeep(it, null, TraverseDirection.UP) } ?: false
        }
    }

    fun shouldKeep(
        declaration: IrDeclarationWithName,
        signature: String?,
        traverseDirection: TraverseDirection
    ): Boolean {
        if (signature in keptSignatures) return true
        if (declaration is IrSimpleFunction) {
            if (declaration.overriddenSymbols.isNotEmpty()) return false
            if (shouldKeepFunction(declaration)) {
                signature?.let { keptSignatures.add(it) }
                return true
            }
        }

        if (isInKeep(declaration)) {
            signature?.let { keptSignatures.add(it) }
            return true
        }

        return when (traverseDirection) {
            TraverseDirection.UP -> {
                (when (val parent = declaration.parent) {
                    is IrDeclarationWithName -> shouldKeep(parent, signature, traverseDirection)
                    else -> false
                }).also {
                    if (it) {
                        signature?.let { keptSignatures.add(it) }
                    }
                }
            }
            TraverseDirection.DOWN -> if (declaration is IrDeclarationContainer) {
                declaration.declarations.any { shouldKeep(it) }
            } else false
        }
    }

    fun additionalKeep(declaration: IrDeclaration) {
        additionalKeep.add(declaration)
    }

    private fun shouldKeepFunction(function: IrSimpleFunction): Boolean {
        val correspondingPropertySymbol = function.correspondingPropertySymbol
            ?: return isInKeep(function)

        return isInKeep(correspondingPropertySymbol.owner)
    }

    private fun isInKeep(declaration: IrDeclarationWithName): Boolean {
        return (declaration.fqNameWhenAvailable?.asString() in keep)
    }
}
