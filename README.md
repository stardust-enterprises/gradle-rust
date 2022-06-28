# gradle-rust [![Build][badge-github-ci]][project-gradle-ci] 

a plugin for inter-compatibility with [rust][rust] inside [gradle][gradle] projects.

# importing

you can import [gradle-rust][project-url] from [gradle's plugin portal][gpp] just by adding it to your `plugins` block:

```kotlin
plugins {
    id("fr.stardustenterprises.rust.wrapper") version "3.2.3"
}
```

# how to use

check out [this example repository](https://github.com/stardust-enterprises/gradle-rust-example) to learn in detail
how to use gradle-rust

# troubleshooting

if you ever encounter any problem **related to this project**, you can [open an issue][new-issue] describing what the
problem is. please, be as precise as you can, so that we can help you asap. we are most likely to close the issue if it
is not related to our work.

# contributing

you can contribute by [forking the repository][fork], making your changes and [creating a new pull request][new-pr]
describing what you changed, why and how.

# licensing

this project is under the [ISC license][project-license].


<!-- Links -->

[jvm]: https://adoptium.net "adoptium website"

[kotlin]: https://kotlinlang.org "kotlin website"

[gradle]: https://gradle.org "gradle website"

[rust]: https://rust-lang.org "rust website"

[mvnc]: https://repo1.maven.org/maven2/ "maven central website"

[gpp]: https://plugins.gradle.org/ "gradle plugin portal website"

<!-- Project Links -->

[project-url]: https://github.com/stardust-enterprises/gradle-rust "project github repository"

[fork]: https://github.com/stardust-enterprises/gradle-rust/fork "fork this repository"

[new-pr]: https://github.com/stardust-enterprises/gradle-rust/pulls/new "create a new pull request"

[new-issue]: https://github.com/stardust-enterprises/gradle-rust/issues/new "create a new issue"

[project-gradle-ci]: https://github.com/stardust-enterprises/gradle-rust/actions/workflows/gradle-ci.yml "gradle ci workflow"

[project-license]: https://github.com/stardust-enterprises/gradle-rust/blob/trunk/LICENSE "LICENSE source file"

<!-- Badges -->

[badge-github-ci]: https://github.com/stardust-enterprises/gradle-rust/actions/workflows/build.yml/badge.svg?branch=trunk "github actions badge"
