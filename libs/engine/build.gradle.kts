plugins {
    kotlin("jvm") version "2.2.10"
}

group = "io.awais.cricket_championship"
version = "0.1"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}