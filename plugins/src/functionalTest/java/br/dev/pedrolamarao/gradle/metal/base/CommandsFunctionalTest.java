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

    @Test
    public void cxxWithCpp () throws IOException
    {
        final var subDir = projectDir.resolve("sub");
        final var cppDir = subDir.resolve("src/main/cpp");
        final var ixxDir = subDir.resolve("src/main/cxx");

        Files.createDirectories(cppDir);
        Files.createDirectories(ixxDir);

        Files.writeString(cppDir.resolve("foo.h"),
        """
        #pragma once
        
        int foo (int argc, char * argv[]);
        """
        );

        Files.writeString(ixxDir.resolve("foo.cxx"),
        """        
        #include <foo.h>
        
        int foo (int argc, char * argv[])
        {
            return 0;
        }
        """
        );

        Files.writeString(subDir.resolve("build.gradle.kts"),
        """
        plugins {
             id("br.dev.pedrolamarao.metal.archive")
             id("br.dev.pedrolamarao.metal.cpp")
             id("br.dev.pedrolamarao.metal.cxx")
        }
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
        """
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

    @Test
    public void ixxWithCpp () throws IOException
    {
        final var subDir = projectDir.resolve("sub");
        final var cppDir = subDir.resolve("src/main/cpp");
        final var ixxDir = subDir.resolve("src/main/ixx");

        Files.createDirectories(cppDir);
        Files.createDirectories(ixxDir);

        Files.writeString(cppDir.resolve("foo.h"),
        """
        #pragma once
        
        int foo (int argc, char * argv[]);
        """
        );

        Files.writeString(ixxDir.resolve("bar.ixx"),
        """
        module;
        
        #include <foo.h>
        
        export module bar;
        
        export int bar (int argc, char * argv[]);
        """
        );

        Files.writeString(subDir.resolve("build.gradle.kts"),
        """
        plugins {
             id("br.dev.pedrolamarao.metal.archive")
             id("br.dev.pedrolamarao.metal.cpp")
             id("br.dev.pedrolamarao.metal.ixx")
        }
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
        """
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
