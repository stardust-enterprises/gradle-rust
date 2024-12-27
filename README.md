# gradle-rust

> A plugin for inter-compatibility with Rust inside Gradle projects.

## Notice

> [!WARNING]
> This project is currently being rewritten and is not ready for use. You can check out the [v3.x branch](https://github.com/stardust-enterprises/gradle-rust/tree/v3).

## Developer Todo

- [ ] Feature parity with v3.x
- [ ] Better documentation
- Wrapper Plugin
  - Allow full configuration of the build inside the buildscript
    - [ ] Allow importing from a `Cargo.toml` file
    - [ ] Support for `rust-toolchain.toml`
    - [ ] Custom Dependency scope for rust libraries
      - [ ] version catalog???????????? no.
    - [ ] Replicate `java.toolchain` as `rust.toolchain`?
  - Automatic downloading of tooling
      - [ ] cargo
      - [ ] rustc
      - [ ] rustfmt/clippy
      - [ ] rustup?
      - [ ] cross???
      - [ ] osxcross?

## License

This project is licensed under the [GNU GPLv3](./LICENSE).