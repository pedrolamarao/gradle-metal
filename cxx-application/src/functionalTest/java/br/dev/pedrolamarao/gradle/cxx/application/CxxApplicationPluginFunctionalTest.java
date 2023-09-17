package br.dev.pedrolamarao.gradle.cxx.application;

import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CxxApplicationPluginFunctionalTest
{
    @TempDir
    Path projectDir;

    @Test
    public void apply () throws IOException
    {
        Files.createDirectories(projectDir);

        final var buildGradleKts =
            """
            plugins {
                id("br.dev.pedrolamarao.cxx.application")
            }
            """;

        Files.writeString(projectDir.resolve("build.gradle.kts"), buildGradleKts);

        GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();
    }

    @Test
    public void compile () throws IOException
    {
        Files.createDirectories(projectDir);

        final var buildGradleKts =
            """
                plugins {
                    id("br.dev.pedrolamarao.cxx.application")
                }
                            
                cxx {
                    sourceSets {
                        create("main") {
                            srcDir(file("src/main/cpp"))
                        }
                    }
                }
                """;

        Files.writeString(projectDir.resolve("build.gradle.kts"), buildGradleKts);

        Files.createDirectories(projectDir.resolve("src/main/cpp"));

        final var mainCpp =
            """
            int main (int argc, char * argv[])
            {
                return 0;
            }
            """;

        Files.writeString(projectDir.resolve("src/main/cpp/main.cpp"),mainCpp);

        GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .withArguments("compile")
            .build();

        assertTrue( Files.exists( projectDir.resolve("build/obj/src/main/cpp/main.cpp.o") ) );
    }
}
