plugins {
    kotlin("jvm") version "2.2.10"
}

group = "io.awais.cricket_championship"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":libs:engine"))
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}