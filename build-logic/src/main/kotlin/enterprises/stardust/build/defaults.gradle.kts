package enterprises.stardust.build

plugins {
    id("enterprises.stardust.build.baseline")
}

publishing {
    publications {
        create<MavenPublication>("defaultJava") {
            from(components["java"])
        }
    }
}