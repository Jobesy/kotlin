// IGNORE_BACKEND: JS
// RUN_PLAIN_BOX_FUNCTION
// INFER_MAIN_MODULE
// KEEP: A.foo

// MODULE: keep_top_level_fun
// FILE: lib.kt

open class A {
    open fun foo(): String {
        return "foo"
    }

    open fun bar(): String {
        return "bar"
    }
}

open class B : A() {
    override fun foo(): String {
        return super.foo() + "!"
    }
}

@JsExport
fun bar(): B {
    return B()
}

// FILE: test.js
function box() {
    var b = this["keep_top_level_fun"].bar()

    if (b.foo_26di_k$() != "foo!") return "fail 1"

    return "OK"
}