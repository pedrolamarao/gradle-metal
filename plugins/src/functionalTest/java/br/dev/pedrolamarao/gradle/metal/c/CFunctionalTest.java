package br.dev.pedrolamarao.gradle.metal.c;

import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class CFunctionalTest
{
    @TempDir Path projectDir;

    @Test
    public void compile () throws IOException
    {
        Files.createDirectories(projectDir.resolve("src/main/c"));

        Files.writeString(projectDir.resolve("src/main/c/main.c"),
        """
        int main (int argc, char * argv [])
        {
            return 0;
        }
        """
        );

        Files.writeString(projectDir.resolve("build.gradle.kts"),
        """
        plugins {
            id("br.dev.pedrolamarao.metal.c")
        }
        
        metal {
            c {
                create("main")
            }
        }
        """
        );

        GradleRunner.create()
            .withArguments("--configuration-cache","compile-main-c")
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
            id("br.dev.pedrolamarao.metal.c")
        }
        
        metal {
            compileOptions = listOf("--foo")
            
            c { create("main") }
        }
        
        tasks.register("compileOptions") {
            val compileOptions = metal.c.named("main").flatMap{it.compileOptions}
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
        Files.createDirectories(projectDir.resolve("src/main/c"));

        Files.writeString(projectDir.resolve("src/main/c/main.c"),
        """
        int main (int argc, char * argv [])
        {
            return 0;
        }
        """
        );

        Files.writeString(projectDir.resolve("build.gradle.kts"),
        """
        plugins {
            id("br.dev.pedrolamarao.metal.c")
        }
        
        metal {
            c {
                create("main") {
                    targets = setOf("i686-elf")
                }
            }
        }
        """
        );

        GradleRunner.create()
            .withArguments("--configuration-cache","compile-main-c","-Pmetal.target=x86_64-elf")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertFalse( Files.exists(projectDir.resolve("build/obj")) );
    }

    @Test
    public void targetEnabled () throws IOException
    {
        Files.createDirectories(projectDir.resolve("src/main/c"));

        Files.writeString(projectDir.resolve("src/main/c/main.c"),
        """
        int main (int argc, char * argv [])
        {
            return 0;
        }
        """
        );

        Files.writeString(projectDir.resolve("build.gradle.kts"),
        """
        plugins {
            id("br.dev.pedrolamarao.metal.c")
        }
        
        metal {
            c {
                create("main") {
                    targets = setOf("i686-elf")
                }
            }
        }
        """
        );

        GradleRunner.create()
            .withArguments("--configuration-cache","compile-main-c","-Pmetal.target=i686-elf")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertTrue( Files.exists(projectDir.resolve("build/obj")) );

        try (var stream = Files.walk(projectDir.resolve("build/obj")).filter(Files::isRegularFile)) {
            assertEquals( 1, stream.count() );
        }
    }
}
