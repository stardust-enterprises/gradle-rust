// Copyright 2021 Arc'blroth
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package fr.stardustenterprises.rust.wrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Configuration options for the Cross wrapper plugin.
 */
public class WrapperExtension {

    /**
     * Command used to run Rust's build system.
     * This usually is <code>cargo</code> or <code>cross</code>.
     * By default, the plugin will use the <code>cargo</code>
     * executable located on the path.
     */
    public String command = "cargo";

    /**
     * The rust toolchain to use.
     * By default, the plugin will use the default toolchain as
     * <a href="https://rust-lang.github.io/rustup/overrides.html">
     * resolved by rustup
     * </a>.
     */
    public String toolchain = null;

    /**
     * Location of the crate to build.
     * By default, this is the current project directory.
     */
    public String crate = null;

    /**
     * Map of the platform to target to the locations of the output executables/libraries.
     * Each key in this map is a <a href="https://doc.rust-lang.org/nightly/rustc/platform-support.html">target triple</a>.
     * Use <code>""</code> an empty string to use the default platform.
     */
    public Map<String, String> outputs = new ConcurrentHashMap<>();

    /**
     * Build profile to use. If this is not set to <code>"debug"</code>,
     * the plugin assumes a release profile.
     * By default, this is set to the <code>"release"</code> release profile.
     */
    public String profile = "release";

    /**
     * Additional arguments to pass to the build system.
     */
    public List<String> arguments = new ArrayList<>();

    /**
     * Additional environmental variables to set while building.
     */
    public Map<String, String> environment = new ConcurrentHashMap<>();
}
