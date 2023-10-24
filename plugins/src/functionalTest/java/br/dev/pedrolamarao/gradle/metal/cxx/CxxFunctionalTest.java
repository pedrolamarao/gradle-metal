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
            .withArguments("--configuration-cache","compile-main-cxx")
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
            id("br.dev.pedrolamarao.metal.cxx")
        }
        
        metal {
            compileOptions = listOf("--foo")
            
            cxx { create("main") }
        }
        
        tasks.register("compileOptions") {
            val compileOptions = metal.cxx.named("main").flatMap{it.compileOptions}
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
                    include.from( cpp.named("main").map { it.includables } )
                }
            }
        }
        """
        );

        GradleRunner.create()
            .withArguments("--configuration-cache","compile-main-cxx")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
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
            .withArguments("--configuration-cache","compile-main-cxx","-Pmetal.target=x86_64-elf")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
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
            .withArguments("--configuration-cache","compile-main-cxx","-Pmetal.target=i686-elf")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        assertTrue( Files.exists(projectDir.resolve("build/obj")) );

        try (var stream = Files.walk(projectDir.resolve("build/obj")).filter(Files::isRegularFile)) {
            assertEquals( 1, stream.count() );
        }
    }
}
