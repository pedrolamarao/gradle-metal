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
                named("main") {
                    archiveOptions = listOf()
                }
            }
        }
        """
        );

        GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .withDebug(true)
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
                named("main") {
                    compileOptions = listOf()
                }
            }
            archives {
                named("main") {
                    archiveOptions = listOf()
                }
            }
        }
        """.formatted(language,language)
        );

        GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .withDebug(true)
            .build();
    }
}
