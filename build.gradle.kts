plugins {
    id("java")
    // Picks remapping Loom for obfuscated Minecraft versions and native Loom for 26.1+.
    id("dev.kikugie.loom-back-compat")
}

val archives_base_name: String by project
val mod_version: String by project
val minecraft_version: String by project
val loader_version: String by project
val malilib_version: String by project
val litematica_version: String by project
val mixinextras_version: String by project
val java_version: String by project

// Minecraft is only distributed deobfuscated from 26.1 onwards; older versions need remapping.
val obfuscated = stonecutter.current.version.startsWith("1.")

version = "$mod_version+mc$minecraft_version"
base.archivesName = archives_base_name
java.toolchain.languageVersion = JavaLanguageVersion.of(java_version.toInt())

repositories {
    mavenCentral()

    exclusiveContent {
        forRepository { maven("https://api.modrinth.com/maven") { name = "Modrinth" } }
        filter { includeGroup("maven.modrinth") }
    }
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraft_version")
    loomx.applyMojangMappings()

    if (obfuscated) {
        modImplementation("net.fabricmc:fabric-loader:$loader_version")
        modImplementation("maven.modrinth:malilib:$malilib_version")
        modImplementation("maven.modrinth:litematica:$litematica_version")
    } else {
        implementation("net.fabricmc:fabric-loader:$loader_version")
        implementation("maven.modrinth:malilib:$malilib_version")
        implementation("maven.modrinth:litematica:$litematica_version")
    }

    // Fabric Loader already ships MixinExtras, so compile against it without bundling a second copy.
    compileOnly("io.github.llamalad7:mixinextras-fabric:$mixinextras_version")
    annotationProcessor("io.github.llamalad7:mixinextras-fabric:$mixinextras_version")
}

loom {
    fabricModJsonPath = rootProject.file("src/main/resources/fabric.mod.json")
}

tasks.withType<ProcessResources> {
    val props = mapOf(
        "version" to mod_version,
        "minecraft_version" to minecraft_version,
        "loader_version" to loader_version,
        "malilib_version" to malilib_version,
        "litematica_version" to litematica_version,
        "mixinextras_version" to mixinextras_version,
        "java_version" to java_version,
    )
    props.forEach { (k, v) -> inputs.property(k, v) }

    filesMatching("fabric.mod.json") { expand(props) }
}
