package br.dev.pedrolamarao.gradle.metal;

import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.gradle.testkit.runner.TaskOutcome.FROM_CACHE;
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS;

public class MetalLibraryTest extends MetalTestBase
{
    @Test
    public void compileAsm () throws IOException
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
    }

    @Test
    public void compileCxx () throws IOException
    {
        Files.createDirectories(projectDir.resolve("src/main/cxx"));

        Files.writeString(projectDir.resolve("src/main/cxx/main.cxx"),
            """
            int main (int argc, char * argv[])
            {
                return 0;
            }
            """
        );

        Files.writeString(projectDir.resolve("build.gradle.kts"),
            """
            plugins {
                id("br.dev.pedrolamarao.metal.library")
                id("br.dev.pedrolamarao.metal.cxx")
            }
            """
        );

        final var compile = GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache",metalPathProperty,"compileCxx")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertThat( compile.task(":compileCxx").getOutcome() ).isEqualTo(SUCCESS);

        try (var stream = Files.walk(projectDir.resolve("build/obj/main/cxx")).filter(Files::isRegularFile)) {
            assertThat( stream.count() ).isEqualTo(1);
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
    }
}
