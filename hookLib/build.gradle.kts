plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("maven-publish")
}

android {
    namespace = "com.ww.gradle.hooklib"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

publishing {
    publications {
        register<MavenPublication>("mavenRelease") {
            groupId = "com.ww.gradle"
            artifactId = "hookLib"
            version = "1.0.4"
            afterEvaluate {
                from(components["release"])
            }
        }
    }
    repositories {
        mavenLocal()
//        maven {
//            url = uri("../repo")
//        }
    }
}

dependencies {
    api("org.ow2.asm:asm:9.2")
    api("org.ow2.asm:asm-commons:9.2")
    api("org.ow2.asm:asm-tree:9.2")
    api("org.ow2.asm:asm-util:9.2")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
}