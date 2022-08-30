// WITH_STDLIB
// WITH_COROUTINES

import kotlin.coroutines.*

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
    // TODO: Use kotlin.orEmpty after stdlib bootstrap
    arrayOf("1").orEmpty().forEach { suspendHere(it) }
}

fun box(): String {
    runBlocking(::main)
    return "OK"
}