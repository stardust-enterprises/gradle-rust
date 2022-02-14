object Coordinates {
    const val NAME = "Gradle Rust"
    const val DESC = "Compile and use Rust projects within a Gradle project."

    const val GROUP = "fr.stardustenterprises.rust"
    const val VERSION = "3.1.0"
    const val REPO_ID = "stardust-enterprises/gradle-rust"
}

object Pom {
    const val REPO_ID = "stardust-enterprises/gradle-rust"
    val licenses = arrayOf(
        License("ISC License", "https://opensource.org/licenses/ISC")
    )
    val developers = arrayOf(
        Developer("xtrm")
    )
}

data class License(val name: String, val url: String)
data class Developer(val id: String, val name: String = id)