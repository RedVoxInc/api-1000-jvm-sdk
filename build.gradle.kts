plugins {
    java
    kotlin("jvm") version "1.4.10"
}

group = "io.redvox.apis"
version = "0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    testCompile("junit", "junit", "4.12")
}
