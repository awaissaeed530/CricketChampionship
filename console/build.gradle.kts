plugins {
   alias(libs.plugins.kotlinJvm)
}

group = "io.awais.cricket_championship"
version = "unspecified"

dependencies {
    implementation(project(":libs:engine"))
}