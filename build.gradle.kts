import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    `java-library`
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "pink.zak.mc"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://jitpack.io")
}

dependencies {
    implementation("redis.clients:jedis:3.6.1")
    implementation("com.github.GC-spigot:simple-spigot:1.1.8")

    compileOnly("me.clip:placeholderapi:2.10.10")
    compileOnly("org.spigotmc:spigot-api:1.17.1-R0.1-SNAPSHOT")
}

tasks.withType<ProcessResources> {
    filter(ReplaceTokens::class, "tokens" to mapOf("version" to version))
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    relocate("me.hyfe.simplespigot", "pink.zak.mc.leaderboards.simplespigot")
}