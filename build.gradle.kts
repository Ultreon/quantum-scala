import org.jetbrains.gradle.ext.Application
import org.jetbrains.gradle.ext.GradleTask
import org.jetbrains.gradle.ext.runConfigurations
import org.jetbrains.gradle.ext.settings
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardOpenOption
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

plugins {
    id("idea")
    id("java")
    id("java-library")
    id("scala")
    id("maven-publish")
    id("org.jetbrains.gradle.plugin.idea-ext") version "1.1.8"
}

group = "dev.ultreon.craftmods"
version = "0.1.0+snapshot." + DateTimeFormatter.ofPattern("yyy.MM.dd.HH.mm").format(Instant.now().atOffset(ZoneOffset.UTC))

println("Building version $version")

base {
    archivesName.set("ultracraft-scala")
}

repositories {
    mavenCentral()

    maven {
        name = "SonaType Snapshots"
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")

        content {
            includeGroup("com.badlogicgames.gdx-video")
        }
    }

    maven {
        name = "SonaType Releases"
        url = uri("https://oss.sonatype.org/content/repositories/releases")

        content {
            includeGroup("com.badlogicgames.gdx-video")
        }
    }

    maven {
        name = "JitPack"
        url = uri("https://jitpack.io")

        content {
            includeGroup("dev.ultreon")
            includeGroup("dev.ultreon.quantum-voxel")
            includeGroup("dev.ultreon.ubo")
            includeGroup("dev.ultreon.corelibs")
            includeGroup("dev.ultreon.JNoiseJDK11")
            includeGroup("dev.ultreon.json5-api")
            includeGroup("dev.ultreon.quantum-fabric-loader")
            includeGroup("com.github.jagrosh")
            includeGroup("com.github.JnCrMx")
            includeGroup("com.github.mgsx-dev.gdx-gltf")
            includeGroup("space.earlygrey")
        }
    }

    maven {
        name = "FabricMC"
        url = uri("https://maven.fabricmc.net/")
    }
}

configurations {
    create("include") {
        isCanBeResolved = true
    }
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    configurations["include"](compileOnly("org.scala-lang:scala-library:2.13.10")!!)

//    api(implementation("io.github.ultreon.craft:ultracraft-api:0.1.+")!!)
    api(implementation("dev.ultreon.quantum-voxel:quantum-desktop:ed398498ec")!!)
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    from(configurations["include"].map { if (it.isDirectory) it else zipTree(it) })
}

tasks.test {
    useJUnitPlatform()
}

fun setupIdea() {
    mkdir("$projectDir/build/gameutils")
    mkdir("$projectDir/run")
    mkdir("$projectDir/run/client")
    mkdir("$projectDir/run/client/alt")
    mkdir("$projectDir/run/client/main")
    mkdir("$projectDir/run/server")

    val ps = File.pathSeparator!!
    val files = configurations["runtimeClasspath"]!!

    val classPath = files.asSequence()
        .filter { it != null }
        .map { it.path }
        .joinToString(ps)

    //language=TEXT
    val conf = """
commonProperties
	fabric.development=true
	log4j2.formatMsgNoLookups=true
	fabric.log.disableAnsi=false
	log4j.configurationFile=$projectDir/log4j.xml
    """.trimIndent()
    val launchFile = file("$projectDir/build/gameutils/launch.cfg")
    Files.writeString(
        launchFile.toPath(),
        conf,
        StandardOpenOption.CREATE,
        StandardOpenOption.TRUNCATE_EXISTING,
        StandardOpenOption.WRITE
    )

    val cpFile = file("$projectDir/build/gameutils/classpath.txt")
    Files.writeString(
        cpFile.toPath(),
        classPath,
        StandardOpenOption.CREATE,
        StandardOpenOption.TRUNCATE_EXISTING,
        StandardOpenOption.WRITE
    )

    rootProject.idea {
        project {
            settings {
                withIDEADir {
                    println("Callback 1 executed with: $absolutePath")
                }

                runConfigurations {
                    create(
                        "Ultracraft Client Scala",
                        Application::class.java
                    ) {                       // Create new run configuration "MyApp" that will run class foo.App
                        jvmArgs =
                            "-Xmx4g -Dfabric.skipMcProvider=true -Dfabric.dli.config=${launchFile.path} -Dfabric.dli.env=CLIENT -Dfabric.dli.main=net.fabricmc.loader.impl.launch.knot.KnotClient -Dfabric.zipfs.use_temp_file=false"
                        mainClass = "net.fabricmc.devlaunchinjector.Main"
                        moduleName = idea.module.name + ".api-scala.main"
                        workingDirectory = "$projectDir/run/client/main/"
                        programParameters = "--gameDir=."
                        beforeRun {
                            create("Clear Quilt Cache", GradleTask::class.java) {
                                this.task = tasks.named("clearClientMainQuiltCache").get()
                            }
                        }
                    }
                    create(
                        "Ultracraft Client Scala Alt",
                        Application::class.java
                    ) {                       // Create new run configuration "MyApp" that will run class foo.App
                        jvmArgs =
                            "-Xmx4g -Dfabric.skipMcProvider=true -Dfabric.dli.config=${launchFile.path} -Dfabric.dli.env=CLIENT -Dfabric.dli.main=net.fabricmc.loader.impl.launch.knot.KnotClient -Dfabric.zipfs.use_temp_file=false"
                        mainClass = "net.fabricmc.devlaunchinjector.Main"
                        moduleName = idea.module.name + ".api-scala.main"
                        workingDirectory = "$projectDir/run/client/alt/"
                        programParameters = "--gameDir=."
                        beforeRun {
                            create("Clear Quilt Cache", GradleTask::class.java) {
                                this.task = tasks.named("clearClientAltQuiltCache").get()
                            }
                        }
                    }
                    create(
                        "Ultracraft Server Scala",
                        Application::class.java
                    ) {                       // Create new run configuration "MyApp" that will run class foo.App
                        jvmArgs =
                            "-Xmx4g -Dfabric.skipMcProvider=true -Dfabric.dli.config=${launchFile.path} -Dfabric.dli.env=SERVER -Dfabric.dli.main=net.fabricmc.loader.impl.launch.knot.KnotClient -Dfabric.zipfs.use_temp_file=false"
                        mainClass = "net.fabricmc.devlaunchinjector.Main"
                        moduleName = idea.module.name + ".api-scala.main"
                        workingDirectory = "$projectDir/run/server/"
                        programParameters = "--gameDir=."
                        beforeRun {
                            create("Clear Quilt Cache", GradleTask::class.java) {
                                this.task = tasks.named("clearServerQuiltCache").get()
                            }
                        }
                    }
                }
            }
        }
    }
    rootProject.idea {
        module {
            isDownloadJavadoc = true
            isDownloadSources = true
        }
    }
}

beforeEvaluate {
    setupIdea()
}

publishing {
    publications {
        create("mavenScala", MavenPublication::class) {
            //noinspection GrUnresolvedAccess
            from(components["java"])
        }
    }

    repositories {
        mavenLocal()
    }
}
