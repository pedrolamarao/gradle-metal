// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal;

import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.gradle.testkit.runner.TaskOutcome.FROM_CACHE;
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS;

@DisplayName("Gradle Metal C language")
class MetalCCompileTest extends MetalTestBase
{
    @Test
    void compile () throws IOException
    {
        Files.createDirectories(projectDir.resolve("src"));

        Files.writeString(projectDir.resolve("src/foo.c"),
            """
            int foo () { return 0; }
            """
        );

        Files.writeString(projectDir.resolve("src/bar.c"),
            """
            int bar () { return 0; }
            """
        );

        Files.writeString(projectDir.resolve("build.gradle.kts"),
            """
            import br.dev.pedrolamarao.gradle.metal.MetalCCompile
            
            plugins {
                id("base")
                id("br.dev.pedrolamarao.metal.base")
            }
            
            val compile = tasks.register<MetalCCompile>("compile") {
                outputDirectory = layout.buildDirectory.dir("obj")
                source = layout.projectDirectory.dir("src").asFileTree
            }
            """
        );

        final var compile = GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache","compile")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertThat( compile.task(":compile").getOutcome() ).isEqualTo(SUCCESS);

        try (var stream = Files.walk(projectDir.resolve("build/obj")).filter(Files::isRegularFile)) {
            assertThat( stream.count() ).isEqualTo(2);
        }

        GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache","clean")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        final var recompile = GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache","compile")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertThat( recompile.task(":compile").getOutcome() ).isEqualTo(FROM_CACHE);
    }
}
