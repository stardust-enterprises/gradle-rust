dependencyResolutionManagement {
    // Make a catalog from the root project's libs.versions.toml
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "build-logic"