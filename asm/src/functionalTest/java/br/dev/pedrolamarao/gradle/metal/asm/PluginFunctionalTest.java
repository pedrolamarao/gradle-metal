package br.dev.pedrolamarao.gradle.metal.asm;

import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PluginFunctionalTest
{
    @TempDir Path projectDir;

    @Test
    public void apply () throws IOException
    {
        final var buildGradleKts =
        """
        plugins {
            id("br.dev.pedrolamarao.metal.asm")
        }
        """;

        Files.writeString(projectDir.resolve("build.gradle.kts"), buildGradleKts);

        GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();
    }

    @Test
    public void compile () throws Exception
    {
        final var mainCpp =
        """
        .global meh
        meh:
            ret
        """;

        Files.writeString(projectDir.resolve("main.s"), mainCpp);

        final var buildGradleKts =
        """
        plugins {
            id("br.dev.pedrolamarao.metal.asm")
        }
        
        tasks.register<br.dev.pedrolamarao.gradle.metal.asm.AsmCompileTask>("compile") {
            outputDirectory = file("object")
            source(file("main.s"))
        }
        """;

        Files.writeString(projectDir.resolve("build.gradle.kts"), buildGradleKts);

        GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .withArguments("compile")
            .build();

        try (var stream = Files.list(projectDir.resolve("object"))) {
            assertEquals(1, stream.count() );
        }
    }
}
