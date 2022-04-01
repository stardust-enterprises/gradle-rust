object Coordinates {
    const val NAME = "kotlin-template"
    const val DESC = "Project description."
    const val VENDOR = "Stardust Enterprises"

    const val GIT_HOST = "github.com"
    const val REPO_ID = "stardust-enterprises/$NAME"

    const val GROUP = "fr.stardustenterprises"
    const val VERSION = "0.0.1"
}

object Pom {
    val licenses = arrayOf(
        License("ISC License", "https://opensource.org/licenses/ISC")
    )
    val developers = arrayOf(
        Developer("dev1")
    )
}

data class License(val name: String, val url: String, val distribution: String = "repo")
data class Developer(val id: String, val name: String = id)
