plugins {
    kotlin("jvm")
    `java-library`
}

group = "io.buttersoft.feja"
version = "0.1.0"

kotlin {
    jvmToolchain(17)
}

dependencies {
    api("io.arrow-kt:arrow-core:2.3.0.1")

    testImplementation("io.kotest:kotest-runner-junit5:6.2.7")
    testImplementation("io.kotest:kotest-assertions-core:6.2.7")
}

tasks.test {
    useJUnitPlatform()
}