# Gradle Cross Wrapper

A [Gradle](https://www.gradle.org) plugin that wraps Rust's
[Cross](https://github.com/rust-embedded/cross) build system,
for embedding Rust libraries in Java projects.

This plugin has been tested with Gradle 7.0.0, and should
work with at least 7.x versions of Gradle.

If you're looking to use Rust with Android, you might want Mozilla's
[rust-android-gradle](https://github.com/mozilla/rust-android-gradle)
plugin.

If you're looking to wrap Rust's [Cargo](https://doc.rust-lang.org/cargo/),
make sure to check out the original [Gradle Cargo Wrapper](https://github.com/Arc-blroth/gradle-cargo-wrapper).

## Quickstart

For a basic project setup, see the [example](example) folder.

To use the plugin, first apply it in a subproject:
```groovy
// in example/native/build.gradle

plugins {
    id "io.github.nkosmos.cross" version "1.0.0"
}
```

Then specify the location of your Rust crate and the filename
of your built library using the `cross` extension:

```groovy
cross {
    // This defaults to the current project path if not specified.
    crate = projectDir.path
    outputs = ['': System.mapLibraryName('wrapper_example')]
}
```

The subproject where this plugin is applied will now publish
Cargo's outputs as Gradle artifacts, allowing you to use them in
other subprojects:

```groovy
// in example/app/build.gradle

// This doesn't work properly yet since this plugin supports
// multiple output files. Will try to figure out a workaround.

/*
configurations {
    // Declare a custom configuration to
    // resolve the library from :native
    backend {
        canBeConsumed false
        canBeResolved true
    }
}

dependencies {
    // Depend on our native code
    backend(project(':native'))
}

processResources {
    // Copy the native library into the final jar
    from(configurations.backend)
}
*/
```

## Configuration Options

The `cross` extension accepts these options:

### `crossCommand`

Location of the `cross` executable. Defaults to the Cross
executable located on the path.

### `toolchain`

The Rust toolchain to use. Defaults to the default toolchain as
[resolved by rustup](https://rust-lang.github.io/rustup/overrides.html).

### `crate`

Location of the crate to build. Defaults to
the project directory of the subproject where the plugin is applied.

### `outputs`

A map of the locations of the outputs executables/libraries.
Each entry in the map is a pair between a
[target triple](https://doc.rust-lang.org/nightly/rustc/platform-support.html)
and the name of the output file. Use `""` (the empty string)
to specify the default target.

This is useful if you want to build for several targets in
the same project.

### `profile`

The Rust [profile](https://doc.rust-lang.org/cargo/reference/profiles.html)
to use, as passed on the command line to Cargo. Can be either
`"debug"` or `"release"`. Defaults to `"release"`.

### `arguments`

A list of additional arguments to pass to the Cargo command line.

### `environment`

A map of additional environmental variable to set while launching
Cargo.

## License

The original [Gradle Cargo Wrapper](https://github.com/Arc-blroth/gradle-cargo-wrapper), 
and this project, are licensed under the [Apache 2.0 License](LICENSE).

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
