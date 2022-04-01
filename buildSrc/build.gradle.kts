plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("main-kts"))
    implementation(kotlin("script-runtime"))
}
