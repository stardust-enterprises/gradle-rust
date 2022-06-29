object Coordinates {
    const val NAME = "Gradle Rust"
    const val DESC = "Compile and use Rust projects within a Gradle project."
    const val VENDOR = "Stardust Enterprises"

    const val GIT_HOST = "github.com"
    const val REPO_ID = "stardust-enterprises/gradle-rust"

    const val GROUP = "fr.stardustenterprises.rust"
    const val VERSION = "3.2.4"
}

object Pom {
    val licenses = arrayOf(
        License("ISC License", "https://opensource.org/licenses/ISC")
    )
    val developers = arrayOf(
        Developer("xtrm")
    )
}

data class License(val name: String, val url: String, val distribution: String = "repo")
data class Developer(val id: String, val name: String = id)
