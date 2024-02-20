// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal;

import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.gradle.testkit.runner.TaskOutcome.FROM_CACHE;
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS;

public class MetalCxxCompileTest extends MetalTestBase
{
    @Test
    public void compile () throws IOException
    {
        Files.createDirectories(projectDir.resolve("src"));

        Files.writeString(projectDir.resolve("src/foo.cxx"),
            """
            int foo () { return 0; }
            """
        );

        Files.writeString(projectDir.resolve("src/bar.cxx"),
            """
            int bar () { return 0; }
            """
        );

        Files.writeString(projectDir.resolve("build.gradle.kts"),
            """
            import br.dev.pedrolamarao.gradle.metal.MetalCxxCompile
            
            plugins {
                id("base")
                id("br.dev.pedrolamarao.metal.base")
            }
            
            val compile = tasks.register<MetalCxxCompile>("compile") {
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
