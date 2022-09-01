// WITH_STDLIB
// WITH_COROUTINES

import kotlin.coroutines.*

public inline fun <reified T> myEmptyArray(): Array<T> = arrayOfNulls<T>(0) as Array<T>

inline fun <reified T> Array<out T>?.myOrEmpty(): Array<out T> = this ?: myEmptyArray<T>()

fun runBlocking(block: suspend () -> Unit) {
    block.startCoroutine(object : Continuation<Unit> {
        override val context: CoroutineContext
            get() = EmptyCoroutineContext

        override fun resumeWith(result: Result<Unit>) {
            (block as Function1<Continuation<Unit>, Any?>)(this)
        }
    })
}

suspend fun suspendHere(x: String) {}

suspend fun main() {
    arrayOf("1").myOrEmpty().forEach { suspendHere(it) }
}

fun box(): String {
    runBlocking(::main)
    return "OK"
}