package enterprises.stardust.build

plugins {
    `java-library`
    `maven-publish`
    id("org.gradlex.reproducible-builds")
    signing
}

apply<PoorMansKotlinDslPlugin>()

val include by configurations.registering

java {
    withSourcesJar()
    withJavadocJar()

    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

tasks {
    jar {
        dependsOn(include)
        from(include.map { it.map { file -> if (file.isDirectory) zipTree(file) else file }})
    }
}

publishing {
    publications {
        all {
            signing {
                sign(this@all)
                isRequired = properties["signing.keyId"] != null
            }
        }
    }
}