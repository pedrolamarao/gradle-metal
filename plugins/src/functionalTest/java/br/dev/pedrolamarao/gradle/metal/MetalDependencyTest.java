// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

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
        // base library

        final var baseDir = projectDir.resolve("base");
        Files.createDirectories(baseDir);
        Files.writeString(baseDir.resolve("build.gradle.kts"),
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

        Files.createDirectories(baseDir.resolve("src/main/cpp"));
        Files.writeString(baseDir.resolve("src/main/cpp/base.h"),
            """
            int base ();
            """
        );

        Files.createDirectories(baseDir.resolve("src/main/ixx"));
        Files.writeString(baseDir.resolve("src/main/ixx/base.ixx"),
            """
            module;
            
            #include <base.h>
            
            export module base;
            
            export int mbase () { return base(); }
            """
        );

        Files.createDirectories(baseDir.resolve("src/main/cxx"));
        Files.writeString(baseDir.resolve("src/main/cxx/base.cxx"),
            """
            #include <base.h>
            
            int base () { return 0; }
            """
        );

        // intermediate library

        final var middleDir = projectDir.resolve("middle");
        Files.createDirectories(middleDir);
        Files.writeString(middleDir.resolve("build.gradle.kts"),
            """
            plugins {
                id("br.dev.pedrolamarao.metal.library")
                id("br.dev.pedrolamarao.metal.cxx")
            }
            
            library {
                compileOptions = listOf("-std=c++20")
            }
            
            dependencies {
                api(project(":base"))
            }
            """
        );

        Files.createDirectories(middleDir.resolve("src/main/cpp"));
        Files.writeString(middleDir.resolve("src/main/cpp/middle.h"),
            """
            int middle ();
            """
        );

        Files.createDirectories(middleDir.resolve("src/main/ixx"));
        Files.writeString(middleDir.resolve("src/main/ixx/middle.ixx"),
            """
            module;
            
            #include <base.h>
            #include <middle.h>
            
            export module middle;
            
            import base;
           
            export int mmiddle () { return base() + mbase() + middle(); }
            """
        );

        Files.createDirectories(middleDir.resolve("src/main/cxx"));
        Files.writeString(middleDir.resolve("src/main/cxx/middle.cxx"),
            """
            #include <base.h>
            #include <middle.h>
            
            import base;
            
            int middle () { return base() + mbase(); }
            """
        );

        Files.createDirectories(middleDir.resolve("src/test/cxx"));
        Files.writeString(middleDir.resolve("src/test/cxx/main.cxx"),
            """
            #include <base.h>
            #include <middle.h>
            
            import base;
            import middle;
            
            int main (int argc, char * argv [])
            {
                return base() + mbase() + middle() + mmiddle();
            }
            """
        );

        // application

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
                implementation(project(":middle"))
            }
            """
        );

        final var applicationSourceDir = applicationDir.resolve("src/main/cxx");
        Files.createDirectories(applicationSourceDir);
        Files.writeString(applicationSourceDir.resolve("main.cxx"),
            """
            #include <base.h>
            #include <middle.h>
            
            import base;
            import middle;
            
            int main (int argc, char * argv [])
            {
                return base() + mbase() + middle() + mmiddle();
            }
            """
        );

        Files.writeString(projectDir.resolve("settings.gradle.kts"),
            """
            include("application")
            include("base")
            include("middle")
            """
        );

        final var check = GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache","--info",":middle:check")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertThat( check.task(":middle:check").getOutcome() ).isEqualTo( SUCCESS );

        final var run = GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache","--info",":application:run")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertThat( run.task(":application:run").getOutcome() ).isEqualTo( SUCCESS );
    }

    @Test
    public void composite () throws IOException
    {
        // base library

        final var baseDir = projectDir.resolve("base");
        Files.createDirectories(baseDir);
        Files.writeString(baseDir.resolve("build.gradle.kts"),
            """
            plugins {
                id("br.dev.pedrolamarao.metal.library")
                id("br.dev.pedrolamarao.metal.cxx")
            }
            
            group = "base"
            version = "1.0"
            
            library {
                compileOptions = listOf("-std=c++20")
            }
            """
        );

        Files.createDirectories(baseDir.resolve("src/main/cpp"));
        Files.writeString(baseDir.resolve("src/main/cpp/base.h"),
            """
            int base ();
            """
        );

        Files.createDirectories(baseDir.resolve("src/main/ixx"));
        Files.writeString(baseDir.resolve("src/main/ixx/base.ixx"),
            """
            module;
            
            #include <base.h>
            
            export module base;
            
            export int mbase () { return base(); }
            """
        );

        Files.createDirectories(baseDir.resolve("src/main/cxx"));
        Files.writeString(baseDir.resolve("src/main/cxx/base.cxx"),
            """
            #include <base.h>
            
            int base () { return 0; }
            """
        );

        Files.copy(projectDir.resolve("gradle.properties"),baseDir.resolve("gradle.properties"));

        // intermediate library

        final var middleDir = projectDir.resolve("middle");
        Files.createDirectories(middleDir);
        Files.writeString(middleDir.resolve("build.gradle.kts"),
            """
            plugins {
                id("br.dev.pedrolamarao.metal.library")
                id("br.dev.pedrolamarao.metal.cxx")
            }
            
            group = "middle"
            version = "1.0"
            
            library {
                compileOptions = listOf("-std=c++20")
            }
            
            dependencies {
                api("base:base:1.0")
            }
            """
        );

        Files.createDirectories(middleDir.resolve("src/main/cpp"));
        Files.writeString(middleDir.resolve("src/main/cpp/middle.h"),
            """
            int middle ();
            """
        );

        Files.createDirectories(middleDir.resolve("src/main/ixx"));
        Files.writeString(middleDir.resolve("src/main/ixx/middle.ixx"),
            """
            module;
            
            #include <base.h>
            #include <middle.h>
            
            export module middle;
            
            import base;
           
            export int mmiddle () { return base() + mbase() + middle(); }
            """
        );

        Files.createDirectories(middleDir.resolve("src/main/cxx"));
        Files.writeString(middleDir.resolve("src/main/cxx/middle.cxx"),
            """
            #include <base.h>
            #include <middle.h>
            
            import base;
            
            int middle () { return base() + mbase(); }
            """
        );

        Files.createDirectories(middleDir.resolve("src/test/cxx"));
        Files.writeString(middleDir.resolve("src/test/cxx/main.cxx"),
            """
            #include <base.h>
            #include <middle.h>
            
            import base;
            import middle;
            
            int main (int argc, char * argv [])
            {
                return base() + mbase() + middle() + mmiddle();
            }
            """
        );

        Files.copy(projectDir.resolve("gradle.properties"),middleDir.resolve("gradle.properties"));

        // application

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
                implementation("middle:middle:1.0")
            }
            """
        );

        final var applicationSourceDir = applicationDir.resolve("src/main/cxx");
        Files.createDirectories(applicationSourceDir);
        Files.writeString(applicationSourceDir.resolve("main.cxx"),
            """
            #include <base.h>
            #include <middle.h>
            
            import base;
            import middle;
            
            int main (int argc, char * argv [])
            {
                return base() + mbase() + middle() + mmiddle();
            }
            """
        );

        Files.copy(projectDir.resolve("gradle.properties"),applicationDir.resolve("gradle.properties"));

        Files.writeString(projectDir.resolve("settings.gradle.kts"),
            """
            includeBuild("application")
            includeBuild("base")
            includeBuild("middle")
            """
        );

        final var check = GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache","--info",":middle:check")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertThat( check.task(":middle:check").getOutcome() ).isEqualTo( SUCCESS );

        final var run = GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache","--info",":application:run")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertThat( run.task(":application:run").getOutcome() ).isEqualTo( SUCCESS );
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
            .withArguments("--build-cache","--configuration-cache","--info",":bar:check")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertThat( check.task(":bar:check").getOutcome() ).isEqualTo( SUCCESS );
    }
}
