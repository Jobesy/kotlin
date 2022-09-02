plugins {
    kotlin("multiplatform")
}

kotlin {
    jvm()
    val targetA = <targetA>("targetA")
    val targetB = <targetB>("targetB")

    listOf(targetA, targetB).forEach {
        it.compilations.getByName("main") {
            cinterops.create("yummy") {
                val nativeLibs = rootDir.resolve("native-libs")
                defFile = nativeLibs.resolve("yummy.def")
                compilerOpts += "-I" + nativeLibs.absolutePath
            }
        }
    }

    sourceSets {
        val commonMain by getting
        val targetAMain by getting
        val targetBMain by getting

        val nativeMain by creating {
            this.dependsOn(commonMain)
            targetAMain.dependsOn(this)
            targetBMain.dependsOn(this)
            dependencies {
                implementation(project(":lib"))
            }
        }
        
        val commonTest by getting
        val targetATest by getting
        val targetBTest by getting

        val nativeTest by creating {
            this.dependsOn(commonTest)
            targetATest.dependsOn(this)
            targetBTest.dependsOn(this)
        }
    }
}
