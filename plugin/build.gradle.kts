@file:Suppress("UnstableApiUsage")

plugins {
    id("com.gradle.plugin-publish") version "0.18.0"
    `java-gradle-plugin`
    `maven-publish`
}

dependencies {
    compileOnly(gradleApi())
    implementation("commons-io:commons-io:2.11.0")
    implementation("net.lingala.zip4j:zip4j:2.9.1")
}

java {
    withJavadocJar()
    withSourcesJar()
}

tasks.jar {
    from("LICENSE")
}

gradlePlugin {
    plugins {
        create("wrapperPlugin") {
            displayName = "Rust Wrapper"
            description = "A plugin that wraps Rust's build systems, for embedding Rust libraries in Java projects."
            id = "fr.stardustenterprises.rust.wrapper"
            implementationClass = "fr.stardustenterprises.rust.wrapper.WrapperPlugin"
        }
        create("importerPlugin") {
            displayName = "Rust Importer"
            description = "A plugin that makes it possible to import outputs from Rust from another Gradle project."
            id = "fr.stardustenterprises.rust.importer"
            implementationClass = "fr.stardustenterprises.rust.importer.ImporterPlugin"
        }
    }
}

pluginBundle {
    vcsUrl = "https://github.com/${Coordinates.REPO_ID}"
    website = "https://github.com/${Coordinates.REPO_ID}"
    tags = listOf("rust", "rustlang", "cargo", "native", "wrapper")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = Coordinates.GROUP
            version = Coordinates.VERSION

            pom {
                val repo = Coordinates.REPO_ID

                name.set(Coordinates.NAME)
                description.set(Coordinates.DESC)
                url.set("https://github.com/$repo")

                licenses {
                    Pom.licenses.forEach {
                        license {
                            name.set(it.name)
                            url.set(it.url)
                        }
                    }
                }
                developers {
                    Pom.developers.forEach {
                        developer {
                            id.set(it.id)
                            name.set(it.name)
                        }
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/$repo.git")
                    developerConnection.set("scm:git:ssh://github.com/$repo.git")
                    url.set("https://github.com/$repo")
                }
            }
        }
    }
}
