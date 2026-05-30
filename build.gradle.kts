plugins {
    `java-library`

    alias(libs.plugins.fabric.loom) apply false
    alias(libs.plugins.neoforged.gradle) apply false

    alias(libs.plugins.modstitch.multiloader)
    alias(libs.plugins.modstitch.manifests)
    alias(libs.plugins.modstitch.modrepos)

    `maven-publish`
    alias(libs.plugins.mod.publish.plugin)
    alias(libs.plugins.central.portal.publishing)
    signing

    alias(libs.plugins.spotless)
}

val minecraftVersion = libs.versions.minecraft.get()

group = "dev.isxander"
version = "1.4.0+$minecraftVersion"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
    isxander()
    terraformersMC()
}

dependencies {
    minecraft(libs.minecraft)
    fabricLoader(libs.fabric.loader)

    implementation(libs.yacl.fabric) // no common artifact of yacl

    fabricApi(libs.fabric.api)
    fabricApi(libs.yacl.fabric)
    fabricImplementation(libs.mod.menu)

    neoforgeImplementation(libs.neoforge)
    neoforgeImplementation(libs.yacl.neoforge)
}

runs.register("neoforgeClient") {
    runType("client")
}

val minecraftVersionRange = "[26.1,26.2)"
val supportedMinecraftVersions = manifests.minecraftReleasesMatching(minecraftVersionRange)

manifests {
    val common = manifest {
        modId = providers.gradleProperty("mod.id")
        version = project.version.toString()
        displayName = providers.gradleProperty("mod.name")
        description = providers.gradleProperty("mod.description")
        authors.add("isXander")
        iconPath = "icon.png"
        licenses.add("MPL-2.0")
        issueTrackerUrl = providers.gradleProperty("mod.issuesUrl")
        sourcesUrl = providers.gradleProperty("mod.sourcesUrl")
        homepage = sourcesUrl
        mixin("adaptive-tooltips.mixins.json")
        dependency("minecraft", REQUIRED, minecraftVersionRange)
        dependency("yet_another_config_lib_v3", REQUIRED, "[3.6.3,4)")
    }

    fabricModJson(sourceSets.fabric.get()) {
        from(common)
        provides.add("adaptive-tooltips") // legacy mod id
        environment = CLIENT
        entrypoint("client", "dev.isxander.adaptivetooltips.fabric.AdaptiveTooltipsFabric")
        entrypoint("modmenu", "dev.isxander.adaptivetooltips.fabric.ModMenuIntegration")
        dependency("fabricloader", REQUIRED, "[0.19,)")
        dependency("fabric-api", REQUIRED, "(,)")
    }

    neoForgeModsToml(sourceSets.neoforge.get()) {
        from(common)
    }
}

val clientGameTestManifest = manifests.fabricModJson {
    modId = "adaptive-tooltips-test"
    displayName = "Adaptive Tooltips Test"
    version = "1.0.0"
    iconPath = "icon.png"
    environment = CLIENT
    entrypoint("fabric-client-gametest", "dev.isxander.adaptivetooltips.test.AdaptiveTooltipsTest")
}

// TODO: https://github.com/FabricMC/fabric-loom/pull/1568
fabricApi {
    @Suppress("UnstableApiUsage")
    configureTests {
        createSourceSet = true
        modId = clientGameTestManifest.modId
        enableGameTests = false
        enableClientGameTests = true
        eula = true
    }
}

manifests.fabricModJson(sourceSets.getByName("gametest")) {
    from(clientGameTestManifest)
}

sourceSets.named("gametest") {
    compileClasspath += sourceSets.fabric.get().output
    runtimeClasspath += sourceSets.fabric.get().output
}
configurations.named("gametestCompileClasspath") {
    extendsFrom(configurations.named("fabricCompileClasspath"))
}
configurations.named("gametestRuntimeClasspath") {
    extendsFrom(configurations.named("fabricRuntimeClasspath"))
}

tasks.withType<Jar>().configureEach {
    from(rootProject.file("LICENSE")) {
        into("META-INF")
    }
}

tasks.withType<Javadoc>().configureEach {
    (options as StandardJavadocDocletOptions).addBooleanOption("Xdoclint:none", true)
}

publishMods {
    file = tasks.universalJar.flatMap { it.archiveFile }
    additionalFiles.from(tasks.fabricJar.flatMap { it.archiveFile })
    additionalFiles.from(tasks.neoforgeJar.flatMap { it.archiveFile })

    val projectVersion = project.version.toString()

    displayName = providers.gradleProperty("mod.name").map { "$it $projectVersion" }
    version = project.version.toString()
    modLoaders.addAll("fabric", "neoforge")
    type = STABLE

    changelog = providers.fileContents(rootProject.layout.projectDirectory.file("CHANGELOG.md")).asText
        .map { it.replace("{version}", projectVersion) }

    modrinth {
        accessToken = providers.environmentVariable("MODRINTH_TOKEN")
        projectId = providers.gradleProperty("modrinth.id")
        minecraftVersions.addAll(supportedMinecraftVersions)
        announcementTitle = "Download from Modrinth"

        requires("fabric-api", "yacl")
        optional("modmenu")
    }

    curseforge {
        accessToken = providers.environmentVariable("CURSEFORGE_TOKEN")
        projectId = providers.gradleProperty("curseforge.id")
        minecraftVersions.addAll(supportedMinecraftVersions)
        announcementTitle = "Download from Curseforge"
        client = true
        javaVersions.add(JavaVersion.VERSION_25)

        requires("fabric-api", "yacl")
        optional("modmenu")
    }

    discord {
        webhookUrl = providers.environmentVariable("DISCORD_WEBHOOK_URL")
        dryRunWebhookUrl = providers.environmentVariable("DISCORD_WEBHOOK_URL_DRY_RUN")
        username = displayName
        avatarUrl = "https://raw.githubusercontent.com/isXander/AdaptiveTooltips/main/src/main/resources/icon.png"
        content = changelog.zip(providers.gradleProperty("discord.ping")) { c, p -> "$c\n\n$p" }
    }
}

centralPortalPublishing.bundle("main") {
    username = providers.environmentVariable("MAVEN_CENTRAL_USERNAME")
    password = providers.environmentVariable("MAVEN_CENTRAL_PASSWORD")

    publishingType = "AUTOMATIC"
}

publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            from(components["java"])
            artifact(tasks.universalJar)
            artifact(tasks.universalSourcesJar)
        }
    }

    repositories {
        // Darn you, Kotlin!
        val repos = this as ExtensionAware
        repos.extensions.getByType<dev.lukebemish.centralportalpublishing.CentralPortalRepositoryHandlerExtension>()
            .portalBundle(":", "main")
    }
}

val shouldSign = providers.environmentVariable("SIGN")
    .map { it.toBoolean() }
    .orElse(false)

signing {
    isRequired = shouldSign.get()
    useInMemoryPgpKeys(
        providers.environmentVariable("GPG_KEY_ID").orNull,
        providers.environmentVariable("GPG_PRIVATE_KEY").orNull,
        providers.environmentVariable("GPG_PASSPHRASE").orNull,
    )
    sign(publishing.publications["mavenJava"])
}

tasks.register("publishAdaptiveTooltips") {
    group = "publishing"
    dependsOn("publishMods")
    dependsOn("publishMavenJavaPublicationToCentralPortalMainRepository")
}

spotless {
    java {
        target("src/**/*.java")
        licenseHeaderFile(rootProject.layout.projectDirectory.file("HEADER"))
    }
}
