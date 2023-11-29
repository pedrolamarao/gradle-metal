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
                id("br.dev.pedrolamarao.metal.library")
                id("br.dev.pedrolamarao.metal.cxx")
            }
            
            library {
                compileOptions = listOf("-std=c++20")
            }
            """
        );

        final var libraryHeaderDir = libraryDir.resolve("src/main/cpp");
        Files.createDirectories(libraryHeaderDir);
        Files.writeString(libraryHeaderDir.resolve("foo.h"),
            """
            int foo ();
            """
        );

        final var libraryModuleDir = libraryDir.resolve("src/main/ixx");
        Files.createDirectories(libraryModuleDir);
        Files.writeString(libraryModuleDir.resolve("bar.ixx"),
            """
            module;
            
            #include <foo.h>
            
            export module bar;
            
            export int bar () { return foo(); }
            """
        );

        final var librarySourceDir = libraryDir.resolve("src/main/cxx");
        Files.createDirectories(librarySourceDir);
        Files.writeString(librarySourceDir.resolve("foo.cxx"),
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
            
            application {
                compileOptions = listOf("-std=c++20")
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
            
            import bar;
            
            int main (int argc, char * argv [])
            {
                return foo() + bar();
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
            .withArguments("--build-cache","--configuration-cache","--info",metalPathProperty,":application:link")
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
                id("br.dev.pedrolamarao.metal.library")
                id("br.dev.pedrolamarao.metal.cxx")
            }
            
            group = "library"
            version = "1.0"
            
            library {
                compileOptions = listOf("-std=c++20")
            }
            """
        );

        final var libraryHeaderDir = libraryDir.resolve("src/main/cpp");
        Files.createDirectories(libraryHeaderDir);
        Files.writeString(libraryHeaderDir.resolve("foo.h"),
            """
            int foo ();
            """
        );

        final var libraryModuleDir = libraryDir.resolve("src/main/ixx");
        Files.createDirectories(libraryModuleDir);
        Files.writeString(libraryModuleDir.resolve("bar.ixx"),
            """
            module;
            
            #include <foo.h>
            
            export module bar;
            
            export int bar () { return foo(); }
            """
        );

        final var librarySourceDir = libraryDir.resolve("src/main/cxx");
        Files.createDirectories(librarySourceDir);
        Files.writeString(librarySourceDir.resolve("foo.cxx"),
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
            
            application {
                compileOptions = listOf("-std=c++20")
            }
            
            dependencies {
                implementation("library:library:1.0")
            }
            """
        );

        final var applicationSourceDir = applicationDir.resolve("src/main/cxx");
        Files.createDirectories(applicationSourceDir);
        Files.writeString(applicationSourceDir.resolve("main.cxx"),
            """
            #include <foo.h>
            
            import bar;
            
            int main (int argc, char * argv [])
            {
                return foo() + bar();
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
            .withArguments("--build-cache","--configuration-cache","--info",metalPathProperty,":application:link")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertThat( link.task(":application:link").getOutcome() ).isEqualTo( SUCCESS );
    }

    @Test
    public void libraryTest () throws IOException
    {
        final var fooDir = projectDir.resolve("foo");
        Files.createDirectories(fooDir);
        Files.writeString(fooDir.resolve("build.gradle.kts"),
            """
            plugins {
                id("br.dev.pedrolamarao.metal.library")
                id("br.dev.pedrolamarao.metal.cxx")
            }
            
            library {
                compileOptions = listOf("-std=c++20")
            }
            """
        );

        final var fooHeaderDir = fooDir.resolve("src/main/cpp");
        Files.createDirectories(fooHeaderDir);
        Files.writeString(fooHeaderDir.resolve("foo.h"),
            """
            int foo ();
            """
        );

        final var fooModuleDir = fooDir.resolve("src/main/ixx");
        Files.createDirectories(fooModuleDir);
        Files.writeString(fooModuleDir.resolve("foo.ixx"),
            """
            module;
            
            #include <foo.h>
            
            export module foo;
            
            export int foo2 () { return foo(); }
            """
        );

        final var fooSourceDir = fooDir.resolve("src/main/cxx");
        Files.createDirectories(fooSourceDir);
        Files.writeString(fooSourceDir.resolve("foo.cxx"),
            """
            #include <foo.h>
            
            int foo () { return 0; }
            """
        );

        final var barDir = projectDir.resolve("bar");
        Files.createDirectories(barDir);
        Files.writeString(barDir.resolve("build.gradle.kts"),
            """
            plugins {
                id("br.dev.pedrolamarao.metal.library")
                id("br.dev.pedrolamarao.metal.cxx")
            }
            
            library {
                compileOptions = listOf("-std=c++20")
            }
            
            dependencies {
                testImplementation(project(":foo"))
            }
            """
        );

        final var barModuleDir = barDir.resolve("src/main/ixx");
        Files.createDirectories(barModuleDir);
        Files.writeString(barModuleDir.resolve("bar.ixx"),
            """
            export module bar;
            
            export int bar ();
            """
        );

        final var barSourceDir = barDir.resolve("src/main/cxx");
        Files.createDirectories(barSourceDir);
        Files.writeString(barSourceDir.resolve("bar.cxx"),
            """            
            module bar;
            
            int bar ()
            {
                return 0;
            }
            """
        );

        final var barTestSourceDir = barDir.resolve("src/test/cxx");
        Files.createDirectories(barTestSourceDir);
        Files.writeString(barTestSourceDir.resolve("main.cxx"),
            """
            #include <foo.h>
            
            import foo;
            import bar;
            
            int main (int argc, char * argv[])
            {
                return foo() + foo2() + bar();
            }
            """
        );

        Files.writeString(projectDir.resolve("settings.gradle.kts"),
            """
            include("foo")
            include("bar")
            """
        );

        final var check = GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache","--info",metalPathProperty,":bar:check")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertThat( check.task(":bar:check").getOutcome() ).isEqualTo( SUCCESS );
    }
}
