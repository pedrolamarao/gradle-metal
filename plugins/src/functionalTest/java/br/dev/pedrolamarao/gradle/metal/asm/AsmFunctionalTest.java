package br.dev.pedrolamarao.gradle.metal.asm;

import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AsmFunctionalTest
{
    @TempDir Path projectDir;

    @Test
    public void compile () throws IOException
    {
        Files.createDirectories(projectDir.resolve("src/main/asm"));

        Files.writeString(projectDir.resolve("src/main/asm/bar.s"),
        """
        .global foo
        foo:
            ret
        """
        );

        Files.writeString(projectDir.resolve("build.gradle.kts"),
        """
        plugins {
            id("br.dev.pedrolamarao.metal.asm")
        }
        
        metal {
            asm {
                create("main")
            }
        }
        """
        );

        GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .withArguments("compile-main-asm")
            .build();

        try (var stream = Files.walk(projectDir.resolve("build/obj")).filter(Files::isRegularFile)) {
            assertEquals( 1, stream.count() );
        }
    }

    @Test
    public void compileOptions () throws IOException
    {
        Files.writeString(projectDir.resolve("build.gradle.kts"),
        """
        plugins {
            id("br.dev.pedrolamarao.metal.asm")
        }
        
        metal {
            compileOptions = listOf("--foo")
            
            asm { create("main") }
        }
        
        tasks.register("compileOptions") {
            doLast {
                System.out.printf("%s",metal.asm.named("main").flatMap{it.compileOptions}.get())
            }
        }
        """
        );

        final var compileOptions = GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .withArguments("--quiet","compileOptions")
            .build();
        assertEquals("[--foo]",compileOptions.getOutput());
    }
}
