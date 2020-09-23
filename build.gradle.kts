plugins {
    java
    kotlin("jvm") version "1.4.10"
    kotlin("plugin.serialization") version "1.4.10"
    id("org.jetbrains.dokka") version "1.4.10"
}

group = "io.redvox.apis"
version = "0.1"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.google.protobuf", "protobuf-java", "3.13.0")
    implementation("org.lz4", "lz4-java", "1.7.1")
    testCompile("junit", "junit", "4.12")
}
