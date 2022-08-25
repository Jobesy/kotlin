/*
 * Copyright 2010-2022 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.backend.js.utils

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor

class Keeper(private val keep: Set<String>) : IrElementVisitor<Unit, Keeper.KeepData> {
    private val keptDeclarations: MutableSet<IrDeclaration> = mutableSetOf()

    fun shouldKeep(declaration: IrDeclaration): Boolean {
        return declaration in keptDeclarations
    }

    override fun visitElement(element: IrElement, data: KeepData) {
        element.acceptChildren(this, data)
    }

    override fun visitClass(declaration: IrClass, data: KeepData) {
        val prevShouldBeKept = data.classShouldBeKept
        val prevClassInKeep = data.classInKeep
        data.classShouldBeKept = false
        val keptClass = data.classInKeep || isInKeep(declaration)
        if (keptClass) {
            keptDeclarations.add(declaration)
        }
        data.classInKeep = keptClass
        super.visitClass(declaration, data)
        if (data.classShouldBeKept) {
            keptDeclarations.add(declaration)
        }
        data.classShouldBeKept = prevShouldBeKept
        data.classInKeep = prevClassInKeep
    }

    override fun visitDeclaration(declaration: IrDeclarationBase, data: KeepData) {
        super.visitDeclaration(declaration, data)
        if (declaration in keptDeclarations) {
            return
        }
        if (declaration is IrDeclarationWithName && isInKeep(declaration) || data.classInKeep) {
            keptDeclarations.add(declaration)
            data.classShouldBeKept = true
            return
        }
    }

    private fun isInKeep(declaration: IrDeclarationWithName): Boolean {
        return declaration.fqNameWhenAvailable?.asString() in keep
    }

    class KeepData(var classInKeep: Boolean, var classShouldBeKept: Boolean)
}
