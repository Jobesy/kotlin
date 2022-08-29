// CHECK_TYPESCRIPT_DECLARATIONS
// RUN_PLAIN_BOX_FUNCTION
// SKIP_MINIFICATION
// SKIP_NODE_JS
// INFER_MAIN_MODULE
// MODULE: JS_TESTS
// WITH_STDLIB
// FILE: declarations.kt

package foo

@JsExport
class OnlyFooParamExported(val foo: String) {
    @JsExport.Ignore
    constructor() : this("TEST")

    @JsExport.Ignore
    inline fun <A, reified B> A.notExportableReified(): Boolean = this is B

    @JsExport.Ignore
    suspend fun notExportableSuspend(): String = "SuspendResult"

    @JsExport.Ignore
    fun notExportableReturn(): List<String> = listOf("1", "2")

    @JsExport.Ignore
    val String.notExportableExentsionProperty: String
        get() = "notExportableExentsionProperty"

    @JsExport.Ignore
    annotation class NotExportableAnnotation

    @JsExport.Ignore
    value class NotExportableInlineClass(val value: Int)
}

@JsExport
interface ExportedInterface {
    @JsExport.Ignore
    class NotExportableNestedInsideInterface

    @JsExport.Ignore
    companion object {
        val foo: String ="FOO"
    }
}
