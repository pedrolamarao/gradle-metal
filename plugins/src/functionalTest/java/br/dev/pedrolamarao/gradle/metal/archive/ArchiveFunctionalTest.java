package br.dev.pedrolamarao.gradle.metal.archive;

import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ArchiveFunctionalTest
{
    @TempDir Path projectDir;

    @Test
    public void apply () throws IOException
    {
        Files.writeString(projectDir.resolve("build.gradle.kts"),
        """
        plugins {
             id("br.dev.pedrolamarao.metal.archive")
        }
        
        metal {
            archives {
                main {
                    archiveOptions = listOf()
                }
            }
        }
        """
        );

        GradleRunner.create()
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
             id("br.dev.pedrolamarao.metal.archive")
             id("br.dev.pedrolamarao.metal.%s")
        }
         
        metal {
            %s {
                main {
                    compileOptions = listOf()
                }
            }
            archives {
                main {
                    archiveOptions = listOf()
                }
            }
        }
        """.formatted(language,language)
        );

        GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();
    }

    @Test
    public void testCxx () throws IOException
    {
        final var testLanguageDir = projectDir.resolve("src/test/cxx");

        Files.createDirectories(testLanguageDir);

        Files.writeString(projectDir.resolve("build.gradle.kts"),
        """
        plugins {
            id("br.dev.pedrolamarao.metal.archive")
            id("br.dev.pedrolamarao.metal.cxx")
        }
        """
        );

        Files.writeString(testLanguageDir.resolve("test.cxx"),
        """
        int main (int argc, char * argv[])
        {
            return 0;
        }
        """
        );

        GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .withArguments("run-test")
            .build();
    }
}
