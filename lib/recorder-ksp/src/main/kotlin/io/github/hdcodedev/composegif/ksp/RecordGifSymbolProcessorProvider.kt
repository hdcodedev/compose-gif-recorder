package io.github.hdcodedev.composegif.ksp

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSValueArgument
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.writeTo
import io.github.hdcodedev.composegif.annotations.RecordGif

private const val GENERATED_PACKAGE = "io.github.hdcodedev.composegif.generated"
private const val GENERATED_OBJECT_NAME = "GeneratedGifScenarioRegistry"

public class RecordGifSymbolProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return RecordGifSymbolProcessor(
            codeGenerator = environment.codeGenerator,
            logger = environment.logger,
        )
    }
}

private class RecordGifSymbolProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {
    private var generated: Boolean = false

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (generated) return emptyList()

        val annotationName = RecordGif::class.qualifiedName ?: return emptyList()
        val symbols = resolver.getSymbolsWithAnnotation(annotationName).toList()

        val deferred = symbols.filterNot { it.validate() }
        val functions = symbols.filterIsInstance<KSFunctionDeclaration>()

        val validEntries = mutableListOf<ScenarioEntry>()
        val duplicateCheck = mutableSetOf<String>()
        var hasErrors = false

        functions.forEach { function ->
            val qualifiedName = function.qualifiedName?.asString()
            if (qualifiedName == null) {
                logger.error("@RecordGif can only be used on named functions.", function)
                hasErrors = true
                return@forEach
            }
            if (function.parameters.isNotEmpty()) {
                logger.error("@RecordGif supports only parameterless composable functions.", function)
                hasErrors = true
                return@forEach
            }
            if (function.parentDeclaration != null) {
                logger.error("@RecordGif currently supports only top-level composable functions.", function)
                hasErrors = true
                return@forEach
            }
            val isComposable =
                function.annotations.any {
                    val declaration = it.annotationType.resolve().declaration
                    declaration.simpleName.asString() == "Composable" &&
                        declaration.qualifiedName?.asString() == "androidx.compose.runtime.Composable"
                }
            if (!isComposable) {
                logger.error("@RecordGif target must also be annotated with @Composable.", function)
                hasErrors = true
                return@forEach
            }

            val args = function.extractArguments()
            val scenarioName = args.name.takeIf { it.isNotBlank() } ?: ScenarioNaming.defaultName(function.simpleName.asString())
            if (!duplicateCheck.add(scenarioName)) {
                logger.error("Duplicate @RecordGif scenario name '$scenarioName'.", function)
                hasErrors = true
                return@forEach
            }

            validEntries +=
                ScenarioEntry(
                    name = scenarioName,
                    qualifiedFunctionName = qualifiedName,
                    durationMs = args.durationMs,
                    fps = args.fps,
                    widthPx = args.widthPx,
                    heightPx = args.heightPx,
                    theme = args.theme,
                )
        }

        if (hasErrors) return deferred
        if (validEntries.isEmpty()) return deferred

        generateRegistry(validEntries)
        generated = true
        return deferred
    }

    private fun generateRegistry(entries: List<ScenarioEntry>) {
        val scenarioSpec = ClassName("io.github.hdcodedev.composegif.core", "GifScenarioSpec")
        val captureConfig = ClassName("io.github.hdcodedev.composegif.core", "GifCaptureConfig")
        val coreTheme = ClassName("io.github.hdcodedev.composegif.core", "GifTheme")
        val scenarioRegistry = ClassName("io.github.hdcodedev.composegif.core", "GifScenarioRegistry")
        val validator = ClassName("io.github.hdcodedev.composegif.core", "GifCaptureValidator")

        val scenariosProperty =
            PropertySpec.builder("scenarios", List::class.asClassName().parameterizedBy(scenarioSpec), KModifier.PRIVATE)
                .initializer(
                    CodeBlock.builder().apply {
                        add("listOf(\n")
                        entries.forEachIndexed { index, entry ->
                            add(
                                "  %T(name = %S, capture = %T(durationMs = %L, fps = %L, widthPx = %L, heightPx = %L, theme = %T.%L))",
                                scenarioSpec,
                                entry.name,
                                captureConfig,
                                entry.durationMs,
                                entry.fps,
                                entry.widthPx,
                                entry.heightPx,
                                coreTheme,
                                entry.theme,
                            )
                            if (index != entries.lastIndex) {
                                add(",\n")
                            } else {
                                add("\n")
                            }
                        }
                        add(")")
                    }.build(),
                )
                .build()

        val namesProperty =
            PropertySpec.builder("SCENARIO_NAMES", List::class.asClassName().parameterizedBy(String::class.asClassName()))
                .initializer(
                    CodeBlock.builder().apply {
                        add("listOf(")
                        entries.forEachIndexed { index, entry ->
                            if (index > 0) add(", ")
                            add("%S", entry.name)
                        }
                        add(")")
                    }.build(),
                )
                .build()

        val scenariosFun =
            FunSpec.builder("scenarios")
                .addModifiers(KModifier.OVERRIDE)
                .returns(List::class.asClassName().parameterizedBy(scenarioSpec))
                .addStatement("return scenarios")
                .build()

        val renderFun =
            FunSpec.builder("Render")
                .addModifiers(KModifier.OVERRIDE)
                .addAnnotation(ClassName("androidx.compose.runtime", "Composable"))
                .addParameter("name", String::class)
                .beginControlFlow("when (name)")
                .apply {
                    entries.forEach { entry ->
                        addStatement("%S -> %L()", entry.name, entry.qualifiedFunctionName)
                    }
                    addStatement("else -> error(%P)", "Unknown GIF scenario: \$name")
                }
                .endControlFlow()
                .build()

        val initBlock =
            CodeBlock.builder()
                .beginControlFlow("for (scenario in scenarios)")
                .addStatement("%T.validate(scenario)", validator)
                .endControlFlow()
                .build()

        val type =
            TypeSpec.objectBuilder(GENERATED_OBJECT_NAME)
                .addSuperinterface(scenarioRegistry)
                .addProperty(scenariosProperty)
                .addProperty(namesProperty)
                .addInitializerBlock(initBlock)
                .addFunction(scenariosFun)
                .addFunction(renderFun)
                .build()

        FileSpec.builder(GENERATED_PACKAGE, GENERATED_OBJECT_NAME)
            .addType(type)
            .build()
            .writeTo(codeGenerator = codeGenerator, aggregating = true)
    }

    private fun KSFunctionDeclaration.extractArguments(): AnnotationArgs {
        val args = annotations.first { it.shortName.asString() == "RecordGif" }.arguments
        return AnnotationArgs(
            name = args.valueAsString("name") ?: "",
            durationMs = args.valueAsInt("durationMs") ?: 1800,
            fps = args.valueAsInt("fps") ?: 50,
            widthPx = args.valueAsInt("widthPx") ?: 540,
            heightPx = args.valueAsInt("heightPx") ?: 0,
            theme = (args.valueAsEnumName("theme") ?: "DARK"),
        )
    }

    private data class AnnotationArgs(
        val name: String,
        val durationMs: Int,
        val fps: Int,
        val widthPx: Int,
        val heightPx: Int,
        val theme: String,
    )

    private data class ScenarioEntry(
        val name: String,
        val qualifiedFunctionName: String,
        val durationMs: Int,
        val fps: Int,
        val widthPx: Int,
        val heightPx: Int,
        val theme: String,
    )
}

private fun List<KSValueArgument>.valueAsString(name: String): String? =
    firstOrNull { it.name?.asString() == name }?.value as? String

private fun List<KSValueArgument>.valueAsInt(name: String): Int? =
    firstOrNull { it.name?.asString() == name }?.value as? Int

private fun List<KSValueArgument>.valueAsEnumName(name: String): String? {
    val value = firstOrNull { it.name?.asString() == name }?.value ?: return null
    return value.toString().substringAfterLast('.')
}
