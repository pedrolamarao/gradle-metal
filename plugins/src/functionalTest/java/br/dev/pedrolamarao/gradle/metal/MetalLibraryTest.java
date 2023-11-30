// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal;

import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.gradle.testkit.runner.TaskOutcome.*;

public class MetalLibraryTest extends MetalTestBase
{
    @Test
    public void compileAsm () throws IOException
    {
        Files.createDirectories(projectDir.resolve("src/main/asm"));
        Files.writeString(projectDir.resolve("src/main/asm/foo.s"),
            """
            .intel_syntax
            .global foo
            foo:
                xor eax, eax
                ret
            """
        );

        Files.createDirectories(projectDir.resolve("src/test/asm"));
        Files.writeString(projectDir.resolve("src/test/asm/main.s"),
            """
            .intel_syntax
            .global main
            main:
                call foo
                ret
            """
        );

        Files.writeString(projectDir.resolve("build.gradle.kts"),
            """
            plugins {
                id("br.dev.pedrolamarao.metal.library")
                id("br.dev.pedrolamarao.metal.asm")
            }
            """
        );

        final var compile = GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache",metalPathProperty,"compileAsm")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertThat( compile.task(":compileAsm").getOutcome() ).isEqualTo(SUCCESS);

        try (var stream = Files.walk(projectDir.resolve("build/obj/main/asm")).filter(Files::isRegularFile)) {
            assertThat( stream.count() ).isEqualTo(1);
        }

        GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache",metalPathProperty,"clean")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        final var recompile = GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache",metalPathProperty,"compileAsm")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertThat( recompile.task(":compileAsm").getOutcome() ).isEqualTo(FROM_CACHE);

        final var archive = GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache",metalPathProperty,"archive")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertThat( archive.task(":archive").getOutcome() ).isEqualTo(SUCCESS);

        try (var stream = Files.walk(projectDir.resolve("build/lib/main")).filter(Files::isRegularFile)) {
            assertThat( stream.count() ).isEqualTo(1);
        }

        GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache",metalPathProperty,"clean")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        final var rearchive = GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache",metalPathProperty,"archive")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertThat( rearchive.task(":archive").getOutcome() ).isEqualTo(FROM_CACHE);

        try (var stream = Files.walk(projectDir.resolve("build/lib/main")).filter(Files::isRegularFile)) {
            assertThat( stream.count() ).isEqualTo(1);
        }

        final var check = GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache","--info",metalPathProperty,"check")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertThat( check.task(":check").getOutcome() ).isEqualTo(SUCCESS);
    }

    @Test
    public void compileC () throws IOException
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

        Files.createDirectories(projectDir.resolve("src/test/c"));
        Files.writeString(projectDir.resolve("src/test/c/main.c"),
            """
            #include <foo.h>
            
            int main (int argc, char *  argv[])
            {
                return foo();
            }
            """
        );

        Files.writeString(projectDir.resolve("build.gradle.kts"),
            """
            plugins {
                id("br.dev.pedrolamarao.metal.library")
                id("br.dev.pedrolamarao.metal.c")
            }
            """
        );

        final var compile = GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache",metalPathProperty,"compileC")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertThat( compile.task(":compileC").getOutcome() ).isEqualTo(SUCCESS);

        try (var stream = Files.walk(projectDir.resolve("build/obj/main/c")).filter(Files::isRegularFile)) {
            assertThat( stream.count() ).isEqualTo(1);
        }

        GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache",metalPathProperty,"clean")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        final var recompile = GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache",metalPathProperty,"compileC")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertThat( recompile.task(":compileC").getOutcome() ).isEqualTo(FROM_CACHE);

        final var archive = GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache",metalPathProperty,"archive")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertThat( archive.task(":archive").getOutcome() ).isEqualTo(SUCCESS);

        try (var stream = Files.walk(projectDir.resolve("build/lib/main")).filter(Files::isRegularFile)) {
            assertThat( stream.count() ).isEqualTo(1);
        }

        GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache",metalPathProperty,"clean")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        final var rearchive = GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache",metalPathProperty,"archive")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertThat( rearchive.task(":archive").getOutcome() ).isEqualTo(FROM_CACHE);

        try (var stream = Files.walk(projectDir.resolve("build/lib/main")).filter(Files::isRegularFile)) {
            assertThat( stream.count() ).isEqualTo(1);
        }

        final var check = GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache","--info",metalPathProperty,"check")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertThat( check.task(":check").getOutcome() ).isEqualTo(SUCCESS);
    }

    @Test
    public void compileCxx () throws IOException
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
            export module bar;
            
            export int bar ();
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
        Files.writeString(projectDir.resolve("src/main/cxx/bar.cxx"),
            """
            module;
            
            #include <foo.h>
            
            module bar;
            
            int bar ()
            {
                return foo();
            }
            """
        );

        Files.createDirectories(projectDir.resolve("src/test/cxx"));
        Files.writeString(projectDir.resolve("src/test/cxx/main.cxx"),
            """
            #include <foo.h>
            
            import bar;
            
            int main (int argc, char * argv[])
            {
                return foo() + bar();
            }
            """
        );
        Files.writeString(projectDir.resolve("build.gradle.kts"),
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

        final var compile = GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache",metalPathProperty,"compileCxx")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertThat( compile.task(":compileCxx").getOutcome() ).isEqualTo(SUCCESS);

        try (var stream = Files.walk(projectDir.resolve("build/bmi/main")).filter(Files::isRegularFile)) {
            assertThat( stream.count() ).isEqualTo(1);
        }

        try (var stream = Files.walk(projectDir.resolve("build/obj/main")).filter(Files::isRegularFile)) {
            assertThat( stream.count() ).isEqualTo(3);
        }

        GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache",metalPathProperty,"clean")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        final var recompile = GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache",metalPathProperty,"compileCxx")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertThat( recompile.task(":compileCxx").getOutcome() ).isEqualTo(FROM_CACHE);

        final var archive = GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache",metalPathProperty,"archive")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertThat( archive.task(":archive").getOutcome() ).isEqualTo(SUCCESS);

        try (var stream = Files.walk(projectDir.resolve("build/lib/main")).filter(Files::isRegularFile)) {
            assertThat( stream.count() ).isEqualTo(1);
        }

        GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache",metalPathProperty,"clean")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        final var rearchive = GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache",metalPathProperty,"archive")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertThat( rearchive.task(":archive").getOutcome() ).isEqualTo(FROM_CACHE);

        try (var stream = Files.walk(projectDir.resolve("build/lib/main")).filter(Files::isRegularFile)) {
            assertThat( stream.count() ).isEqualTo(1);
        }

        final var check = GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache","--info",metalPathProperty,"check")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertThat( check.task(":check").getOutcome() ).isEqualTo(SUCCESS);
    }

    @Test
    public void empty () throws IOException
    {
        Files.writeString(projectDir.resolve("build.gradle.kts"),
            """         
            plugins {
                id("br.dev.pedrolamarao.metal.library")
                id("br.dev.pedrolamarao.metal.asm")
                id("br.dev.pedrolamarao.metal.c")
                id("br.dev.pedrolamarao.metal.cxx")
            }
            """
        );

        final var assemble = GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache","--quiet",metalPathProperty,"assemble")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertThat( assemble.task(":compileAsm").getOutcome() ).isEqualTo( NO_SOURCE );
        assertThat( assemble.task(":compileC").getOutcome()   ).isEqualTo( NO_SOURCE );
        assertThat( assemble.task(":compileCxx").getOutcome() ).isEqualTo( NO_SOURCE );
        assertThat( assemble.task(":archive").getOutcome()       ).isEqualTo( NO_SOURCE );
        assertThat( assemble.task(":assemble").getOutcome()   ).isEqualTo( UP_TO_DATE );

        final var check = GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache","--quiet",metalPathProperty,"check")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertThat( check.task(":runTest").getOutcome() ).isEqualTo( SKIPPED );
        assertThat( check.task(":check").getOutcome() ).isEqualTo( UP_TO_DATE );
    }
}
