import kotlinx.cinterop.cValue
import dummy.Y
import dummy.foo
import yummy.sel

fun nativeMain() {
    val y = cValue<Y> {
        n = 42
    }
    //yummy.yummy(y) //<-- doesn't work :( https://youtrack.jetbrains.com/issue/KT-51517
    foo()
    sel()
    dummyMain()
}