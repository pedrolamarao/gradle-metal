// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal;

import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.gradle.testkit.runner.TaskOutcome.*;

@DisplayName("Gradle Metal application project")
class MetalApplicationTest extends MetalTestBase
{
    @DisplayName("compile with Assembler sources")
    @Test
    void compileAsm () throws IOException
    {
        Files.createDirectories(projectDir.resolve("src/main/asm"));

        Files.writeString(projectDir.resolve("src/main/asm/main.s"),
            """
            .global main
            main:
                ret
            """
        );

        Files.writeString(projectDir.resolve("build.gradle.kts"),
            """
            plugins {
                id("br.dev.pedrolamarao.metal.application")
                id("br.dev.pedrolamarao.metal.asm")
            }
            """
        );

        final var compile = GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache","compileAsm")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertThat( compile.task(":compileAsm").getOutcome() ).isEqualTo(SUCCESS);

        try (var stream = Files.walk(projectDir.resolve("build/obj/main/asm")).filter(Files::isRegularFile)) {
            assertThat( stream.count() ).isEqualTo(1);
        }

        GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache","clean")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        final var recompile = GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache","compileAsm")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertThat( recompile.task(":compileAsm").getOutcome() ).isEqualTo(FROM_CACHE);

        final var link = GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache","link")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertThat( link.task(":link").getOutcome() ).isEqualTo(SUCCESS);

        try (var stream = Files.walk(projectDir.resolve("build/exe/main")).filter(Files::isRegularFile)) {
            assertThat( stream.count() ).isEqualTo(1);
        }

        GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache","clean")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        final var relink = GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache","link")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertThat( relink.task(":link").getOutcome() ).isEqualTo(FROM_CACHE);

        try (var stream = Files.walk(projectDir.resolve("build/exe/main")).filter(Files::isRegularFile)) {
            assertThat( stream.count() ).isEqualTo(1);
        }
    }

    @DisplayName("compile with filtered Assembler sources")
    @Test
    void compileFilteredAsm () throws IOException
    {
        Files.createDirectories(projectDir.resolve("src/main/asm"));

        Files.writeString(projectDir.resolve("src/main/asm/main.s"),
            """
            .global main
            main:
                ret
            """
        );

        Files.writeString(projectDir.resolve("src/main/asm/oops.s"),
            """
            this obviously cannot compile
            """
        );

        Files.writeString(projectDir.resolve("build.gradle.kts"),
            """
            plugins {
                id("br.dev.pedrolamarao.metal.application")
                id("br.dev.pedrolamarao.metal.asm")
            }
            
            application {
                exclude("oops.s")
            }
            """
        );

        final var compile = GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache","compileAsm")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertThat( compile.task(":compileAsm").getOutcome() ).isEqualTo(SUCCESS);

        try (var stream = Files.walk(projectDir.resolve("build/obj/main/asm")).filter(Files::isRegularFile)) {
            assertThat( stream.count() ).isEqualTo(1);
        }

        GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache","clean")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        final var recompile = GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache","compileAsm")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertThat( recompile.task(":compileAsm").getOutcome() ).isEqualTo(FROM_CACHE);

        final var link = GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache","link")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertThat( link.task(":link").getOutcome() ).isEqualTo(SUCCESS);

        try (var stream = Files.walk(projectDir.resolve("build/exe/main")).filter(Files::isRegularFile)) {
            assertThat( stream.count() ).isEqualTo(1);
        }

        GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache","clean")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        final var relink = GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache","link")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertThat( relink.task(":link").getOutcome() ).isEqualTo(FROM_CACHE);

        try (var stream = Files.walk(projectDir.resolve("build/exe/main")).filter(Files::isRegularFile)) {
            assertThat( stream.count() ).isEqualTo(1);
        }
    }

    @DisplayName("compile with C sources")
    @Test
    void compileC () throws IOException
    {
        Files.createDirectories(projectDir.resolve("src/main/cpp"));
        Files.writeString(projectDir.resolve("src/main/cpp/foo.h"),
            """
            int foo ();
            """
        );

        Files.createDirectories(projectDir.resolve("src/main/c"));
        Files.writeString(projectDir.resolve("src/main/c/foo.c"),
            """
            #include <foo.h>
            
            int foo ()
            {
                return 0;
            }
            """
        );
        Files.writeString(projectDir.resolve("src/main/c/main.c"),
            """
            #include <foo.h>
            
            int main (int argc, char * argv[])
            {
                return foo();
            }
            """
        );

        Files.writeString(projectDir.resolve("build.gradle.kts"),
            """
            plugins {
                id("br.dev.pedrolamarao.metal.application")
                id("br.dev.pedrolamarao.metal.c")
            }
            """
        );

        final var compile = GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache","compileC")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertThat( compile.task(":compileC").getOutcome() ).isEqualTo(SUCCESS);

        try (var stream = Files.walk(projectDir.resolve("build/obj/main/c")).filter(Files::isRegularFile)) {
            assertThat( stream.count() ).isEqualTo(2);
        }

        GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache","clean")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        final var recompile = GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache","compileC")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertThat( recompile.task(":compileC").getOutcome() ).isEqualTo(FROM_CACHE);

        final var link = GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache","link")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertThat( link.task(":link").getOutcome() ).isEqualTo(SUCCESS);

        try (var stream = Files.walk(projectDir.resolve("build/exe/main")).filter(Files::isRegularFile)) {
            assertThat( stream.count() ).isEqualTo(1);
        }

        GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache","clean")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        final var relink = GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache","link")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertThat( relink.task(":link").getOutcome() ).isEqualTo(FROM_CACHE);

        try (var stream = Files.walk(projectDir.resolve("build/exe/main")).filter(Files::isRegularFile)) {
            assertThat( stream.count() ).isEqualTo(1);
        }
    }

    @DisplayName("compile with filtered C sources")
    @Test
    void compileFilteredC () throws IOException
    {
        Files.createDirectories(projectDir.resolve("src/main/cpp"));
        Files.writeString(projectDir.resolve("src/main/cpp/foo.h"),
            """
            int foo ();
            """
        );

        Files.createDirectories(projectDir.resolve("src/main/c"));
        Files.writeString(projectDir.resolve("src/main/c/foo.c"),
            """
            #include <foo.h>
            
            int foo ()
            {
                return 0;
            }
            """
        );

        Files.writeString(projectDir.resolve("src/main/c/main.c"),
            """
            #include <foo.h>
            
            int main (int argc, char * argv[])
            {
                return foo();
            }
            """
        );

        Files.writeString(projectDir.resolve("src/main/c/oops.c"),
            """
            this obviously cannot compile
            """
        );

        Files.writeString(projectDir.resolve("build.gradle.kts"),
            """
            plugins {
                id("br.dev.pedrolamarao.metal.application")
                id("br.dev.pedrolamarao.metal.c")
            }
            
            application {
                exclude("oops.*")
            }
            """
        );

        final var compile = GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache","compileC")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertThat( compile.task(":compileC").getOutcome() ).isEqualTo(SUCCESS);

        try (var stream = Files.walk(projectDir.resolve("build/obj/main/c")).filter(Files::isRegularFile)) {
            assertThat( stream.count() ).isEqualTo(2);
        }

        GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache","clean")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        final var recompile = GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache","compileC")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertThat( recompile.task(":compileC").getOutcome() ).isEqualTo(FROM_CACHE);

        final var link = GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache","link")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertThat( link.task(":link").getOutcome() ).isEqualTo(SUCCESS);

        try (var stream = Files.walk(projectDir.resolve("build/exe/main")).filter(Files::isRegularFile)) {
            assertThat( stream.count() ).isEqualTo(1);
        }

        GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache","clean")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        final var relink = GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache","link")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertThat( relink.task(":link").getOutcome() ).isEqualTo(FROM_CACHE);

        try (var stream = Files.walk(projectDir.resolve("build/exe/main")).filter(Files::isRegularFile)) {
            assertThat( stream.count() ).isEqualTo(1);
        }
    }

    @DisplayName("compile with C++ sources")
    @Test
    void compileCxx () throws IOException
    {
        Files.createDirectories(projectDir.resolve("src/main/cpp"));
        Files.writeString(projectDir.resolve("src/main/cpp/foo.h"),
            """
            int foo ();
            """
        );

        Files.createDirectories(projectDir.resolve("src/main/ixx"));
        Files.writeString(projectDir.resolve("src/main/ixx/bar.ixx"),
            """
            module;
            
            #include <foo.h>
            
            export module bar;
            
            export int bar () { return foo(); }
            """
        );

        Files.createDirectories(projectDir.resolve("src/main/cxx"));
        Files.writeString(projectDir.resolve("src/main/cxx/foo.cxx"),
            """
            #include <foo.h>
            
            int foo ()
            {
                return 0;
            }
            """
        );
        Files.writeString(projectDir.resolve("src/main/cxx/main.cxx"),
            """
            import bar;
            
            int main (int argc, char * argv[])
            {
                return bar();
            }
            """
        );

        Files.writeString(projectDir.resolve("build.gradle.kts"),
            """
            plugins {
                id("br.dev.pedrolamarao.metal.application")
                id("br.dev.pedrolamarao.metal.cxx")
            }
            
            application {
                compileOptions = listOf("-std=c++20")
            }
            """
        );

        final var compile = GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache","--info","compileCxx")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertThat( compile.task(":compileCxx").getOutcome() ).isEqualTo(SUCCESS);

        try (var stream = Files.walk(projectDir.resolve("build/obj/main/cxx")).filter(Files::isRegularFile)) {
            assertThat( stream.count() ).isEqualTo(3);
        }

        GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache","clean")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        final var recompile = GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache","compileCxx")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertThat( recompile.task(":compileCxx").getOutcome() ).isEqualTo(FROM_CACHE);

        final var link = GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache","link")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertThat( link.task(":link").getOutcome() ).isEqualTo(SUCCESS);

        try (var stream = Files.walk(projectDir.resolve("build/exe/main")).filter(Files::isRegularFile)) {
            assertThat( stream.count() ).isEqualTo(1);
        }

        GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache","clean")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        final var relink = GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache","link")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertThat( relink.task(":link").getOutcome() ).isEqualTo(FROM_CACHE);

        try (var stream = Files.walk(projectDir.resolve("build/exe/main")).filter(Files::isRegularFile)) {
            assertThat( stream.count() ).isEqualTo(1);
        }
    }

    @DisplayName("compile with filtered C++ sources")
    @Test
    void compileFilteredCxx () throws IOException
    {
        Files.createDirectories(projectDir.resolve("src/main/cpp"));
        Files.writeString(projectDir.resolve("src/main/cpp/foo.h"),
            """
            int foo ();
            """
        );

        Files.createDirectories(projectDir.resolve("src/main/ixx"));
        Files.writeString(projectDir.resolve("src/main/ixx/bar.ixx"),
            """
            module;
            
            #include <foo.h>
            
            export module bar;
            
            export int bar () { return foo(); }
            """
        );

        Files.createDirectories(projectDir.resolve("src/main/ixx"));
        Files.writeString(projectDir.resolve("src/main/ixx/oops.ixx"),
            """
            this obviously cannot compile
            """
        );

        Files.createDirectories(projectDir.resolve("src/main/cxx"));
        Files.writeString(projectDir.resolve("src/main/cxx/foo.cxx"),
            """
            #include <foo.h>
            
            int foo ()
            {
                return 0;
            }
            """
        );

        Files.writeString(projectDir.resolve("src/main/cxx/main.cxx"),
            """
            import bar;
            
            int main (int argc, char * argv[])
            {
                return bar();
            }
            """
        );

        Files.writeString(projectDir.resolve("src/main/cxx/oops.cxx"),
            """
            this obviously cannot compile
            """
        );

        Files.writeString(projectDir.resolve("build.gradle.kts"),
            """
            plugins {
                id("br.dev.pedrolamarao.metal.application")
                id("br.dev.pedrolamarao.metal.cxx")
            }
            
            application {
                compileOptions = listOf("-std=c++20")
                exclude("oops.*")
            }
            """
        );

        final var compile = GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache","--info","compileCxx")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertThat( compile.task(":compileCxx").getOutcome() ).isEqualTo(SUCCESS);

        try (var stream = Files.walk(projectDir.resolve("build/obj/main/cxx")).filter(Files::isRegularFile)) {
            assertThat( stream.count() ).isEqualTo(3);
        }

        GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache","clean")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        final var recompile = GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache","compileCxx")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertThat( recompile.task(":compileCxx").getOutcome() ).isEqualTo(FROM_CACHE);

        final var link = GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache","link")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertThat( link.task(":link").getOutcome() ).isEqualTo(SUCCESS);

        try (var stream = Files.walk(projectDir.resolve("build/exe/main")).filter(Files::isRegularFile)) {
            assertThat( stream.count() ).isEqualTo(1);
        }

        GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache","clean")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        final var relink = GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache","link")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertThat( relink.task(":link").getOutcome() ).isEqualTo(FROM_CACHE);

        try (var stream = Files.walk(projectDir.resolve("build/exe/main")).filter(Files::isRegularFile)) {
            assertThat( stream.count() ).isEqualTo(1);
        }
    }

    @DisplayName("compile with no sources")
    @Test
    void empty () throws IOException
    {
        Files.writeString(projectDir.resolve("build.gradle.kts"),
            """         
            plugins {
                id("br.dev.pedrolamarao.metal.application")
                id("br.dev.pedrolamarao.metal.asm")
                id("br.dev.pedrolamarao.metal.c")
                id("br.dev.pedrolamarao.metal.cxx")
            }
            """
        );

        final var assemble = GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache","assemble")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertThat( assemble.task(":compileAsm").getOutcome() ).isEqualTo( NO_SOURCE );
        assertThat( assemble.task(":compileC").getOutcome()   ).isEqualTo( NO_SOURCE );
        assertThat( assemble.task(":compileCxx").getOutcome() ).isEqualTo( NO_SOURCE );
        assertThat( assemble.task(":link").getOutcome()       ).isEqualTo( NO_SOURCE );
        assertThat( assemble.task(":assemble").getOutcome()   ).isEqualTo( UP_TO_DATE );

        final var check = GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache","run")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertThat( check.task(":run").getOutcome() ).isEqualTo( SKIPPED );
    }

    @DisplayName("link a large source set")
    @Test
    void largeSourceSet () throws IOException
    {
        Files.createDirectories(projectDir.resolve("src/main/cpp"));
        Files.writeString(projectDir.resolve("src/main/cpp/foo.h"),
            """
            int foo (int i);
            """
        );

        Files.createDirectories(projectDir.resolve("src/main/c"));
        Files.writeString(projectDir.resolve("src/main/c/foo.c"),
            """
            #include <foo.h>
            int foo (int i) { return i % 42; }
            """
        );
        for (int i = 0, j = 512; i != j; ++i)
        {
            Files.writeString(projectDir.resolve("src/main/c/%d.c".formatted(i)),
                """
                #include <foo.h>
                int foo_%d () { return foo(%d); }
                """.formatted(i,i)
            );
        }
        Files.writeString(projectDir.resolve("src/main/c/main.c"),
            """
            #include <foo.h>
            int main (int argc, char* argv[]) { return foo(0); }
            """
        );

        Files.writeString(projectDir.resolve("build.gradle.kts"),
            """
            plugins {
                id("br.dev.pedrolamarao.metal.application")
                id("br.dev.pedrolamarao.metal.c")
            }
            """
        );

        final var archive = GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache","link")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertThat( archive.task(":link").getOutcome() ).isEqualTo(SUCCESS);
    }
}
