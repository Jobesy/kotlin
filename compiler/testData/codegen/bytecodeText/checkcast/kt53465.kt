// TARGET_BACKEND: JVM_IR

public inline fun <reified T> myEmptyArray(): Array<T> = arrayOfNulls<T>(0) as Array<T>

inline fun <reified T> Array<out T>?.myOrEmpty(): Array<out T> = this ?: myEmptyArray<T>()

fun foo(a : Array<String>?) = a.myOrEmpty()

// 0 CHECKCAST