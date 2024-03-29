plugins {
    `maven-publish`
    java
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"
    id("org.jetbrains.dokka") version "1.6.10"
}

group = "io.redvox.apis"
version = "0.2.3"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.google.protobuf", "protobuf-java", "3.19.3")
    implementation("org.lz4", "lz4-java", "1.8.0")
    testCompile("junit", "junit", "4.12")
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/RedVoxInc/api-1000-jvm-sdk")
            credentials {
                username = System.getenv("GH_USER")
                password = System.getenv("GH_TOKEN")
            }
        }
    }
    publications {
        create<MavenPublication>("default") {
            from(components["java"])
        }
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

