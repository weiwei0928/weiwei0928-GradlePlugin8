plugins {
    id("java-gradle-plugin") //会自动引入java-library、gradleApi()
    id("maven-publish") //maven发布插件
//    kotlin("jvm") version "1.8.10"//支持kotlin编写插件
    kotlin("jvm")
}

gradlePlugin {
    plugins {
        create("gradlePlugin") {
            group = "com.ww.plugin"
            version = "1.0.4"
            id = "com.ww.plugin.track"
            implementationClass = "com.ww.gradle.plugin.AsmTrackPlugin"
        }
    }
}

publishing {
    repositories {
        mavenLocal()
//        maven {
//            url = uri("../repo")
//        }
    }
}

dependencies {
//    implementation("com.android.tools.build:gradle:8.6.0")
    implementation("com.android.tools.build:gradle:7.4.2")
    implementation("org.ow2.asm:asm:9.2")
    implementation("org.ow2.asm:asm-commons:9.2")
    implementation("org.ow2.asm:asm-tree:9.2")
    implementation("org.ow2.asm:asm-util:9.2")

//    implementation(kotlin("stdlib-jdk8"))
}

//repositories {
//    google()
//    mavenCentral()
//    gradlePluginPortal()
//}

//kotlinOptions {
//    jvmTarget = "1.8"
//}
//kotlin {
//    jvmToolchain(11)
//}