// TARGET_BACKEND: JVM_IR

fun foo(a : Array<String>?) = a.orEmpty()

// 0 CHECKCAST