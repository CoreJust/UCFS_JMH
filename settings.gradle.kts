plugins {
    kotlin("jvm") version "1.9.20" apply false
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
}
rootProject.name = "ucfs"
include("solver")
include("generator")
include("test-shared")
include("sg_bench")
