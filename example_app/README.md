# Example Project using Gradle-Rust

This project is organized into two subprojects:
- `:native`, which implements a JNI interface in Rust.
- `:app`, which depends on `:native` and calls Rust code through JNI.
