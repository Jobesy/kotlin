/*
 * Copyright 2010-2022 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.visitors

import org.jetbrains.kotlin.fir.FirElement
import org.jetbrains.kotlin.fir.FirAnnotationContainer
import org.jetbrains.kotlin.fir.types.FirTypeRef
import org.jetbrains.kotlin.fir.references.FirReference
import org.jetbrains.kotlin.fir.FirLabel
import org.jetbrains.kotlin.fir.expressions.FirResolvable
import org.jetbrains.kotlin.fir.FirTargetElement
import org.jetbrains.kotlin.fir.declarations.FirDeclarationStatus
import org.jetbrains.kotlin.fir.declarations.FirResolvedDeclarationStatus
import org.jetbrains.kotlin.fir.declarations.FirControlFlowGraphOwner
import org.jetbrains.kotlin.fir.expressions.FirStatement
import org.jetbrains.kotlin.fir.expressions.FirExpression
import org.jetbrains.kotlin.fir.declarations.FirContextReceiver
import org.jetbrains.kotlin.fir.declarations.FirDeclaration
import org.jetbrains.kotlin.fir.declarations.FirTypeParameterRefsOwner
import org.jetbrains.kotlin.fir.declarations.FirTypeParametersOwner
import org.jetbrains.kotlin.fir.declarations.FirMemberDeclaration
import org.jetbrains.kotlin.fir.declarations.FirAnonymousInitializer
import org.jetbrains.kotlin.fir.declarations.FirCallableDeclaration
import org.jetbrains.kotlin.fir.declarations.FirTypeParameterRef
import org.jetbrains.kotlin.fir.declarations.FirTypeParameter
import org.jetbrains.kotlin.fir.declarations.FirVariable
import org.jetbrains.kotlin.fir.declarations.FirValueParameter
import org.jetbrains.kotlin.fir.declarations.FirProperty
import org.jetbrains.kotlin.fir.declarations.FirField
import org.jetbrains.kotlin.fir.declarations.FirEnumEntry
import org.jetbrains.kotlin.fir.declarations.FirClassLikeDeclaration
import org.jetbrains.kotlin.fir.declarations.FirClass
import org.jetbrains.kotlin.fir.declarations.FirRegularClass
import org.jetbrains.kotlin.fir.declarations.FirTypeAlias
import org.jetbrains.kotlin.fir.declarations.FirFunction
import org.jetbrains.kotlin.fir.declarations.FirContractDescriptionOwner
import org.jetbrains.kotlin.fir.declarations.FirSimpleFunction
import org.jetbrains.kotlin.fir.declarations.FirPropertyAccessor
import org.jetbrains.kotlin.fir.declarations.FirBackingField
import org.jetbrains.kotlin.fir.declarations.FirConstructor
import org.jetbrains.kotlin.fir.declarations.FirFile
import org.jetbrains.kotlin.fir.FirPackageDirective
import org.jetbrains.kotlin.fir.declarations.FirAnonymousFunction
import org.jetbrains.kotlin.fir.expressions.FirAnonymousFunctionExpression
import org.jetbrains.kotlin.fir.declarations.FirAnonymousObject
import org.jetbrains.kotlin.fir.expressions.FirAnonymousObjectExpression
import org.jetbrains.kotlin.fir.diagnostics.FirDiagnosticHolder
import org.jetbrains.kotlin.fir.declarations.FirImport
import org.jetbrains.kotlin.fir.declarations.FirResolvedImport
import org.jetbrains.kotlin.fir.declarations.FirErrorImport
import org.jetbrains.kotlin.fir.expressions.FirLoop
import org.jetbrains.kotlin.fir.expressions.FirErrorLoop
import org.jetbrains.kotlin.fir.expressions.FirDoWhileLoop
import org.jetbrains.kotlin.fir.expressions.FirWhileLoop
import org.jetbrains.kotlin.fir.expressions.FirBlock
import org.jetbrains.kotlin.fir.expressions.FirBinaryLogicExpression
import org.jetbrains.kotlin.fir.expressions.FirJump
import org.jetbrains.kotlin.fir.expressions.FirLoopJump
import org.jetbrains.kotlin.fir.expressions.FirBreakExpression
import org.jetbrains.kotlin.fir.expressions.FirContinueExpression
import org.jetbrains.kotlin.fir.expressions.FirCatch
import org.jetbrains.kotlin.fir.expressions.FirTryExpression
import org.jetbrains.kotlin.fir.expressions.FirConstExpression
import org.jetbrains.kotlin.fir.types.FirTypeProjection
import org.jetbrains.kotlin.fir.types.FirStarProjection
import org.jetbrains.kotlin.fir.types.FirPlaceholderProjection
import org.jetbrains.kotlin.fir.types.FirTypeProjectionWithVariance
import org.jetbrains.kotlin.fir.expressions.FirArgumentList
import org.jetbrains.kotlin.fir.expressions.FirCall
import org.jetbrains.kotlin.fir.expressions.FirAnnotation
import org.jetbrains.kotlin.fir.expressions.FirAnnotationCall
import org.jetbrains.kotlin.fir.expressions.FirAnnotationArgumentMapping
import org.jetbrains.kotlin.fir.expressions.FirComparisonExpression
import org.jetbrains.kotlin.fir.expressions.FirTypeOperatorCall
import org.jetbrains.kotlin.fir.expressions.FirAssignmentOperatorStatement
import org.jetbrains.kotlin.fir.expressions.FirEqualityOperatorCall
import org.jetbrains.kotlin.fir.expressions.FirWhenExpression
import org.jetbrains.kotlin.fir.expressions.FirWhenBranch
import org.jetbrains.kotlin.fir.expressions.FirContextReceiverArgumentListOwner
import org.jetbrains.kotlin.fir.expressions.FirQualifiedAccess
import org.jetbrains.kotlin.fir.expressions.FirCheckNotNullCall
import org.jetbrains.kotlin.fir.expressions.FirElvisExpression
import org.jetbrains.kotlin.fir.expressions.FirArrayOfCall
import org.jetbrains.kotlin.fir.expressions.FirAugmentedArraySetCall
import org.jetbrains.kotlin.fir.expressions.FirClassReferenceExpression
import org.jetbrains.kotlin.fir.expressions.FirErrorExpression
import org.jetbrains.kotlin.fir.declarations.FirErrorFunction
import org.jetbrains.kotlin.fir.declarations.FirErrorProperty
import org.jetbrains.kotlin.fir.expressions.FirQualifiedAccessExpression
import org.jetbrains.kotlin.fir.expressions.FirPropertyAccessExpression
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.expressions.FirIntegerLiteralOperatorCall
import org.jetbrains.kotlin.fir.expressions.FirImplicitInvokeCall
import org.jetbrains.kotlin.fir.expressions.FirDelegatedConstructorCall
import org.jetbrains.kotlin.fir.expressions.FirComponentCall
import org.jetbrains.kotlin.fir.expressions.FirCallableReferenceAccess
import org.jetbrains.kotlin.fir.expressions.FirThisReceiverExpression
import org.jetbrains.kotlin.fir.expressions.FirSmartCastExpression
import org.jetbrains.kotlin.fir.expressions.FirSafeCallExpression
import org.jetbrains.kotlin.fir.expressions.FirCheckedSafeCallSubject
import org.jetbrains.kotlin.fir.expressions.FirGetClassCall
import org.jetbrains.kotlin.fir.expressions.FirWrappedExpression
import org.jetbrains.kotlin.fir.expressions.FirWrappedArgumentExpression
import org.jetbrains.kotlin.fir.expressions.FirLambdaArgumentExpression
import org.jetbrains.kotlin.fir.expressions.FirSpreadArgumentExpression
import org.jetbrains.kotlin.fir.expressions.FirNamedArgumentExpression
import org.jetbrains.kotlin.fir.expressions.FirVarargArgumentsExpression
import org.jetbrains.kotlin.fir.expressions.FirResolvedQualifier
import org.jetbrains.kotlin.fir.expressions.FirErrorResolvedQualifier
import org.jetbrains.kotlin.fir.expressions.FirResolvedReifiedParameterReference
import org.jetbrains.kotlin.fir.expressions.FirReturnExpression
import org.jetbrains.kotlin.fir.expressions.FirStringConcatenationCall
import org.jetbrains.kotlin.fir.expressions.FirThrowExpression
import org.jetbrains.kotlin.fir.expressions.FirVariableAssignment
import org.jetbrains.kotlin.fir.expressions.FirWhenSubjectExpression
import org.jetbrains.kotlin.fir.expressions.FirWrappedDelegateExpression
import org.jetbrains.kotlin.fir.references.FirNamedReference
import org.jetbrains.kotlin.fir.references.FirErrorNamedReference
import org.jetbrains.kotlin.fir.references.FirSuperReference
import org.jetbrains.kotlin.fir.references.FirThisReference
import org.jetbrains.kotlin.fir.references.FirControlFlowGraphReference
import org.jetbrains.kotlin.fir.references.FirResolvedNamedReference
import org.jetbrains.kotlin.fir.references.FirDelegateFieldReference
import org.jetbrains.kotlin.fir.references.FirBackingFieldReference
import org.jetbrains.kotlin.fir.references.FirResolvedCallableReference
import org.jetbrains.kotlin.fir.types.FirResolvedTypeRef
import org.jetbrains.kotlin.fir.types.FirErrorTypeRef
import org.jetbrains.kotlin.fir.types.FirTypeRefWithNullability
import org.jetbrains.kotlin.fir.types.FirUserTypeRef
import org.jetbrains.kotlin.fir.types.FirDynamicTypeRef
import org.jetbrains.kotlin.fir.types.FirFunctionTypeRef
import org.jetbrains.kotlin.fir.types.FirIntersectionTypeRef
import org.jetbrains.kotlin.fir.types.FirImplicitTypeRef
import org.jetbrains.kotlin.fir.contracts.FirEffectDeclaration
import org.jetbrains.kotlin.fir.contracts.FirContractDescription
import org.jetbrains.kotlin.fir.contracts.FirLegacyRawContractDescription
import org.jetbrains.kotlin.fir.contracts.FirRawContractDescription
import org.jetbrains.kotlin.fir.contracts.FirResolvedContractDescription

/*
 * This file was generated automatically
 * DO NOT MODIFY IT MANUALLY
 */

abstract class FirVisitorVoid : FirVisitor<Unit, Nothing?>() {
    abstract fun visitElement(element: FirElement)

    open fun visitAnnotationContainer(annotationContainer: FirAnnotationContainer) {
        visitElement(annotationContainer)
    }

    open fun visitTypeRef(typeRef: FirTypeRef) {
        visitElement(typeRef)
    }

    open fun visitReference(reference: FirReference) {
        visitElement(reference)
    }

    open fun visitLabel(label: FirLabel) {
        visitElement(label)
    }

    open fun visitResolvable(resolvable: FirResolvable) {
        visitElement(resolvable)
    }

    open fun visitTargetElement(targetElement: FirTargetElement) {
        visitElement(targetElement)
    }

    open fun visitDeclarationStatus(declarationStatus: FirDeclarationStatus) {
        visitElement(declarationStatus)
    }

    open fun visitResolvedDeclarationStatus(resolvedDeclarationStatus: FirResolvedDeclarationStatus) {
        visitElement(resolvedDeclarationStatus)
    }

    open fun visitControlFlowGraphOwner(controlFlowGraphOwner: FirControlFlowGraphOwner) {
        visitElement(controlFlowGraphOwner)
    }

    open fun visitStatement(statement: FirStatement) {
        visitElement(statement)
    }

    open fun visitExpression(expression: FirExpression) {
        visitElement(expression)
    }

    open fun visitContextReceiver(contextReceiver: FirContextReceiver) {
        visitElement(contextReceiver)
    }

    open fun visitDeclaration(declaration: FirDeclaration) {
        visitElement(declaration)
    }

    open fun visitTypeParameterRefsOwner(typeParameterRefsOwner: FirTypeParameterRefsOwner) {
        visitElement(typeParameterRefsOwner)
    }

    open fun visitTypeParametersOwner(typeParametersOwner: FirTypeParametersOwner) {
        visitElement(typeParametersOwner)
    }

    open fun visitMemberDeclaration(memberDeclaration: FirMemberDeclaration) {
        visitElement(memberDeclaration)
    }

    open fun visitAnonymousInitializer(anonymousInitializer: FirAnonymousInitializer) {
        visitElement(anonymousInitializer)
    }

    open fun visitCallableDeclaration(callableDeclaration: FirCallableDeclaration) {
        visitElement(callableDeclaration)
    }

    open fun visitTypeParameterRef(typeParameterRef: FirTypeParameterRef) {
        visitElement(typeParameterRef)
    }

    open fun visitTypeParameter(typeParameter: FirTypeParameter) {
        visitElement(typeParameter)
    }

    open fun visitVariable(variable: FirVariable) {
        visitElement(variable)
    }

    open fun visitValueParameter(valueParameter: FirValueParameter) {
        visitElement(valueParameter)
    }

    open fun visitProperty(property: FirProperty) {
        visitElement(property)
    }

    open fun visitField(field: FirField) {
        visitElement(field)
    }

    open fun visitEnumEntry(enumEntry: FirEnumEntry) {
        visitElement(enumEntry)
    }

    open fun visitClassLikeDeclaration(classLikeDeclaration: FirClassLikeDeclaration) {
        visitElement(classLikeDeclaration)
    }

    open fun visitClass(klass: FirClass) {
        visitElement(klass)
    }

    open fun visitRegularClass(regularClass: FirRegularClass) {
        visitElement(regularClass)
    }

    open fun visitTypeAlias(typeAlias: FirTypeAlias) {
        visitElement(typeAlias)
    }

    open fun visitFunction(function: FirFunction) {
        visitElement(function)
    }

    open fun visitContractDescriptionOwner(contractDescriptionOwner: FirContractDescriptionOwner) {
        visitElement(contractDescriptionOwner)
    }

    open fun visitSimpleFunction(simpleFunction: FirSimpleFunction) {
        visitElement(simpleFunction)
    }

    open fun visitPropertyAccessor(propertyAccessor: FirPropertyAccessor) {
        visitElement(propertyAccessor)
    }

    open fun visitBackingField(backingField: FirBackingField) {
        visitElement(backingField)
    }

    open fun visitConstructor(constructor: FirConstructor) {
        visitElement(constructor)
    }

    open fun visitFile(file: FirFile) {
        visitElement(file)
    }

    open fun visitPackageDirective(packageDirective: FirPackageDirective) {
        visitElement(packageDirective)
    }

    open fun visitAnonymousFunction(anonymousFunction: FirAnonymousFunction) {
        visitElement(anonymousFunction)
    }

    open fun visitAnonymousFunctionExpression(anonymousFunctionExpression: FirAnonymousFunctionExpression) {
        visitElement(anonymousFunctionExpression)
    }

    open fun visitAnonymousObject(anonymousObject: FirAnonymousObject) {
        visitElement(anonymousObject)
    }

    open fun visitAnonymousObjectExpression(anonymousObjectExpression: FirAnonymousObjectExpression) {
        visitElement(anonymousObjectExpression)
    }

    open fun visitDiagnosticHolder(diagnosticHolder: FirDiagnosticHolder) {
        visitElement(diagnosticHolder)
    }

    open fun visitImport(import: FirImport) {
        visitElement(import)
    }

    open fun visitResolvedImport(resolvedImport: FirResolvedImport) {
        visitElement(resolvedImport)
    }

    open fun visitErrorImport(errorImport: FirErrorImport) {
        visitElement(errorImport)
    }

    open fun visitLoop(loop: FirLoop) {
        visitElement(loop)
    }

    open fun visitErrorLoop(errorLoop: FirErrorLoop) {
        visitElement(errorLoop)
    }

    open fun visitDoWhileLoop(doWhileLoop: FirDoWhileLoop) {
        visitElement(doWhileLoop)
    }

    open fun visitWhileLoop(whileLoop: FirWhileLoop) {
        visitElement(whileLoop)
    }

    open fun visitBlock(block: FirBlock) {
        visitElement(block)
    }

    open fun visitBinaryLogicExpression(binaryLogicExpression: FirBinaryLogicExpression) {
        visitElement(binaryLogicExpression)
    }

    open fun <E : FirTargetElement> visitJump(jump: FirJump<E>) {
        visitElement(jump)
    }

    open fun visitLoopJump(loopJump: FirLoopJump) {
        visitElement(loopJump)
    }

    open fun visitBreakExpression(breakExpression: FirBreakExpression) {
        visitElement(breakExpression)
    }

    open fun visitContinueExpression(continueExpression: FirContinueExpression) {
        visitElement(continueExpression)
    }

    open fun visitCatch(catch: FirCatch) {
        visitElement(catch)
    }

    open fun visitTryExpression(tryExpression: FirTryExpression) {
        visitElement(tryExpression)
    }

    open fun <T> visitConstExpression(constExpression: FirConstExpression<T>) {
        visitElement(constExpression)
    }

    open fun visitTypeProjection(typeProjection: FirTypeProjection) {
        visitElement(typeProjection)
    }

    open fun visitStarProjection(starProjection: FirStarProjection) {
        visitElement(starProjection)
    }

    open fun visitPlaceholderProjection(placeholderProjection: FirPlaceholderProjection) {
        visitElement(placeholderProjection)
    }

    open fun visitTypeProjectionWithVariance(typeProjectionWithVariance: FirTypeProjectionWithVariance) {
        visitElement(typeProjectionWithVariance)
    }

    open fun visitArgumentList(argumentList: FirArgumentList) {
        visitElement(argumentList)
    }

    open fun visitCall(call: FirCall) {
        visitElement(call)
    }

    open fun visitAnnotation(annotation: FirAnnotation) {
        visitElement(annotation)
    }

    open fun visitAnnotationCall(annotationCall: FirAnnotationCall) {
        visitElement(annotationCall)
    }

    open fun visitAnnotationArgumentMapping(annotationArgumentMapping: FirAnnotationArgumentMapping) {
        visitElement(annotationArgumentMapping)
    }

    open fun visitComparisonExpression(comparisonExpression: FirComparisonExpression) {
        visitElement(comparisonExpression)
    }

    open fun visitTypeOperatorCall(typeOperatorCall: FirTypeOperatorCall) {
        visitElement(typeOperatorCall)
    }

    open fun visitAssignmentOperatorStatement(assignmentOperatorStatement: FirAssignmentOperatorStatement) {
        visitElement(assignmentOperatorStatement)
    }

    open fun visitEqualityOperatorCall(equalityOperatorCall: FirEqualityOperatorCall) {
        visitElement(equalityOperatorCall)
    }

    open fun visitWhenExpression(whenExpression: FirWhenExpression) {
        visitElement(whenExpression)
    }

    open fun visitWhenBranch(whenBranch: FirWhenBranch) {
        visitElement(whenBranch)
    }

    open fun visitContextReceiverArgumentListOwner(contextReceiverArgumentListOwner: FirContextReceiverArgumentListOwner) {
        visitElement(contextReceiverArgumentListOwner)
    }

    open fun visitQualifiedAccess(qualifiedAccess: FirQualifiedAccess) {
        visitElement(qualifiedAccess)
    }

    open fun visitCheckNotNullCall(checkNotNullCall: FirCheckNotNullCall) {
        visitElement(checkNotNullCall)
    }

    open fun visitElvisExpression(elvisExpression: FirElvisExpression) {
        visitElement(elvisExpression)
    }

    open fun visitArrayOfCall(arrayOfCall: FirArrayOfCall) {
        visitElement(arrayOfCall)
    }

    open fun visitAugmentedArraySetCall(augmentedArraySetCall: FirAugmentedArraySetCall) {
        visitElement(augmentedArraySetCall)
    }

    open fun visitClassReferenceExpression(classReferenceExpression: FirClassReferenceExpression) {
        visitElement(classReferenceExpression)
    }

    open fun visitErrorExpression(errorExpression: FirErrorExpression) {
        visitElement(errorExpression)
    }

    open fun visitErrorFunction(errorFunction: FirErrorFunction) {
        visitElement(errorFunction)
    }

    open fun visitErrorProperty(errorProperty: FirErrorProperty) {
        visitElement(errorProperty)
    }

    open fun visitQualifiedAccessExpression(qualifiedAccessExpression: FirQualifiedAccessExpression) {
        visitElement(qualifiedAccessExpression)
    }

    open fun visitPropertyAccessExpression(propertyAccessExpression: FirPropertyAccessExpression) {
        visitElement(propertyAccessExpression)
    }

    open fun visitFunctionCall(functionCall: FirFunctionCall) {
        visitElement(functionCall)
    }

    open fun visitIntegerLiteralOperatorCall(integerLiteralOperatorCall: FirIntegerLiteralOperatorCall) {
        visitElement(integerLiteralOperatorCall)
    }

    open fun visitImplicitInvokeCall(implicitInvokeCall: FirImplicitInvokeCall) {
        visitElement(implicitInvokeCall)
    }

    open fun visitDelegatedConstructorCall(delegatedConstructorCall: FirDelegatedConstructorCall) {
        visitElement(delegatedConstructorCall)
    }

    open fun visitComponentCall(componentCall: FirComponentCall) {
        visitElement(componentCall)
    }

    open fun visitCallableReferenceAccess(callableReferenceAccess: FirCallableReferenceAccess) {
        visitElement(callableReferenceAccess)
    }

    open fun visitThisReceiverExpression(thisReceiverExpression: FirThisReceiverExpression) {
        visitElement(thisReceiverExpression)
    }

    open fun visitSmartCastExpression(smartCastExpression: FirSmartCastExpression) {
        visitElement(smartCastExpression)
    }

    open fun visitSafeCallExpression(safeCallExpression: FirSafeCallExpression) {
        visitElement(safeCallExpression)
    }

    open fun visitCheckedSafeCallSubject(checkedSafeCallSubject: FirCheckedSafeCallSubject) {
        visitElement(checkedSafeCallSubject)
    }

    open fun visitGetClassCall(getClassCall: FirGetClassCall) {
        visitElement(getClassCall)
    }

    open fun visitWrappedExpression(wrappedExpression: FirWrappedExpression) {
        visitElement(wrappedExpression)
    }

    open fun visitWrappedArgumentExpression(wrappedArgumentExpression: FirWrappedArgumentExpression) {
        visitElement(wrappedArgumentExpression)
    }

    open fun visitLambdaArgumentExpression(lambdaArgumentExpression: FirLambdaArgumentExpression) {
        visitElement(lambdaArgumentExpression)
    }

    open fun visitSpreadArgumentExpression(spreadArgumentExpression: FirSpreadArgumentExpression) {
        visitElement(spreadArgumentExpression)
    }

    open fun visitNamedArgumentExpression(namedArgumentExpression: FirNamedArgumentExpression) {
        visitElement(namedArgumentExpression)
    }

    open fun visitVarargArgumentsExpression(varargArgumentsExpression: FirVarargArgumentsExpression) {
        visitElement(varargArgumentsExpression)
    }

    open fun visitResolvedQualifier(resolvedQualifier: FirResolvedQualifier) {
        visitElement(resolvedQualifier)
    }

    open fun visitErrorResolvedQualifier(errorResolvedQualifier: FirErrorResolvedQualifier) {
        visitElement(errorResolvedQualifier)
    }

    open fun visitResolvedReifiedParameterReference(resolvedReifiedParameterReference: FirResolvedReifiedParameterReference) {
        visitElement(resolvedReifiedParameterReference)
    }

    open fun visitReturnExpression(returnExpression: FirReturnExpression) {
        visitElement(returnExpression)
    }

    open fun visitStringConcatenationCall(stringConcatenationCall: FirStringConcatenationCall) {
        visitElement(stringConcatenationCall)
    }

    open fun visitThrowExpression(throwExpression: FirThrowExpression) {
        visitElement(throwExpression)
    }

    open fun visitVariableAssignment(variableAssignment: FirVariableAssignment) {
        visitElement(variableAssignment)
    }

    open fun visitWhenSubjectExpression(whenSubjectExpression: FirWhenSubjectExpression) {
        visitElement(whenSubjectExpression)
    }

    open fun visitWrappedDelegateExpression(wrappedDelegateExpression: FirWrappedDelegateExpression) {
        visitElement(wrappedDelegateExpression)
    }

    open fun visitNamedReference(namedReference: FirNamedReference) {
        visitElement(namedReference)
    }

    open fun visitErrorNamedReference(errorNamedReference: FirErrorNamedReference) {
        visitElement(errorNamedReference)
    }

    open fun visitSuperReference(superReference: FirSuperReference) {
        visitElement(superReference)
    }

    open fun visitThisReference(thisReference: FirThisReference) {
        visitElement(thisReference)
    }

    open fun visitControlFlowGraphReference(controlFlowGraphReference: FirControlFlowGraphReference) {
        visitElement(controlFlowGraphReference)
    }

    open fun visitResolvedNamedReference(resolvedNamedReference: FirResolvedNamedReference) {
        visitElement(resolvedNamedReference)
    }

    open fun visitDelegateFieldReference(delegateFieldReference: FirDelegateFieldReference) {
        visitElement(delegateFieldReference)
    }

    open fun visitBackingFieldReference(backingFieldReference: FirBackingFieldReference) {
        visitElement(backingFieldReference)
    }

    open fun visitResolvedCallableReference(resolvedCallableReference: FirResolvedCallableReference) {
        visitElement(resolvedCallableReference)
    }

    open fun visitResolvedTypeRef(resolvedTypeRef: FirResolvedTypeRef) {
        visitElement(resolvedTypeRef)
    }

    open fun visitErrorTypeRef(errorTypeRef: FirErrorTypeRef) {
        visitElement(errorTypeRef)
    }

    open fun visitTypeRefWithNullability(typeRefWithNullability: FirTypeRefWithNullability) {
        visitElement(typeRefWithNullability)
    }

    open fun visitUserTypeRef(userTypeRef: FirUserTypeRef) {
        visitElement(userTypeRef)
    }

    open fun visitDynamicTypeRef(dynamicTypeRef: FirDynamicTypeRef) {
        visitElement(dynamicTypeRef)
    }

    open fun visitFunctionTypeRef(functionTypeRef: FirFunctionTypeRef) {
        visitElement(functionTypeRef)
    }

    open fun visitIntersectionTypeRef(intersectionTypeRef: FirIntersectionTypeRef) {
        visitElement(intersectionTypeRef)
    }

    open fun visitImplicitTypeRef(implicitTypeRef: FirImplicitTypeRef) {
        visitElement(implicitTypeRef)
    }

    open fun visitEffectDeclaration(effectDeclaration: FirEffectDeclaration) {
        visitElement(effectDeclaration)
    }

    open fun visitContractDescription(contractDescription: FirContractDescription) {
        visitElement(contractDescription)
    }

    open fun visitLegacyRawContractDescription(legacyRawContractDescription: FirLegacyRawContractDescription) {
        visitElement(legacyRawContractDescription)
    }

    open fun visitRawContractDescription(rawContractDescription: FirRawContractDescription) {
        visitElement(rawContractDescription)
    }

    open fun visitResolvedContractDescription(resolvedContractDescription: FirResolvedContractDescription) {
        visitElement(resolvedContractDescription)
    }

    final override fun visitElement(element: FirElement, data: Nothing?) {
        visitElement(element)
    }

    final override fun visitAnnotationContainer(annotationContainer: FirAnnotationContainer, data: Nothing?) {
        visitAnnotationContainer(annotationContainer)
    }

    final override fun visitTypeRef(typeRef: FirTypeRef, data: Nothing?) {
        visitTypeRef(typeRef)
    }

    final override fun visitReference(reference: FirReference, data: Nothing?) {
        visitReference(reference)
    }

    final override fun visitLabel(label: FirLabel, data: Nothing?) {
        visitLabel(label)
    }

    final override fun visitResolvable(resolvable: FirResolvable, data: Nothing?) {
        visitResolvable(resolvable)
    }

    final override fun visitTargetElement(targetElement: FirTargetElement, data: Nothing?) {
        visitTargetElement(targetElement)
    }

    final override fun visitDeclarationStatus(declarationStatus: FirDeclarationStatus, data: Nothing?) {
        visitDeclarationStatus(declarationStatus)
    }

    final override fun visitResolvedDeclarationStatus(resolvedDeclarationStatus: FirResolvedDeclarationStatus, data: Nothing?) {
        visitResolvedDeclarationStatus(resolvedDeclarationStatus)
    }

    final override fun visitControlFlowGraphOwner(controlFlowGraphOwner: FirControlFlowGraphOwner, data: Nothing?) {
        visitControlFlowGraphOwner(controlFlowGraphOwner)
    }

    final override fun visitStatement(statement: FirStatement, data: Nothing?) {
        visitStatement(statement)
    }

    final override fun visitExpression(expression: FirExpression, data: Nothing?) {
        visitExpression(expression)
    }

    final override fun visitContextReceiver(contextReceiver: FirContextReceiver, data: Nothing?) {
        visitContextReceiver(contextReceiver)
    }

    final override fun visitDeclaration(declaration: FirDeclaration, data: Nothing?) {
        visitDeclaration(declaration)
    }

    final override fun visitTypeParameterRefsOwner(typeParameterRefsOwner: FirTypeParameterRefsOwner, data: Nothing?) {
        visitTypeParameterRefsOwner(typeParameterRefsOwner)
    }

    final override fun visitTypeParametersOwner(typeParametersOwner: FirTypeParametersOwner, data: Nothing?) {
        visitTypeParametersOwner(typeParametersOwner)
    }

    final override fun visitMemberDeclaration(memberDeclaration: FirMemberDeclaration, data: Nothing?) {
        visitMemberDeclaration(memberDeclaration)
    }

    final override fun visitAnonymousInitializer(anonymousInitializer: FirAnonymousInitializer, data: Nothing?) {
        visitAnonymousInitializer(anonymousInitializer)
    }

    final override fun visitCallableDeclaration(callableDeclaration: FirCallableDeclaration, data: Nothing?) {
        visitCallableDeclaration(callableDeclaration)
    }

    final override fun visitTypeParameterRef(typeParameterRef: FirTypeParameterRef, data: Nothing?) {
        visitTypeParameterRef(typeParameterRef)
    }

    final override fun visitTypeParameter(typeParameter: FirTypeParameter, data: Nothing?) {
        visitTypeParameter(typeParameter)
    }

    final override fun visitVariable(variable: FirVariable, data: Nothing?) {
        visitVariable(variable)
    }

    final override fun visitValueParameter(valueParameter: FirValueParameter, data: Nothing?) {
        visitValueParameter(valueParameter)
    }

    final override fun visitProperty(property: FirProperty, data: Nothing?) {
        visitProperty(property)
    }

    final override fun visitField(field: FirField, data: Nothing?) {
        visitField(field)
    }

    final override fun visitEnumEntry(enumEntry: FirEnumEntry, data: Nothing?) {
        visitEnumEntry(enumEntry)
    }

    final override fun visitClassLikeDeclaration(classLikeDeclaration: FirClassLikeDeclaration, data: Nothing?) {
        visitClassLikeDeclaration(classLikeDeclaration)
    }

    final override fun visitClass(klass: FirClass, data: Nothing?) {
        visitClass(klass)
    }

    final override fun visitRegularClass(regularClass: FirRegularClass, data: Nothing?) {
        visitRegularClass(regularClass)
    }

    final override fun visitTypeAlias(typeAlias: FirTypeAlias, data: Nothing?) {
        visitTypeAlias(typeAlias)
    }

    final override fun visitFunction(function: FirFunction, data: Nothing?) {
        visitFunction(function)
    }

    final override fun visitContractDescriptionOwner(contractDescriptionOwner: FirContractDescriptionOwner, data: Nothing?) {
        visitContractDescriptionOwner(contractDescriptionOwner)
    }

    final override fun visitSimpleFunction(simpleFunction: FirSimpleFunction, data: Nothing?) {
        visitSimpleFunction(simpleFunction)
    }

    final override fun visitPropertyAccessor(propertyAccessor: FirPropertyAccessor, data: Nothing?) {
        visitPropertyAccessor(propertyAccessor)
    }

    final override fun visitBackingField(backingField: FirBackingField, data: Nothing?) {
        visitBackingField(backingField)
    }

    final override fun visitConstructor(constructor: FirConstructor, data: Nothing?) {
        visitConstructor(constructor)
    }

    final override fun visitFile(file: FirFile, data: Nothing?) {
        visitFile(file)
    }

    final override fun visitPackageDirective(packageDirective: FirPackageDirective, data: Nothing?) {
        visitPackageDirective(packageDirective)
    }

    final override fun visitAnonymousFunction(anonymousFunction: FirAnonymousFunction, data: Nothing?) {
        visitAnonymousFunction(anonymousFunction)
    }

    final override fun visitAnonymousFunctionExpression(anonymousFunctionExpression: FirAnonymousFunctionExpression, data: Nothing?) {
        visitAnonymousFunctionExpression(anonymousFunctionExpression)
    }

    final override fun visitAnonymousObject(anonymousObject: FirAnonymousObject, data: Nothing?) {
        visitAnonymousObject(anonymousObject)
    }

    final override fun visitAnonymousObjectExpression(anonymousObjectExpression: FirAnonymousObjectExpression, data: Nothing?) {
        visitAnonymousObjectExpression(anonymousObjectExpression)
    }

    final override fun visitDiagnosticHolder(diagnosticHolder: FirDiagnosticHolder, data: Nothing?) {
        visitDiagnosticHolder(diagnosticHolder)
    }

    final override fun visitImport(import: FirImport, data: Nothing?) {
        visitImport(import)
    }

    final override fun visitResolvedImport(resolvedImport: FirResolvedImport, data: Nothing?) {
        visitResolvedImport(resolvedImport)
    }

    final override fun visitErrorImport(errorImport: FirErrorImport, data: Nothing?) {
        visitErrorImport(errorImport)
    }

    final override fun visitLoop(loop: FirLoop, data: Nothing?) {
        visitLoop(loop)
    }

    final override fun visitErrorLoop(errorLoop: FirErrorLoop, data: Nothing?) {
        visitErrorLoop(errorLoop)
    }

    final override fun visitDoWhileLoop(doWhileLoop: FirDoWhileLoop, data: Nothing?) {
        visitDoWhileLoop(doWhileLoop)
    }

    final override fun visitWhileLoop(whileLoop: FirWhileLoop, data: Nothing?) {
        visitWhileLoop(whileLoop)
    }

    final override fun visitBlock(block: FirBlock, data: Nothing?) {
        visitBlock(block)
    }

    final override fun visitBinaryLogicExpression(binaryLogicExpression: FirBinaryLogicExpression, data: Nothing?) {
        visitBinaryLogicExpression(binaryLogicExpression)
    }

    final override fun <E : FirTargetElement> visitJump(jump: FirJump<E>, data: Nothing?) {
        visitJump(jump)
    }

    final override fun visitLoopJump(loopJump: FirLoopJump, data: Nothing?) {
        visitLoopJump(loopJump)
    }

    final override fun visitBreakExpression(breakExpression: FirBreakExpression, data: Nothing?) {
        visitBreakExpression(breakExpression)
    }

    final override fun visitContinueExpression(continueExpression: FirContinueExpression, data: Nothing?) {
        visitContinueExpression(continueExpression)
    }

    final override fun visitCatch(catch: FirCatch, data: Nothing?) {
        visitCatch(catch)
    }

    final override fun visitTryExpression(tryExpression: FirTryExpression, data: Nothing?) {
        visitTryExpression(tryExpression)
    }

    final override fun <T> visitConstExpression(constExpression: FirConstExpression<T>, data: Nothing?) {
        visitConstExpression(constExpression)
    }

    final override fun visitTypeProjection(typeProjection: FirTypeProjection, data: Nothing?) {
        visitTypeProjection(typeProjection)
    }

    final override fun visitStarProjection(starProjection: FirStarProjection, data: Nothing?) {
        visitStarProjection(starProjection)
    }

    final override fun visitPlaceholderProjection(placeholderProjection: FirPlaceholderProjection, data: Nothing?) {
        visitPlaceholderProjection(placeholderProjection)
    }

    final override fun visitTypeProjectionWithVariance(typeProjectionWithVariance: FirTypeProjectionWithVariance, data: Nothing?) {
        visitTypeProjectionWithVariance(typeProjectionWithVariance)
    }

    final override fun visitArgumentList(argumentList: FirArgumentList, data: Nothing?) {
        visitArgumentList(argumentList)
    }

    final override fun visitCall(call: FirCall, data: Nothing?) {
        visitCall(call)
    }

    final override fun visitAnnotation(annotation: FirAnnotation, data: Nothing?) {
        visitAnnotation(annotation)
    }

    final override fun visitAnnotationCall(annotationCall: FirAnnotationCall, data: Nothing?) {
        visitAnnotationCall(annotationCall)
    }

    final override fun visitAnnotationArgumentMapping(annotationArgumentMapping: FirAnnotationArgumentMapping, data: Nothing?) {
        visitAnnotationArgumentMapping(annotationArgumentMapping)
    }

    final override fun visitComparisonExpression(comparisonExpression: FirComparisonExpression, data: Nothing?) {
        visitComparisonExpression(comparisonExpression)
    }

    final override fun visitTypeOperatorCall(typeOperatorCall: FirTypeOperatorCall, data: Nothing?) {
        visitTypeOperatorCall(typeOperatorCall)
    }

    final override fun visitAssignmentOperatorStatement(assignmentOperatorStatement: FirAssignmentOperatorStatement, data: Nothing?) {
        visitAssignmentOperatorStatement(assignmentOperatorStatement)
    }

    final override fun visitEqualityOperatorCall(equalityOperatorCall: FirEqualityOperatorCall, data: Nothing?) {
        visitEqualityOperatorCall(equalityOperatorCall)
    }

    final override fun visitWhenExpression(whenExpression: FirWhenExpression, data: Nothing?) {
        visitWhenExpression(whenExpression)
    }

    final override fun visitWhenBranch(whenBranch: FirWhenBranch, data: Nothing?) {
        visitWhenBranch(whenBranch)
    }

    final override fun visitContextReceiverArgumentListOwner(contextReceiverArgumentListOwner: FirContextReceiverArgumentListOwner, data: Nothing?) {
        visitContextReceiverArgumentListOwner(contextReceiverArgumentListOwner)
    }

    final override fun visitQualifiedAccess(qualifiedAccess: FirQualifiedAccess, data: Nothing?) {
        visitQualifiedAccess(qualifiedAccess)
    }

    final override fun visitCheckNotNullCall(checkNotNullCall: FirCheckNotNullCall, data: Nothing?) {
        visitCheckNotNullCall(checkNotNullCall)
    }

    final override fun visitElvisExpression(elvisExpression: FirElvisExpression, data: Nothing?) {
        visitElvisExpression(elvisExpression)
    }

    final override fun visitArrayOfCall(arrayOfCall: FirArrayOfCall, data: Nothing?) {
        visitArrayOfCall(arrayOfCall)
    }

    final override fun visitAugmentedArraySetCall(augmentedArraySetCall: FirAugmentedArraySetCall, data: Nothing?) {
        visitAugmentedArraySetCall(augmentedArraySetCall)
    }

    final override fun visitClassReferenceExpression(classReferenceExpression: FirClassReferenceExpression, data: Nothing?) {
        visitClassReferenceExpression(classReferenceExpression)
    }

    final override fun visitErrorExpression(errorExpression: FirErrorExpression, data: Nothing?) {
        visitErrorExpression(errorExpression)
    }

    final override fun visitErrorFunction(errorFunction: FirErrorFunction, data: Nothing?) {
        visitErrorFunction(errorFunction)
    }

    final override fun visitErrorProperty(errorProperty: FirErrorProperty, data: Nothing?) {
        visitErrorProperty(errorProperty)
    }

    final override fun visitQualifiedAccessExpression(qualifiedAccessExpression: FirQualifiedAccessExpression, data: Nothing?) {
        visitQualifiedAccessExpression(qualifiedAccessExpression)
    }

    final override fun visitPropertyAccessExpression(propertyAccessExpression: FirPropertyAccessExpression, data: Nothing?) {
        visitPropertyAccessExpression(propertyAccessExpression)
    }

    final override fun visitFunctionCall(functionCall: FirFunctionCall, data: Nothing?) {
        visitFunctionCall(functionCall)
    }

    final override fun visitIntegerLiteralOperatorCall(integerLiteralOperatorCall: FirIntegerLiteralOperatorCall, data: Nothing?) {
        visitIntegerLiteralOperatorCall(integerLiteralOperatorCall)
    }

    final override fun visitImplicitInvokeCall(implicitInvokeCall: FirImplicitInvokeCall, data: Nothing?) {
        visitImplicitInvokeCall(implicitInvokeCall)
    }

    final override fun visitDelegatedConstructorCall(delegatedConstructorCall: FirDelegatedConstructorCall, data: Nothing?) {
        visitDelegatedConstructorCall(delegatedConstructorCall)
    }

    final override fun visitComponentCall(componentCall: FirComponentCall, data: Nothing?) {
        visitComponentCall(componentCall)
    }

    final override fun visitCallableReferenceAccess(callableReferenceAccess: FirCallableReferenceAccess, data: Nothing?) {
        visitCallableReferenceAccess(callableReferenceAccess)
    }

    final override fun visitThisReceiverExpression(thisReceiverExpression: FirThisReceiverExpression, data: Nothing?) {
        visitThisReceiverExpression(thisReceiverExpression)
    }

    final override fun visitSmartCastExpression(smartCastExpression: FirSmartCastExpression, data: Nothing?) {
        visitSmartCastExpression(smartCastExpression)
    }

    final override fun visitSafeCallExpression(safeCallExpression: FirSafeCallExpression, data: Nothing?) {
        visitSafeCallExpression(safeCallExpression)
    }

    final override fun visitCheckedSafeCallSubject(checkedSafeCallSubject: FirCheckedSafeCallSubject, data: Nothing?) {
        visitCheckedSafeCallSubject(checkedSafeCallSubject)
    }

    final override fun visitGetClassCall(getClassCall: FirGetClassCall, data: Nothing?) {
        visitGetClassCall(getClassCall)
    }

    final override fun visitWrappedExpression(wrappedExpression: FirWrappedExpression, data: Nothing?) {
        visitWrappedExpression(wrappedExpression)
    }

    final override fun visitWrappedArgumentExpression(wrappedArgumentExpression: FirWrappedArgumentExpression, data: Nothing?) {
        visitWrappedArgumentExpression(wrappedArgumentExpression)
    }

    final override fun visitLambdaArgumentExpression(lambdaArgumentExpression: FirLambdaArgumentExpression, data: Nothing?) {
        visitLambdaArgumentExpression(lambdaArgumentExpression)
    }

    final override fun visitSpreadArgumentExpression(spreadArgumentExpression: FirSpreadArgumentExpression, data: Nothing?) {
        visitSpreadArgumentExpression(spreadArgumentExpression)
    }

    final override fun visitNamedArgumentExpression(namedArgumentExpression: FirNamedArgumentExpression, data: Nothing?) {
        visitNamedArgumentExpression(namedArgumentExpression)
    }

    final override fun visitVarargArgumentsExpression(varargArgumentsExpression: FirVarargArgumentsExpression, data: Nothing?) {
        visitVarargArgumentsExpression(varargArgumentsExpression)
    }

    final override fun visitResolvedQualifier(resolvedQualifier: FirResolvedQualifier, data: Nothing?) {
        visitResolvedQualifier(resolvedQualifier)
    }

    final override fun visitErrorResolvedQualifier(errorResolvedQualifier: FirErrorResolvedQualifier, data: Nothing?) {
        visitErrorResolvedQualifier(errorResolvedQualifier)
    }

    final override fun visitResolvedReifiedParameterReference(resolvedReifiedParameterReference: FirResolvedReifiedParameterReference, data: Nothing?) {
        visitResolvedReifiedParameterReference(resolvedReifiedParameterReference)
    }

    final override fun visitReturnExpression(returnExpression: FirReturnExpression, data: Nothing?) {
        visitReturnExpression(returnExpression)
    }

    final override fun visitStringConcatenationCall(stringConcatenationCall: FirStringConcatenationCall, data: Nothing?) {
        visitStringConcatenationCall(stringConcatenationCall)
    }

    final override fun visitThrowExpression(throwExpression: FirThrowExpression, data: Nothing?) {
        visitThrowExpression(throwExpression)
    }

    final override fun visitVariableAssignment(variableAssignment: FirVariableAssignment, data: Nothing?) {
        visitVariableAssignment(variableAssignment)
    }

    final override fun visitWhenSubjectExpression(whenSubjectExpression: FirWhenSubjectExpression, data: Nothing?) {
        visitWhenSubjectExpression(whenSubjectExpression)
    }

    final override fun visitWrappedDelegateExpression(wrappedDelegateExpression: FirWrappedDelegateExpression, data: Nothing?) {
        visitWrappedDelegateExpression(wrappedDelegateExpression)
    }

    final override fun visitNamedReference(namedReference: FirNamedReference, data: Nothing?) {
        visitNamedReference(namedReference)
    }

    final override fun visitErrorNamedReference(errorNamedReference: FirErrorNamedReference, data: Nothing?) {
        visitErrorNamedReference(errorNamedReference)
    }

    final override fun visitSuperReference(superReference: FirSuperReference, data: Nothing?) {
        visitSuperReference(superReference)
    }

    final override fun visitThisReference(thisReference: FirThisReference, data: Nothing?) {
        visitThisReference(thisReference)
    }

    final override fun visitControlFlowGraphReference(controlFlowGraphReference: FirControlFlowGraphReference, data: Nothing?) {
        visitControlFlowGraphReference(controlFlowGraphReference)
    }

    final override fun visitResolvedNamedReference(resolvedNamedReference: FirResolvedNamedReference, data: Nothing?) {
        visitResolvedNamedReference(resolvedNamedReference)
    }

    final override fun visitDelegateFieldReference(delegateFieldReference: FirDelegateFieldReference, data: Nothing?) {
        visitDelegateFieldReference(delegateFieldReference)
    }

    final override fun visitBackingFieldReference(backingFieldReference: FirBackingFieldReference, data: Nothing?) {
        visitBackingFieldReference(backingFieldReference)
    }

    final override fun visitResolvedCallableReference(resolvedCallableReference: FirResolvedCallableReference, data: Nothing?) {
        visitResolvedCallableReference(resolvedCallableReference)
    }

    final override fun visitResolvedTypeRef(resolvedTypeRef: FirResolvedTypeRef, data: Nothing?) {
        visitResolvedTypeRef(resolvedTypeRef)
    }

    final override fun visitErrorTypeRef(errorTypeRef: FirErrorTypeRef, data: Nothing?) {
        visitErrorTypeRef(errorTypeRef)
    }

    final override fun visitTypeRefWithNullability(typeRefWithNullability: FirTypeRefWithNullability, data: Nothing?) {
        visitTypeRefWithNullability(typeRefWithNullability)
    }

    final override fun visitUserTypeRef(userTypeRef: FirUserTypeRef, data: Nothing?) {
        visitUserTypeRef(userTypeRef)
    }

    final override fun visitDynamicTypeRef(dynamicTypeRef: FirDynamicTypeRef, data: Nothing?) {
        visitDynamicTypeRef(dynamicTypeRef)
    }

    final override fun visitFunctionTypeRef(functionTypeRef: FirFunctionTypeRef, data: Nothing?) {
        visitFunctionTypeRef(functionTypeRef)
    }

    final override fun visitIntersectionTypeRef(intersectionTypeRef: FirIntersectionTypeRef, data: Nothing?) {
        visitIntersectionTypeRef(intersectionTypeRef)
    }

    final override fun visitImplicitTypeRef(implicitTypeRef: FirImplicitTypeRef, data: Nothing?) {
        visitImplicitTypeRef(implicitTypeRef)
    }

    final override fun visitEffectDeclaration(effectDeclaration: FirEffectDeclaration, data: Nothing?) {
        visitEffectDeclaration(effectDeclaration)
    }

    final override fun visitContractDescription(contractDescription: FirContractDescription, data: Nothing?) {
        visitContractDescription(contractDescription)
    }

    final override fun visitLegacyRawContractDescription(legacyRawContractDescription: FirLegacyRawContractDescription, data: Nothing?) {
        visitLegacyRawContractDescription(legacyRawContractDescription)
    }

    final override fun visitRawContractDescription(rawContractDescription: FirRawContractDescription, data: Nothing?) {
        visitRawContractDescription(rawContractDescription)
    }

    final override fun visitResolvedContractDescription(resolvedContractDescription: FirResolvedContractDescription, data: Nothing?) {
        visitResolvedContractDescription(resolvedContractDescription)
    }

}
