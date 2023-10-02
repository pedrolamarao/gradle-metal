package br.dev.pedrolamarao.gradle.metal.base;

import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CommandsFunctionalTest
{
    @TempDir Path projectDir;

    @Test
    public void apply () throws IOException
    {
        Files.writeString(projectDir.resolve("build.gradle.kts"),
        """
        plugins {
             id("br.dev.pedrolamarao.metal.commands")
        }
        """
        );

        GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .withDebug(true)
            .withArguments("commands")
            .build();
    }

    @ParameterizedTest
    @ValueSource(strings={"asm","c","cxx","ixx"})
    public void language (String language) throws IOException
    {
        final var subprojectDir = projectDir.resolve("sub");
        final var subprojectSourceDir = subprojectDir.resolve("src/main/%s".formatted(language));

        Files.createDirectories(subprojectSourceDir);

        Files.writeString(subprojectDir.resolve("build.gradle.kts"),
        """
        plugins {
             id("br.dev.pedrolamarao.metal.%s")
        }
         
        metal {
            %s {
                create("main")
            }
        }
        """.formatted(language,language)
        );

        Files.writeString(subprojectSourceDir.resolve("foo.%s".formatted(language)),
        """
        """
        );

        Files.writeString(projectDir.resolve("build.gradle.kts"),
        """
        plugins {
             id("br.dev.pedrolamarao.metal.commands")
        }
        
        dependencies {
            commands(project(":sub"))
        }
        """.formatted(language,language)
        );

        Files.writeString(projectDir.resolve("settings.gradle.kts"),
    """
        include("sub")
        """
        );

        GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .withDebug(true)
            .withArguments("commands")
            .build();

        assertTrue( Files.exists( projectDir.resolve("compile_commands.json") ) );
    }
}
