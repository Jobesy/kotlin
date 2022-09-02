plugins {
    kotlin("multiplatform")
}

kotlin {
    listOf(<targetA>(), <targetB>()).forEach {
        it.compilations.getByName("main") {
            cinterops.create("dummy") {
                val nativeLibs = rootDir.resolve("native-libs")
                defFile = nativeLibs.resolve("dummy.def")
                compilerOpts += "-I" + nativeLibs.absolutePath
            }
        }
    }
}
