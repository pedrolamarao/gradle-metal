// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal;

import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.gradle.testkit.runner.TaskOutcome.UP_TO_DATE;

@DisplayName("Gradle Metal extension")
class MetalExtensionTest extends MetalTestBase
{
    @Test
    void archiveFileName () throws IOException
    {
        Files.writeString(projectDir.resolve("build.gradle.kts"),
            """         
            plugins {
                id("br.dev.pedrolamarao.metal.base")
            }
            
            val item = metal.archiveFileName("archive")
            
            tasks.register("print").configure {
                print(item.get())
            }
            """
        );

        final var test = GradleRunner.create()
            .withArguments("--build-cache", "--configuration-cache", "--quiet",":print")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertThat( test.task(":print").getOutcome() ).isEqualTo( UP_TO_DATE );
        assertThat( test.getOutput() ).contains( "archive" );
    }

    @Test
    void locateTool () throws IOException
    {
        Files.writeString(projectDir.resolve("build.gradle.kts"),
            """         
            plugins {
                id("br.dev.pedrolamarao.metal.base")
            }
            
            val item = metal.locateTool("clang")
            
            tasks.register("print").configure {
                print(item.get())
            }
            """
        );

        final var test = GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache","--quiet",":print")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertThat( test.task(":print").getOutcome() ).isEqualTo( UP_TO_DATE );
        assertThat( test.getOutput() ).contains( "clang" );
    }
}
