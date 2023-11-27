package br.dev.pedrolamarao.gradle.metal;

import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS;

public class MetalDependencyTest extends MetalTestBase
{
    @Test
    public void subproject () throws IOException
    {
        final var libraryDir = projectDir.resolve("library");
        Files.createDirectories(libraryDir);
        Files.writeString(libraryDir.resolve("build.gradle.kts"),
            """
            plugins {
                id("base")
                id("br.dev.pedrolamarao.metal.library")
                id("br.dev.pedrolamarao.metal.c")
            }
            """
        );

        final var libraryHeaderDir = libraryDir.resolve("src/main/cpp");
        Files.createDirectories(libraryHeaderDir);
        Files.writeString(libraryHeaderDir.resolve("foo.h"),
            """
            #if defined(__cplusplus)
            extern "C" {
            #endif
            
            int foo ();
            
            #if defined(__cplusplus)
            }
            #endif
            """
        );

        final var librarySourceDir = libraryDir.resolve("src/main/c");
        Files.createDirectories(librarySourceDir);
        Files.writeString(librarySourceDir.resolve("foo.c"),
            """
            #include <foo.h>
            
            int foo () { return 0; }
            """
        );

        final var applicationDir = projectDir.resolve("application");
        Files.createDirectories(applicationDir);
        Files.writeString(applicationDir.resolve("build.gradle.kts"),
            """
            plugins {
                id("base")
                id("br.dev.pedrolamarao.metal.application")
                id("br.dev.pedrolamarao.metal.cxx")
            }
            
            dependencies {
                implementation(project(":library"))
            }
            """
        );

        final var applicationSourceDir = applicationDir.resolve("src/main/cxx");
        Files.createDirectories(applicationSourceDir);
        Files.writeString(applicationSourceDir.resolve("main.cxx"),
            """
            #include <foo.h>
            
            int main (int argc, char * argv [])
            {
                return foo();
            }
            """
        );

        Files.writeString(projectDir.resolve("settings.gradle.kts"),
            """
            include("application")
            include("library")
            """
        );

        final var link = GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache","--info","--stacktrace",metalPathProperty,":application:link")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertThat( link.task(":application:link").getOutcome() ).isEqualTo( SUCCESS );
    }
    @Test
    public void composite () throws IOException
    {
        final var libraryDir = projectDir.resolve("library");
        Files.createDirectories(libraryDir);
        Files.writeString(libraryDir.resolve("build.gradle.kts"),
            """
            plugins {
                id("base")
                id("br.dev.pedrolamarao.metal.library")
                id("br.dev.pedrolamarao.metal.c")
            }
            
            group = "library"
            version = "1.0"
            """
        );
        Files.writeString(libraryDir.resolve("settings.gradle.kts"),"");

        final var libraryHeaderDir = libraryDir.resolve("src/main/cpp");
        Files.createDirectories(libraryHeaderDir);
        Files.writeString(libraryHeaderDir.resolve("foo.h"),
            """
            #if defined(__cplusplus)
            extern "C" {
            #endif
            
            int foo ();
            
            #if defined(__cplusplus)
            }
            #endif
            """
        );

        final var librarySourceDir = libraryDir.resolve("src/main/c");
        Files.createDirectories(librarySourceDir);
        Files.writeString(librarySourceDir.resolve("foo.c"),
            """
            #include <foo.h>
            
            int foo () { return 0; }
            """
        );

        final var applicationDir = projectDir.resolve("application");
        Files.createDirectories(applicationDir);
        Files.writeString(applicationDir.resolve("build.gradle.kts"),
            """
            plugins {
                id("base")
                id("br.dev.pedrolamarao.metal.application")
                id("br.dev.pedrolamarao.metal.cxx")
            }
            
            dependencies {
                implementation("library:library:1.0")
            }
            """
        );
        Files.writeString(applicationDir.resolve("settings.gradle.kts"),"");

        final var applicationSourceDir = applicationDir.resolve("src/main/cxx");
        Files.createDirectories(applicationSourceDir);
        Files.writeString(applicationSourceDir.resolve("main.cxx"),
            """
            #include <foo.h>
            
            int main (int argc, char * argv [])
            {
                return foo();
            }
            """
        );

        Files.writeString(projectDir.resolve("settings.gradle.kts"),
            """
            includeBuild("application")
            includeBuild("library")
            """
        );

        final var link = GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache",metalPathProperty,":application:link")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertThat( link.task(":application:link").getOutcome() ).isEqualTo( SUCCESS );
    }
}
