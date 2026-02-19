package io.github.dautovicharis.composegif.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.io.IOException
import javax.inject.Inject
import kotlin.math.max

private const val GENERATED_REGISTRY_FILE = "generated/ksp/debug/kotlin/io/github/dautovicharis/composegif/generated/GeneratedGifScenarioRegistry.kt"
private const val GENERATED_REGISTRY_CLASS = "io.github.dautovicharis.composegif.generated.GeneratedGifScenarioRegistry"
private const val DEFAULT_TEST_CLASS = "io.github.dautovicharis.composegif.android.GifFrameCaptureTest"
private const val DEFAULT_LIBRARY_VERSION = "0.1.0-SNAPSHOT"
private const val DEFAULT_COMPOSE_UI_VERSION = "1.10.3"
private const val DEFAULT_REMOTE_SUBDIR = "gif-recorder"

public abstract class GifRecorderExtension
    @Inject
    constructor(objects: ObjectFactory) {
        public val applicationId: Property<String> = objects.property(String::class.java)
        public val outputDir: DirectoryProperty = objects.directoryProperty()
        public val adbSerial: Property<String> = objects.property(String::class.java)
        public val adbBin: Property<String> = objects.property(String::class.java)
        public val ffmpegBin: Property<String> = objects.property(String::class.java)
        public val ffprobeBin: Property<String> = objects.property(String::class.java)
        public val gifsicleBin: Property<String> = objects.property(String::class.java)
        public val scenario: Property<String> = objects.property(String::class.java)
        public val registryClass: Property<String> = objects.property(String::class.java)
        public val testClass: Property<String> = objects.property(String::class.java)
        public val libraryVersion: Property<String> = objects.property(String::class.java)
        public val gifWidth: Property<Int> = objects.property(Int::class.java)
        public val gifHeight: Property<Int> = objects.property(Int::class.java)
    }

public class ComposeGifRecorderPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val extension = project.extensions.create("gifRecorder", GifRecorderExtension::class.java)

        extension.outputDir.convention(project.layout.projectDirectory.dir("artifacts/gifs"))
        extension.adbSerial.convention("auto")
        extension.adbBin.convention("adb")
        extension.ffmpegBin.convention("ffmpeg")
        extension.ffprobeBin.convention("ffprobe")
        extension.gifsicleBin.convention("gifsicle")
        extension.scenario.convention("all")
        extension.registryClass.convention(GENERATED_REGISTRY_CLASS)
        extension.testClass.convention(DEFAULT_TEST_CLASS)
        extension.libraryVersion.convention(DEFAULT_LIBRARY_VERSION)
        extension.gifWidth.convention(540)
        extension.gifHeight.convention(0)

        val listTask = project.tasks.register("listGifScenarios", ListGifScenariosTask::class.java)
        listTask.configure { task ->
            task.generatedRegistryFile.convention(project.layout.buildDirectory.file(GENERATED_REGISTRY_FILE))
        }

        val singleTask = project.tasks.register("recordGifDebug", RecordGifTask::class.java)
        singleTask.configure { task ->
            task.configureFromExtension(project, extension)
            task.allScenarios.convention(false)
            task.dependsOn(listTask)
        }

        val allTask = project.tasks.register("recordGifsDebug", RecordGifTask::class.java)
        allTask.configure { task ->
            task.configureFromExtension(project, extension)
            task.allScenarios.convention(true)
            task.dependsOn(listTask)
        }

        project.plugins.withId("com.android.application") {
            configureDependencies(project, extension)
            listTask.configure { task ->
                task.dependsOn("kspDebugKotlin")
            }
            singleTask.configure { task ->
                task.dependsOn("kspDebugKotlin", "installDebug", "installDebugAndroidTest")
            }
            allTask.configure { task ->
                task.dependsOn("kspDebugKotlin", "installDebug", "installDebugAndroidTest")
            }
        }
    }

    private fun configureDependencies(project: Project, extension: GifRecorderExtension) {
        val version = extension.libraryVersion.getOrElse(DEFAULT_LIBRARY_VERSION)

        project.dependencies.add("implementation", "io.github.dautovicharis:compose-gif-recorder-annotations:$version")
        project.dependencies.add("implementation", "io.github.dautovicharis:compose-gif-recorder-core:$version")
        project.dependencies.add("implementation", "io.github.dautovicharis:compose-gif-recorder-android:$version")
        project.dependencies.add("ksp", "io.github.dautovicharis:compose-gif-recorder-ksp:$version")
        project.dependencies.add("androidTestImplementation", "io.github.dautovicharis:compose-gif-recorder-android:$version")
        project.dependencies.add("debugImplementation", "androidx.compose.ui:ui-test-manifest:$DEFAULT_COMPOSE_UI_VERSION")
    }
}

private fun RecordGifTask.configureFromExtension(
    project: Project,
    extension: GifRecorderExtension,
) {
    applicationId.convention(extension.applicationId)
    outputDir.convention(extension.outputDir)
    adbSerial.convention(extension.adbSerial)
    adbBin.convention(extension.adbBin)
    ffmpegBin.convention(extension.ffmpegBin)
    ffprobeBin.convention(extension.ffprobeBin)
    gifsicleBin.convention(extension.gifsicleBin)
    scenario.convention(project.providers.gradleProperty("gifScenario").orElse(extension.scenario))
    registryClass.convention(extension.registryClass)
    testClass.convention(extension.testClass)
    gifWidth.convention(extension.gifWidth)
    gifHeight.convention(extension.gifHeight)
    generatedRegistryFile.convention(project.layout.buildDirectory.file(GENERATED_REGISTRY_FILE))
}

public abstract class ListGifScenariosTask : DefaultTask() {
    @get:Internal
    public abstract val generatedRegistryFile: RegularFileProperty

    @TaskAction
    public fun listScenarios() {
        val file = generatedRegistryFile.get().asFile
        if (!file.exists()) {
            throw IllegalStateException(
                "Generated registry not found at ${file.path}. Run kspDebugKotlin first.",
            )
        }
        val names = parseScenarioNames(file)
        if (names.isEmpty()) {
            throw IllegalStateException("No GIF scenarios found in generated registry.")
        }

        logger.lifecycle("Compose GIF scenarios:")
        names.forEach { logger.lifecycle(" - $it") }
    }
}

public abstract class RecordGifTask : DefaultTask() {
    @get:Input
    public abstract val applicationId: Property<String>

    @get:OutputDirectory
    public abstract val outputDir: DirectoryProperty

    @get:Input
    public abstract val adbSerial: Property<String>

    @get:Input
    public abstract val adbBin: Property<String>

    @get:Input
    public abstract val ffmpegBin: Property<String>

    @get:Input
    public abstract val ffprobeBin: Property<String>

    @get:Input
    public abstract val gifsicleBin: Property<String>

    @get:Input
    public abstract val scenario: Property<String>

    @get:Input
    public abstract val registryClass: Property<String>

    @get:Input
    public abstract val testClass: Property<String>

    @get:Input
    public abstract val gifWidth: Property<Int>

    @get:Input
    public abstract val gifHeight: Property<Int>

    @get:Input
    public abstract val allScenarios: Property<Boolean>

    @get:Internal
    public abstract val generatedRegistryFile: RegularFileProperty

    @TaskAction
    public fun record() {
        val registrySource = generatedRegistryFile.get().asFile
        if (!registrySource.exists()) {
            throw IllegalStateException(
                "Generated registry not found at ${registrySource.path}. Run kspDebugKotlin first.",
            )
        }

        val scenarioNames = parseScenarioNames(registrySource)
        if (scenarioNames.isEmpty()) {
            throw IllegalStateException("No scenarios found to record.")
        }

        val resolvedAdbBin = resolveAdbBinary()
        ensureBinaryExists(resolvedAdbBin)
        ensureBinaryExists(ffmpegBin.get())
        ensureBinaryExists(ffprobeBin.get())
        ensureBinaryExists(gifsicleBin.get())

        val selected =
            if (allScenarios.get()) {
                scenarioNames
            } else {
                val requested = scenario.get().takeIf { it.isNotBlank() && it != "all" } ?: scenarioNames.first()
                listOf(requested)
            }

        val serial = resolveDeviceSerial(resolvedAdbBin, adbSerial.get())
        val adbPrefix = listOf(resolvedAdbBin, "-s", serial)

        val outputRoot = outputDir.get().asFile
        outputRoot.mkdirs()

        selected.forEach { scenarioName ->
            if (!scenarioNames.contains(scenarioName)) {
                throw IllegalStateException("Scenario '$scenarioName' not found. Available: $scenarioNames")
            }
            logger.lifecycle("Recording scenario '$scenarioName' on device '$serial'")

            runChecked(
                adbPrefix +
                    listOf(
                        "shell",
                        "am",
                        "instrument",
                        "-w",
                        "-e",
                        "class",
                        "${testClass.get()}#captureScenario",
                        "-e",
                        "registry_class",
                        registryClass.get(),
                        "-e",
                        "scenario_name",
                        scenarioName,
                        "-e",
                        "output_subdir",
                        DEFAULT_REMOTE_SUBDIR,
                        "${applicationId.get()}.test/androidx.test.runner.AndroidJUnitRunner",
                    ),
            )

            val localScenarioDir = File(temporaryDir, "frames/$scenarioName").apply {
                deleteRecursively()
                mkdirs()
            }

            val remoteScenarioDir = "/sdcard/Android/data/${applicationId.get()}/files/$DEFAULT_REMOTE_SUBDIR/$scenarioName"
            runChecked(adbPrefix + listOf("pull", "$remoteScenarioDir/.", localScenarioDir.absolutePath))

            val frames = localScenarioDir.listFiles { file -> file.name.matches(Regex("frame-\\d{4}\\.png")) }?.sortedBy { it.name }
                ?: emptyList()
            if (frames.isEmpty()) {
                throw IllegalStateException("No frames found for scenario '$scenarioName'.")
            }

            val effectiveHeight = resolveCanvasHeight(frames)
            val normalizedDir = File(temporaryDir, "normalized/$scenarioName").apply {
                deleteRecursively()
                mkdirs()
            }

            val fps = resolveFpsFromRegistry(registrySource, scenarioName)
            runChecked(
                listOf(
                    ffmpegBin.get(),
                    "-hide_banner",
                    "-loglevel",
                    "error",
                    "-y",
                    "-framerate",
                    fps.toString(),
                    "-i",
                    "${localScenarioDir.absolutePath}/frame-%04d.png",
                    "-vf",
                    "scale=${gifWidth.get()}:$effectiveHeight:flags=lanczos:force_original_aspect_ratio=decrease,pad=${gifWidth.get()}:$effectiveHeight:(ow-iw)/2:(oh-ih)/2:color=black,format=rgb24",
                    "${normalizedDir.absolutePath}/frame-%04d.png",
                ),
            )

            val palette = File(temporaryDir, "$scenarioName.palette.png")
            val baseGif = File(temporaryDir, "$scenarioName.base.gif")
            val finalGif = File(outputRoot, "$scenarioName.gif")

            runChecked(
                listOf(
                    ffmpegBin.get(),
                    "-hide_banner",
                    "-loglevel",
                    "error",
                    "-y",
                    "-framerate",
                    fps.toString(),
                    "-i",
                    "${normalizedDir.absolutePath}/frame-%04d.png",
                    "-vf",
                    "palettegen=stats_mode=full",
                    "-frames:v",
                    "1",
                    palette.absolutePath,
                ),
            )

            runChecked(
                listOf(
                    ffmpegBin.get(),
                    "-hide_banner",
                    "-loglevel",
                    "error",
                    "-y",
                    "-framerate",
                    fps.toString(),
                    "-i",
                    "${normalizedDir.absolutePath}/frame-%04d.png",
                    "-i",
                    palette.absolutePath,
                    "-lavfi",
                    "paletteuse=dither=bayer:bayer_scale=3:diff_mode=rectangle",
                    baseGif.absolutePath,
                ),
            )

            runChecked(
                listOf(
                    gifsicleBin.get(),
                    "--no-warnings",
                    "--optimize=3",
                    "--lossy=0",
                    "--colors",
                    "256",
                    baseGif.absolutePath,
                    "-o",
                    finalGif.absolutePath,
                ),
            )

            logger.lifecycle("Generated GIF: ${finalGif.absolutePath}")
        }
    }

    private fun resolveDeviceSerial(
        adb: String,
        configuredSerial: String,
    ): String {
        if (configuredSerial != "auto") return configuredSerial
        val output = runChecked(listOf(adb, "devices"))
        val devices =
            output
                .lineSequence()
                .drop(1)
                .map { it.trim() }
                .filter { it.endsWith("\tdevice") }
                .map { it.substringBefore("\t") }
                .toList()
        if (devices.isEmpty()) {
            throw IllegalStateException("No connected Android device/emulator found.")
        }
        if (devices.size > 1) {
            throw IllegalStateException("Multiple devices found: $devices. Configure gifRecorder.adbSerial.")
        }
        return devices.first()
    }

    private fun resolveCanvasHeight(frames: List<File>): Int {
        val configuredHeight = gifHeight.get()
        if (configuredHeight > 0) return configuredHeight

        var maxScaledHeight = 0
        frames.forEach { frame ->
            val dims =
                runChecked(
                    listOf(
                        ffprobeBin.get(),
                        "-v",
                        "error",
                        "-select_streams",
                        "v:0",
                        "-show_entries",
                        "stream=width,height",
                        "-of",
                        "csv=p=0:s=x",
                        frame.absolutePath,
                    ),
                ).trim()
            val parts = dims.split("x")
            check(parts.size == 2) { "Could not resolve frame dimensions from $dims" }
            val width = parts[0].toInt()
            val height = parts[1].toInt()
            val scaled = (height * gifWidth.get() + width - 1) / width
            if (scaled > maxScaledHeight) maxScaledHeight = scaled
        }

        return max(1, maxScaledHeight)
    }

    private fun resolveFpsFromRegistry(
        registryFile: File,
        scenarioName: String,
    ): Int {
        val text = registryFile.readText()
        val regex = Regex("name\\s*=\\s*\"$scenarioName\".*?fps\\s*=\\s*(\\d+)", setOf(RegexOption.DOT_MATCHES_ALL))
        return regex.find(text)?.groupValues?.get(1)?.toIntOrNull() ?: 50
    }

    private fun resolveAdbBinary(): String {
        val configured = adbBin.get().trim()
        if (configured.isBlank()) {
            throw IllegalStateException("gifRecorder.adbBin cannot be blank.")
        }

        val configuredFile = File(configured)
        if (configuredFile.isAbsolute || configured.contains(File.separatorChar)) {
            return configured
        }

        val executableName = if (isWindows()) "adb.exe" else "adb"
        if (configured != executableName && configured != "adb") {
            return configured
        }

        findExecutableOnPath(executableName)?.let { return it }

        val userHome = System.getProperty("user.home").orEmpty()
        val candidates =
            buildList {
                addSdkCandidate(System.getenv("ANDROID_SDK_ROOT"), executableName)
                addSdkCandidate(System.getenv("ANDROID_HOME"), executableName)
                addSdkCandidate("$userHome/Library/Android/sdk", executableName)
                addSdkCandidate("$userHome/Android/Sdk", executableName)
            }
                .distinct()

        val matched = candidates.firstOrNull { File(it).exists() }
        if (matched != null) return matched

        throw IllegalStateException(
            buildString {
                append("Could not locate adb automatically. ")
                append("Install Android SDK platform-tools or set gifRecorder.adbBin to an absolute adb path. ")
                append("Checked ANDROID_SDK_ROOT, ANDROID_HOME, and common SDK paths: ")
                append(candidates.joinToString())
            },
        )
    }

    private fun findExecutableOnPath(binaryName: String): String? {
        val pathValue = System.getenv("PATH") ?: return null
        val separator = File.pathSeparatorChar
        return pathValue
            .split(separator)
            .asSequence()
            .map { File(it, binaryName) }
            .firstOrNull { it.exists() && it.isFile }
            ?.absolutePath
    }

    private fun MutableList<String>.addSdkCandidate(
        sdkRoot: String?,
        executableName: String,
    ) {
        if (sdkRoot.isNullOrBlank()) return
        add(File(sdkRoot, "platform-tools/$executableName").absolutePath)
    }

    private fun isWindows(): Boolean =
        System.getProperty("os.name").contains("win", ignoreCase = true)

    private fun ensureBinaryExists(binary: String) {
        val resolved =
            if (binary.contains(File.separatorChar)) {
                File(binary).takeIf { it.exists() && it.isFile }?.absolutePath
            } else {
                findExecutableOnPath(binary)
            }
        if (resolved == null) {
            throw IllegalStateException("Binary not found or not executable: $binary")
        }
    }

    private fun runChecked(command: List<String>): String {
        val process =
            try {
                ProcessBuilder(command).directory(project.projectDir).redirectErrorStream(true).start()
            } catch (error: IOException) {
                throw IllegalStateException("Failed to start command (${command.joinToString(" ")}): ${error.message}", error)
            }
        val output = process.inputStream.bufferedReader().readText()
        val exit = process.waitFor()
        if (exit != 0) {
            throw IllegalStateException("Command failed (${command.joinToString(" ")}):\n$output")
        }
        return output
    }
}

internal fun parseScenarioNames(generatedRegistry: File): List<String> {
    if (!generatedRegistry.exists()) return emptyList()
    val text = generatedRegistry.readText()
    val regex = Regex("name\\s*=\\s*\"([^\"]+)\"")
    return regex.findAll(text).map { it.groupValues[1] }.distinct().toList()
}
