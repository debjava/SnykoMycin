plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.1.0"
    id("org.jetbrains.intellij.platform") version "2.7.1"
}

group = "com.ddlab.rnd"
version = "0.0.1"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}


dependencies {
    implementation("tools.jackson.core:jackson-core:3.0.2")
    implementation("tools.jackson.core:jackson-databind:3.0.2")
    implementation("org.slf4j:slf4j-api:2.0.17")
    implementation("ch.qos.logback:logback-classic:1.5.21")
    compileOnly("org.projectlombok:lombok:1.18.32")
    annotationProcessor("org.projectlombok:lombok:1.18.32")

    intellijPlatform {
        create("IC", "2025.1.4.1")
        testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Platform)

        // Add necessary plugin dependencies for compilation here, example:
        // bundledPlugin("com.intellij.java")
    }
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "251"
        }

        changeNotes = """
            Initial version
        """.trimIndent()
    }
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
//        sourceCompatibility = "21"
//        targetCompatibility = "21"
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
}

kotlin {
    compilerOptions {
//        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}
