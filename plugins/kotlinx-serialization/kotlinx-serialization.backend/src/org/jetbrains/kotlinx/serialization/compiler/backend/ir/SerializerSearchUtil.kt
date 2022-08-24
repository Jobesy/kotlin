/*
 * Copyright 2010-2022 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlinx.serialization.compiler.backend.ir

import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.expressions.IrClassReference
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlinx.serialization.compiler.extensions.SerializationPluginContext
import org.jetbrains.kotlinx.serialization.compiler.resolve.*
import org.jetbrains.kotlinx.serialization.compiler.resolve.SerializationJvmNames.contextSerializerId
import org.jetbrains.kotlinx.serialization.compiler.resolve.SerializationJvmNames.enumSerializerId
import org.jetbrains.kotlinx.serialization.compiler.resolve.SerializationJvmNames.objectSerializerId
import org.jetbrains.kotlinx.serialization.compiler.resolve.SerializationJvmNames.polymorphicSerializerId
import org.jetbrains.kotlinx.serialization.compiler.resolve.SerializationJvmNames.referenceArraySerializerId

class IrSerialTypeInfo(
    val property: IrSerializableProperty,
    val elementMethodPrefix: String,
    val serializer: IrClassSymbol? = null
)

fun BaseIrGenerator.getIrSerialTypeInfo(property: IrSerializableProperty, ctx: SerializationPluginContext): IrSerialTypeInfo {
    fun SerializableInfo(serializer: IrClassSymbol?) =
        IrSerialTypeInfo(property, if (property.type.isNullable()) "Nullable" else "", serializer)

    val T = property.type
    property.serializableWith(ctx)?.let { return SerializableInfo(it) }
    findAddOnSerializer(T, ctx)?.let { return SerializableInfo(it) }
    T.overridenSerializer?.let { return SerializableInfo(it) }
    return when {
        T.isTypeParameter() -> IrSerialTypeInfo(property, if (property.type.isMarkedNullable()) "Nullable" else "", null)
        T.isPrimitiveType() -> IrSerialTypeInfo(
            property,
            T.classFqName!!.asString().removePrefix("kotlin.")
        )
        T.isString() -> IrSerialTypeInfo(property, "String")
        T.isArray() -> {
            val serializer = property.serializableWith(ctx) ?: ctx.getClassFromInternalSerializationPackage(SpecialBuiltins.referenceArraySerializer)
            SerializableInfo(serializer)
        }
        else -> {
            val serializer =
                findTypeSerializerOrContext(ctx, property.type)
            SerializableInfo(serializer)
        }
    }
}

fun BaseIrGenerator.findAddOnSerializer(propertyType: IrType, ctx: SerializationPluginContext): IrClassSymbol? {
    val classSymbol = propertyType.classOrNull ?: return null
    additionalSerializersInScopeOfCurrentFile[classSymbol to propertyType.isNullable()]?.let { return it }
    if (classSymbol in contextualKClassListInCurrentFile)
        return ctx.getClassFromRuntime(SpecialBuiltins.contextSerializer)
    if (classSymbol.owner.annotations.hasAnnotation(SerializationAnnotations.polymorphicFqName))
        return ctx.getClassFromRuntime(SpecialBuiltins.polymorphicSerializer)
    if (propertyType.isNullable()) return findAddOnSerializer(propertyType.makeNotNull(), ctx)
    return null
}

fun BaseIrGenerator.findTypeSerializerOrContext(
    context: SerializationPluginContext, kType: IrType
): IrClassSymbol? {
    if (kType.isTypeParameter()) return null
    return findTypeSerializerOrContextUnchecked(context, kType) ?: error("Serializer for element of type ${kType.render()} has not been found")
}

fun BaseIrGenerator.findTypeSerializerOrContextUnchecked(
    context: SerializationPluginContext, kType: IrType
): IrClassSymbol? {
    val annotations = kType.annotations
    if (kType.isTypeParameter()) return null
    annotations.serializableWith()?.let { return it }
    additionalSerializersInScopeOfCurrentFile[kType.classOrNull!! to kType.isNullable()]?.let {
        return it
    }
    if (kType.isMarkedNullable()) return findTypeSerializerOrContextUnchecked(context, kType.makeNotNull())
    if (kType.classOrNull in contextualKClassListInCurrentFile) return context.referenceClass(contextSerializerId)
    return analyzeSpecialSerializers(context, annotations) ?: findTypeSerializer(context, kType)
}

fun analyzeSpecialSerializers(
    context: SerializationPluginContext,
    annotations: List<IrConstructorCall>
): IrClassSymbol? = when {
    annotations.hasAnnotation(SerializationAnnotations.contextualFqName) || annotations.hasAnnotation(SerializationAnnotations.contextualOnPropertyFqName) ->
        context.referenceClass(contextSerializerId)
    // can be annotation on type usage, e.g. List<@Polymorphic Any>
    annotations.hasAnnotation(SerializationAnnotations.polymorphicFqName) ->
        context.referenceClass(polymorphicSerializerId)
    else -> null
}


fun findTypeSerializer(context: SerializationPluginContext, type: IrType): IrClassSymbol? {
    type.overridenSerializer?.let { return it }
    if (type.isTypeParameter()) return null
    if (type.isArray()) return context.referenceClass(referenceArraySerializerId)
    if (type.isGeneratedSerializableObject()) return context.referenceClass(objectSerializerId)
    val stdSer = findStandardKotlinTypeSerializer(context, type) // see if there is a standard serializer
        ?: findEnumTypeSerializer(context, type)
    if (stdSer != null) return stdSer
    if (type.isInterface() && type.classOrNull?.owner?.isSealedSerializableInterface == false) return context.referenceClass(
        polymorphicSerializerId
    )
    return type.classOrNull?.owner.classSerializer(context) // check for serializer defined on the type
}
fun findEnumTypeSerializer(context: SerializationPluginContext, type: IrType): IrClassSymbol? {
    val classSymbol = type.classOrNull?.owner ?: return null
    return if (classSymbol.kind == ClassKind.ENUM_CLASS && !classSymbol.isEnumWithLegacyGeneratedSerializer(context))
        context.referenceClass(enumSerializerId)
    else null
}

internal fun IrClass?.classSerializer(context: SerializationPluginContext): IrClassSymbol? = this?.let {
    // serializer annotation on class?
    serializableWith?.let { return it }
    // companion object serializer?
    if (hasCompanionObjectAsSerializer) return companionObject()?.symbol
    // can infer @Poly?
    polymorphicSerializerIfApplicableAutomatically(context)?.let { return it }
    // default serializable?
    if (shouldHaveGeneratedSerializer(context)) {
        // $serializer nested class
        return this.declarations
            .filterIsInstance<IrClass>()
            .singleOrNull { it.name == SerialEntityNames.SERIALIZER_CLASS_NAME }?.symbol
    }
    return null
}

internal fun IrClass.polymorphicSerializerIfApplicableAutomatically(context: SerializationPluginContext): IrClassSymbol? {
    val serializer = when {
        kind == ClassKind.INTERFACE && modality == Modality.SEALED -> SpecialBuiltins.sealedSerializer
        kind == ClassKind.INTERFACE -> SpecialBuiltins.polymorphicSerializer
        isInternalSerializable && modality == Modality.ABSTRACT -> SpecialBuiltins.polymorphicSerializer
        isInternalSerializable && modality == Modality.SEALED -> SpecialBuiltins.sealedSerializer
        else -> null
    }
    return serializer?.let {
        context.getClassFromRuntimeOrNull(
            it,
            SerializationPackages.packageFqName,
            SerializationPackages.internalPackageFqName
        )
    }
}

internal val IrType.overridenSerializer: IrClassSymbol?
    get() {
        val desc = this.classOrNull ?: return null
        desc.owner.serializableWith?.let { return it }
        return null
    }

internal val IrClass.serializableWith: IrClassSymbol?
    get() = annotations.serializableWith()

internal val IrClass.serializerForClass: IrClassSymbol?
    get() = (annotations.findAnnotation(SerializationAnnotations.serializerAnnotationFqName)
        ?.getValueArgument(0) as? IrClassReference)?.symbol as? IrClassSymbol

fun findStandardKotlinTypeSerializer(context: SerializationPluginContext, type: IrType): IrClassSymbol? {
    val typeName = type.classFqName?.toString()
    val name = when (typeName) {
        "Z" -> if (type.isBoolean()) "BooleanSerializer" else null
        "B" -> if (type.isByte()) "ByteSerializer" else null
        "S" -> if (type.isShort()) "ShortSerializer" else null
        "I" -> if (type.isInt()) "IntSerializer" else null
        "J" -> if (type.isLong()) "LongSerializer" else null
        "F" -> if (type.isFloat()) "FloatSerializer" else null
        "D" -> if (type.isDouble()) "DoubleSerializer" else null
        "C" -> if (type.isChar()) "CharSerializer" else null
        null -> null
        else -> findStandardKotlinTypeSerializerName(typeName)
    } ?: return null
    return context.getClassFromRuntimeOrNull(name, SerializationPackages.internalPackageFqName, SerializationPackages.packageFqName)
}

// @Serializable(X::class) -> X
internal fun List<IrConstructorCall>.serializableWith(): IrClassSymbol? {
    val annotation = findAnnotation(SerializationAnnotations.serializableAnnotationFqName) ?: return null
    val arg = annotation.getValueArgument(0) as? IrClassReference ?: return null
    return arg.symbol as? IrClassSymbol
}

internal fun getSerializableClassByCompanion(companionClass: IrClass): IrClass? {
    if (companionClass.isSerializableObject) return companionClass
    if (!companionClass.isCompanion) return null
    val classDescriptor = (companionClass.parent as? IrClass) ?: return null
    if (!classDescriptor.shouldHaveGeneratedMethodsInCompanion) return null
    return classDescriptor
}

fun BaseIrGenerator.allSealedSerializableSubclassesFor(
    irClass: IrClass,
    context: SerializationPluginContext
): Pair<List<IrSimpleType>, List<IrClassSymbol>> {
    assert(irClass.modality == Modality.SEALED)
    fun recursiveSealed(klass: IrClass): Collection<IrClass> {
        return klass.sealedSubclasses.map { it.owner }.flatMap { if (it.modality == Modality.SEALED) recursiveSealed(it) else setOf(it) }
    }

    val serializableSubtypes = recursiveSealed(irClass).map { it.defaultType }
    return serializableSubtypes.mapNotNull { subtype ->
        findTypeSerializerOrContextUnchecked(context, subtype)?.let { Pair(subtype, it) }
    }.unzip()
}

internal fun SerializationPluginContext.getSerializableClassDescriptorBySerializer(serializer: IrClass): IrClass? {
    val serializerForClass = serializer.serializerForClass
    if (serializerForClass != null) return serializerForClass.owner
    if (serializer.name !in setOf(
            SerialEntityNames.SERIALIZER_CLASS_NAME,
            SerialEntityNames.GENERATED_SERIALIZER_CLASS
        )
    ) return null
    val classDescriptor = (serializer.parent as? IrClass) ?: return null
    if (!classDescriptor.shouldHaveGeneratedSerializer(this)) return null
    return classDescriptor
}

fun SerializationPluginContext.getClassFromRuntimeOrNull(className: String, vararg packages: FqName): IrClassSymbol? {
    val listToSearch = if (packages.isEmpty()) SerializationPackages.allPublicPackages else packages.toList()
    for (pkg in listToSearch) {
        referenceClass(ClassId(pkg, Name.identifier(className)))?.let { return it }
    }
    return null
}

fun SerializationPluginContext.getClassFromRuntime(className: String, vararg packages: FqName): IrClassSymbol {
    return getClassFromRuntimeOrNull(className, *packages) ?: error(
        "Class $className wasn't found in ${packages.toList().ifEmpty { SerializationPackages.allPublicPackages }}. " +
                "Check that you have correct version of serialization runtime in classpath."
    )
}

fun SerializationPluginContext.getClassFromInternalSerializationPackage(className: String): IrClassSymbol =
    getClassFromRuntimeOrNull(className, SerializationPackages.internalPackageFqName)
        ?: error("Class $className wasn't found in ${SerializationPackages.internalPackageFqName}. Check that you have correct version of serialization runtime in classpath.")

