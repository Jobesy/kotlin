// IGNORE_BACKEND: JS
// RUN_PLAIN_BOX_FUNCTION
// INFER_MAIN_MODULE
// KEEP: A.B

// MODULE: keep_top_level_fun
// FILE: lib.kt

class A {
    fun foo(): String {
        return "foo"
    }

    fun bar(): String {
        return "bar"
    }

    class B {
        fun baz() = "baz"
    }
}

@JsExport
fun bar(): A.B {
    return A.B()
}

// FILE: test.js
function box() {
    var b = this["keep_top_level_fun"].bar()

    if (b.baz_232z_k$() != "baz") return "fail 1"

    return "OK"
}