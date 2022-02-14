package me.xtrm.gradle.rust.tests

import java.util.stream.Collectors

fun main() {
    val list = mutableListOf(
        "aarch64-unknown-linux-gnu",
        "i686-pc-windows-gnu",
        "i686-pc-windows-msvc",
        "i686-unknown-linux-gnu",
        "x86_64-apple-darwin",
        "x86_64-pc-windows-gnu",
        "x86_64-pc-windows-msvc",
        "x86_64-unknown-linux-gnu",
        "aarch64-apple-darwin",

        "aarch64-pc-windows-msvc",
        "aarch64-unknown-linux-musl",
        "arm-unknown-linux-gnueabi",
        "arm-unknown-linux-gnueabihf",
        "armv7-unknown-linux-gnuabihf",
        "mips-unknown-linux-gnu",
        "mips64-unknown-linux-gnuabi64",
        "mips64el-unknown-linux-gnuabi64",
        "mipsel-unknown-linux-gnu",
        "powerpc-unknown-linux-gnu",
        "powerpc64-unknown-linux-gnu",
        "powerpc64le-unknown-linux-gnu",
        "riscv64gc-unknown-linux-gnu",
        "s390x-unknown-linux-gnu",
        "x86_64-unknown-freebsd",
        "x86_64-unknown-illumos",
        "x86_64-unknown-linux-musl",
        "x86_64-unknown-netbsd",

        "aarch64-apple-ios",
        "aarch64-apple-ios-sim",
        "aarch64-fuchsia",
        "aarch64-linux-android",
        "aarch64-unknown-none-softfloat",
        "aarch64-unknown-none",
        "arm-linux-androideabi",
        "arm-unknown-linux-musleabi",
        "arm-unknown-linux-musleabihf",
        "armebv7r-none-eabi",
        "armebv7r-none-eabihf",
        "armv5te-unknown-linux-gnueabi",
        "armv5te-unknown-linux-musleabi",
        "armv7-linux-androideabi",
        "armv7-unknown-linux-gnueabi",
        "armv7-unknown-linux-musleabi",
        "armv7-unknown-linux-musleabihf",
        "armv7a-none-eabi",
        "armv7r-none-eabi",
        "armv7r-none-eabihf",
        "asmjs-unknown-emscripten",
        "i586-pc-windows-msvc",
        "i586-unknown-linux-gnu",
        "i586-unknown-linux-musl",
        "i686-linux-android",
        "i686-unknown-freebsd",
        "i686-unknown-linux-musl",
        "mips-unknown-linux-musl",
        "mips64-unknown-linux-muslabi64",
        "mips64el-unknown-linux-muslabi64",
        "mipsel-unknown-linux-musl",
        "nvptx64-nvidia-cuda",
        "riscv32i-unknown-none-elf",
        "riscv32imac-unknown-none-elf",
        "riscv32imc-unknown-none-elf",
        "riscv64gc-unknown-none-elf",
        "riscv64imac-unknown-none-elf",
        "sparc64-unknown-linux-gnu",
        "sparcv9-sun-solaris",
        "thumbv6m-none-eabi",
        "thumbv7em-none-eabi",
        "thumbv7em-none-eabihf",
        "thumbv7m-none-eabi",
        "thumbv7neon-linux-androideabi",
        "thumbv7neon-unknown-linux-gnueabihf",
        "thumbv8m.base-none-eabi",
        "thumbv8m.main-none-eabi",
        "thumbv8m.main-none-eabihf",
        "wasm32-unknown-emscripten",
        "wasm32-unknown-unknown",
        "wasm32-wasi",
        "x86_64-apple-ios",
        "x86_64-fortanix-unknown-sgx",
        "x86_64-fuchsia",
        "x86_64-linux-android",
        "x86_64-pc-solaris",
        "x86_64-unknown-linux-gnux32",
        "x86_64-unknown-redox"
    )

    val skipTags = arrayOf(
        "unknown", "pc", "sun", "nvidia", "gnu", "msvc", "none", "elf", "wasi", "uwp"
    ) // does eabi/abi(64) belong in there?

    val skipSecondTags = arrayOf("apple", "linux")
    list.forEach {
        var parsedData = it.split('-').toMutableList()

        if (skipSecondTags.contains(parsedData[1])) {
            parsedData.removeAt(1)
        }

        var arch = parsedData[0]
        parsedData.removeAt(0)

        parsedData = parsedData.map { data ->
            var newData = data
            skipTags.forEach { skip ->
                newData = newData.replace(skip, "")
            }
            newData
        }.toMutableList()
        parsedData.filter(String::isEmpty).forEach(parsedData::remove)

        parsedData = parsedData.map { data ->
            var newData = data
            if (newData.endsWith("hf") || newData.contains("hardfloat")) {
                newData = newData.replace("hf", "").replace("hardfloat", "")
                arch += "hf"
            }
            if (newData.endsWith("sf") || newData.contains("softfloat")) {
                newData = newData.replace("sf", "").replace("softfloat", "")
                arch += "sf"
            }
            newData
        }.toMutableList()
        parsedData.filter(String::isEmpty).forEach(parsedData::remove)

        var os = parsedData.stream().collect(Collectors.joining("-"))
        if (os.isEmpty()) os = "unknown"

        println("$it arch: $arch, os: $os")
    }
}
