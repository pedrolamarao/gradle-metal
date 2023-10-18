package br.dev.pedrolamarao.gradle.metal.cxx;

import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class CxxFunctionalTest
{
    @TempDir Path projectDir;

    @Test
    public void compile () throws IOException
    {
        Files.createDirectories(projectDir.resolve("src/main/cxx"));

        Files.writeString(projectDir.resolve("src/main/cxx/main.cxx"),
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
            id("br.dev.pedrolamarao.metal.cxx")
        }
        
        metal {
            cxx {
                create("main")
            }
        }
        """
        );

        GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .withArguments("compile-main-cxx")
            .withDebug(true)
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
            id("br.dev.pedrolamarao.metal.cxx")
        }
        
        metal {
            compileOptions = listOf("--foo")
            
            cxx { create("main") }
        }
        
        tasks.register("compileOptions") {
            doLast {
                System.out.printf("%s",metal.cxx.named("main").flatMap{it.compileOptions}.get())
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

    @Test
    public void includes () throws IOException
    {
        Files.createDirectories(projectDir.resolve("src/main/cpp"));
        Files.createDirectories(projectDir.resolve("src/main/cxx"));

        Files.writeString(projectDir.resolve("src/main/cpp/greet.h"),
        """
        int greet (int argc, char * argv []);
        """
        );

        Files.writeString(projectDir.resolve("src/main/cxx/main.cxx"),
        """
        #include <greet.h>
        
        int main (int argc, char * argv [])
        {
            return greet(argc,argv);
        }
        """
        );

        Files.writeString(projectDir.resolve("build.gradle.kts"),
        """
        plugins {
            id("br.dev.pedrolamarao.metal.cpp")
            id("br.dev.pedrolamarao.metal.cxx")
        }
        
        metal {
            cpp {
                create("main")
            }
            cxx {
                create("main") {
                    includes.from( cpp.named("main").map { it.sources } )
                }
            }
        }
        """
        );

        GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .withArguments("compile-main-cxx")
            .withDebug(true)
            .build();

        assertTrue( Files.exists(projectDir.resolve("build/obj")) );

        try (var stream = Files.walk(projectDir.resolve("build/obj")).filter(Files::isRegularFile)) {
            assertEquals( 1, stream.count() );
        }
    }

    @Test
    public void targetDisabled () throws IOException
    {
        Files.createDirectories(projectDir.resolve("src/main/cxx"));

        Files.writeString(projectDir.resolve("src/main/cxx/main.cxx"),
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
            id("br.dev.pedrolamarao.metal.cxx")
        }
        
        metal {
            cxx {
                create("main") {
                    targets = setOf("i686-elf")
                }
            }
        }
        """
        );

        GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .withArguments("compile-main-cxx","-Pmetal.target=x86_64-elf")
            .withDebug(true)
            .build();

        assertFalse( Files.exists(projectDir.resolve("build/obj")) );
    }

    @Test
    public void targetEnabled () throws IOException
    {
        Files.createDirectories(projectDir.resolve("src/main/cxx"));

        Files.writeString(projectDir.resolve("src/main/cxx/main.cxx"),
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
            id("br.dev.pedrolamarao.metal.cxx")
        }
        
        metal {
            cxx {
                create("main") {
                    targets = setOf("i686-elf")
                }
            }
        }
        """
        );

        GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .withArguments("compile-main-cxx","-Pmetal.target=i686-elf")
            .withDebug(true)
            .build();

        assertTrue( Files.exists(projectDir.resolve("build/obj")) );

        try (var stream = Files.walk(projectDir.resolve("build/obj")).filter(Files::isRegularFile)) {
            assertEquals( 1, stream.count() );
        }
    }
}
