package br.dev.pedrolamarao.gradle.metal.asm;

import br.dev.pedrolamarao.gradle.metal.MetalTestBase;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

public class AsmFunctionalMetalTest extends MetalTestBase
{
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
            .withArguments("--configuration-cache","compile-main-asm")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertTrue( Files.exists(projectDir.resolve("build/obj")) );

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
            val compileOptions = metal.asm.named("main").flatMap{it.compileOptions}
            doLast {
                print("${compileOptions.get()}")
            }
        }
        """
        );

        final var compileOptions = GradleRunner.create()
            .withArguments("--configuration-cache","--quiet","compileOptions")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertEquals("[--foo]",compileOptions.getOutput());
    }

    @Test
    public void targetDisabled () throws IOException
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
                create("main") {
                    targets = setOf("i686-elf")
                }
            }
        }
        """
        );

        GradleRunner.create()
            .withArguments("--configuration-cache","compile-main-asm","-Pmetal.target=x86_64-elf")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertFalse( Files.exists(projectDir.resolve("build/obj")) );
    }

    @Test
    public void targetEnabled () throws IOException
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
                create("main") {
                    targets = setOf("i686-elf")
                }
            }
        }
        """
        );

        GradleRunner.create()
            .withArguments("--configuration-cache","compile-main-asm","-Pmetal.target=i686-elf")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertTrue( Files.exists(projectDir.resolve("build/obj")) );

        try (var stream = Files.walk(projectDir.resolve("build/obj")).filter(Files::isRegularFile)) {
            assertEquals( 1, stream.count() );
        }
    }
}
