package br.dev.pedrolamarao.gradle.cxx.language;

import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CxxSourceSetFunctionalTest
{
    @TempDir
    Path projectDir;

    @Test
    public void register () throws IOException
    {
        Files.createDirectories(projectDir);

        final var buildGradleKts =
            """
            plugins {
                id("br.dev.pedrolamarao.cxx.language")
            }
            
            cxx {
                sourceSets {
                    register("main")
                }
            }
            """;

        Files.writeString(projectDir.resolve("build.gradle.kts"), buildGradleKts);

        GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();
    }
}
