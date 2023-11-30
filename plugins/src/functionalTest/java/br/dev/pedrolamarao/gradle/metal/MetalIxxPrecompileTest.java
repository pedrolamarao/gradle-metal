// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal;

import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.gradle.testkit.runner.TaskOutcome.FROM_CACHE;
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS;

public class MetalIxxPrecompileTest extends MetalTestBase
{
    @Test
    public void compile () throws IOException
    {
        Files.createDirectories(projectDir.resolve("src"));

        Files.writeString(projectDir.resolve("src/foo.ixx"),
            """
            export module foo;
            
            export int foo () { return 0; }
            """
        );

        Files.writeString(projectDir.resolve("src/bar.ixx"),
            """
            export module bar;
            
            import foo;
            
            export int bar () { return foo(); }
            """
        );

        Files.writeString(projectDir.resolve("build.gradle.kts"),
            """
            import br.dev.pedrolamarao.gradle.metal.MetalIxxPrecompile
            
            plugins {
                id("base")
                id("br.dev.pedrolamarao.metal.base")
            }
            
            val compile = tasks.register<MetalIxxPrecompile>("compile") {
                options = listOf("-std=c++20")
                outputDirectory = layout.buildDirectory.dir("obj")
                source = layout.projectDirectory.dir("src").asFileTree
            }
            """
        );

        final var compile = GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache",metalPathProperty,"compile")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertThat( compile.task(":compile").getOutcome() ).isEqualTo(SUCCESS);

        try (var stream = Files.walk(projectDir.resolve("build/obj")).filter(Files::isRegularFile)) {
            assertThat( stream.count() ).isEqualTo(2);
        }

        GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache",metalPathProperty,"clean")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        final var recompile = GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache",metalPathProperty,"compile")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertThat( recompile.task(":compile").getOutcome() ).isEqualTo(FROM_CACHE);
    }
}
