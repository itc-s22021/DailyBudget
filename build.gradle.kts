plugins {
    id("com.android.application") version "8.2.0-beta05" apply false // 最新バージョンに更新
    id("com.android.library") version "8.2.0-beta05" apply false
    id("org.jetbrains.kotlin.android") version "1.9.10" apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
