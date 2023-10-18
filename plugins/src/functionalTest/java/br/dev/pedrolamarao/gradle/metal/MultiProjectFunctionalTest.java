package br.dev.pedrolamarao.gradle.metal;

import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

public class MultiProjectFunctionalTest
{
    @TempDir
    Path projectDir;

    /**
     * There are two archive projects, foo and bar, with main C++ sources.
     * Both foo and bar test source set is empty.
     * Bar depends on foo.
     * Running <code>check</code> must not try to run nonexistent test applications.
     * @see <a href="https://github.com/pedrolamarao/gradle-metal/issues/52">https://github.com/pedrolamarao/gradle-metal/issues/52</a>
     */
    @Test
    void issue52 () throws Exception
    {
        final var fooDir = projectDir.resolve("foo");
        Files.createDirectories(fooDir);
        Files.writeString(fooDir.resolve("build.gradle.kts"),
        """
        plugins {
            id("br.dev.pedrolamarao.metal.archive")
            id("br.dev.pedrolamarao.metal.cpp")
            id("br.dev.pedrolamarao.metal.cxx")
        }
        
        metal {
            cpp {
                main {
                    public = true
                }
            }
        }
        """);

        final var barDir = projectDir.resolve("bar");
        Files.createDirectories(barDir);
        Files.writeString(barDir.resolve("build.gradle.kts"),
            """
            plugins {
                id("br.dev.pedrolamarao.metal.archive")
                id("br.dev.pedrolamarao.metal.cxx")
            }
            
            dependencies {
                implementation(project(":foo"))
            }
            """);

        final var fooCppDir = fooDir.resolve("src/main/cpp");
        Files.createDirectories(fooCppDir);
        Files.writeString(fooCppDir.resolve("foo.h"),
    """
        #pragma once
        
        int foo (int argc, char * argv[]);
        """);

        final var fooCxxDir = fooDir.resolve("src/main/cxx");
        Files.createDirectories(fooCxxDir);
        Files.writeString(fooCxxDir.resolve("foo.cxx"),
    """
        #include <foo.h>
        
        int foo (int argc, char * argv[])
        {
            return 0;
        }
        """);

        final var barCxxDir = projectDir.resolve("bar/main/cxx");
        Files.createDirectories(barCxxDir);
        Files.writeString(barCxxDir.resolve("bar.cxx"),
    """
        #include <foo.h>
        
        int bar (int argc, char * argv[])
        {
            return foo(argc,argv);
        }
        """);

        Files.writeString(projectDir.resolve("build.gradle.kts"),
    """
        plugins {
            id("base")
        }
        """);

        Files.writeString(projectDir.resolve("settings.gradle.kts"),
    """
        include("bar")
        include("foo")
        """);

        GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .withArguments("check")
            .build();
    }
}
