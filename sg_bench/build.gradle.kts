plugins {
    kotlin("jvm")
    id("me.champeau.jmh") version "0.7.1"
}

group = "org.pl"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(project(":solver"))
    implementation(project(":generator"))
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.20")
}

kotlin {
    jvmToolchain(11)
}

jmh {
    resultFormat.set("TEXT")
}
