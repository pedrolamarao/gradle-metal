package br.dev.pedrolamarao.gradle.metal.application;

import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ApplicationFunctionalTest
{
    @TempDir Path projectDir;

    @Test
    public void apply () throws IOException
    {
        Files.writeString(projectDir.resolve("build.gradle.kts"),
        """
        plugins {
             id("br.dev.pedrolamarao.metal.application")
        }
         
        metal {
            applications {
                main {
                    linkOptions = listOf()
                }
            }
        }
        """
        );

        GradleRunner.create()
            .withArguments("--configuration-cache")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();
    }

    @ParameterizedTest
    @ValueSource(strings={"asm","c","cpp","cxx","ixx"})
    public void language (String language) throws IOException
    {
        Files.writeString(projectDir.resolve("build.gradle.kts"),
        """
        plugins {
             id("br.dev.pedrolamarao.metal.application")
             id("br.dev.pedrolamarao.metal.%s")
        }
         
        metal {
            %s {
                main {
                    compileOptions = listOf()
                }
            }
            applications {
                main {
                    archiveOptions = listOf()
                }
            }
        }
        """.formatted(language,language)
        );

        GradleRunner.create()
            .withArguments("--configuration-cache")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();
    }

    @Test
    public void languageC () throws IOException
    {
        final var languageDir = projectDir.resolve("src/main/c");

        Files.createDirectories(languageDir);

        Files.writeString(languageDir.resolve("main.c"),
        """
        int main (int argc, char * argv[])
        {
            return 0;
        }
        """);

        Files.writeString(projectDir.resolve("build.gradle.kts"),
        """
        plugins {
             id("br.dev.pedrolamarao.metal.application")
             id("br.dev.pedrolamarao.metal.c")
        }
        """
        );

        GradleRunner.create()
            .withArguments("--configuration-cache","run-main")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();
    }

    @Test
    public void languageCxx () throws IOException
    {
        final var languageDir = projectDir.resolve("src/main/cxx");

        Files.createDirectories(languageDir);

        Files.writeString(languageDir.resolve("main.cxx"),
    """
        int main (int argc, char * argv[])
        {
            return 0;
        }
        """);

        Files.writeString(projectDir.resolve("build.gradle.kts"),
        """
        plugins {
             id("br.dev.pedrolamarao.metal.application")
             id("br.dev.pedrolamarao.metal.cxx")
        }
        """
        );

        GradleRunner.create()
            .withArguments("--configuration-cache","run-main")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();
    }
}
