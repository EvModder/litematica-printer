plugins {
    id("java")
    id("fabric-loom").version("1.16.3")
}

val archives_base_name: String by project
val mod_version: String by project
val minecraft_version: String by project
val yarn_mappings: String by project
val loader_version: String by project
val malilib_version: String by project
val litematica_version: String by project
val mixinextras_version: String by project

version = "$mod_version+mc$minecraft_version"
base.archivesName = archives_base_name

java.toolchain.languageVersion = JavaLanguageVersion.of(21)

repositories {
    mavenCentral()

    exclusiveContent {
        forRepository { maven("https://api.modrinth.com/maven") { name = "Modrinth" } }
        filter { includeGroup("maven.modrinth") }
    }
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraft_version")
    mappings("net.fabricmc:yarn:$yarn_mappings:v2")
    modImplementation("net.fabricmc:fabric-loader:$loader_version")
    modImplementation("maven.modrinth:malilib:$malilib_version")
    modImplementation("maven.modrinth:litematica:$litematica_version")

    // Fabric Loader already ships MixinExtras, so compile against it without bundling a second copy.
    compileOnly("io.github.llamalad7:mixinextras-fabric:$mixinextras_version")
    annotationProcessor("io.github.llamalad7:mixinextras-fabric:$mixinextras_version")
}

tasks.withType<ProcessResources> {
    inputs.property("version", mod_version)

    filesMatching("fabric.mod.json") {
        expand(mapOf("version" to mod_version))
    }
}
