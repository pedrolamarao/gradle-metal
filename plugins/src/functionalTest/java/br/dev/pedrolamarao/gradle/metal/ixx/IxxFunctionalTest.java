package br.dev.pedrolamarao.gradle.metal.ixx;

import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IxxFunctionalTest
{
    @TempDir Path projectDir;

    @Test
    public void compile () throws IOException
    {
        Files.createDirectories(projectDir.resolve("src/main/ixx"));

        Files.writeString(projectDir.resolve("src/main/ixx/greet.ixx"),
        """
        export module br.dev.pedrolamarao;
        
        export namespace br::dev::pedrolamarao
        {
            int greet (int argc, char * argv[])
            {
                return 0;
            }
        }
        """
        );

        Files.writeString(projectDir.resolve("build.gradle.kts"),
        """
        plugins {
            id("br.dev.pedrolamarao.metal.ixx")
        }
        
        metal {
            ixx {
                create("main") {
                    compileOptions = listOf("-std=c++20")
                }
            }
        }
        """
        );

        GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .withArguments("compile-main-ixx")
            .withDebug(true)
            .build();

        try (var stream = Files.walk(projectDir.resolve("build/bmi")).filter(Files::isRegularFile)) {
            assertEquals( 1, stream.count() );
        }
    }

    @Test
    public void compileOptions () throws IOException
    {
        Files.writeString(projectDir.resolve("build.gradle.kts"),
        """
        plugins {
            id("br.dev.pedrolamarao.metal.ixx")
        }
        
        metal {
            compileOptions = listOf("--foo")
            
            ixx { create("main") }
        }
        
        tasks.register("compileOptions") {
            doLast {
                System.out.printf("%s",metal.ixx.named("main").flatMap{it.compileOptions}.get())
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
    public void imports () throws IOException
    {
        Files.createDirectories(projectDir.resolve("src/main/ixx"));

        Files.writeString(projectDir.resolve("src/main/ixx/aaa.ixx"),
        """
        export module br.dev.pedrolamarao:aaa;
        
        export int aaa (int argc, char * argv[]);
        """
        );

        Files.writeString(projectDir.resolve("src/main/ixx/module.ixx"),
        """
        export module br.dev.pedrolamarao;
        
        export import :aaa;
        export import :zzz;
        """
        );

        Files.writeString(projectDir.resolve("src/main/ixx/zzz.ixx"),
        """
        export module br.dev.pedrolamarao:zzz;
        
        export int zzz (int argc, char * argv[]);
        """
        );

        Files.writeString(projectDir.resolve("build.gradle.kts"),
        """
        plugins {
            id("br.dev.pedrolamarao.metal.ixx")
        }
        
        metal {
            ixx {
                create("main") {
                    compileOptions = listOf("-std=c++20")
                }
            }
        }
        """
        );

        GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .withArguments("compile-main-ixx")
            .withDebug(true)
            .build();

        try (var stream = Files.walk(projectDir.resolve("build/bmi")).filter(Files::isRegularFile)) {
            assertEquals( 3, stream.count() );
        }
    }

    @Test
    public void includes () throws IOException
    {
        Files.createDirectories(projectDir.resolve("src/main/cpp"));
        Files.createDirectories(projectDir.resolve("src/main/ixx"));

        Files.writeString(projectDir.resolve("src/main/cpp/greet.h"),
        """
        int greet (int argc, char * argv []);
        """
        );

        Files.writeString(projectDir.resolve("src/main/ixx/greet.ixx"),
        """
        module;
        
        #include <greet.h>
        
        export module br.dev.pedrolamarao;
        
        export namespace br::dev::pedrolamarao
        {
            int greet (int argc, char * argv[])
            {
                return greet(argc,argv);
            }
        }
        """
        );

        Files.writeString(projectDir.resolve("build.gradle.kts"),
        """
        plugins {
            id("br.dev.pedrolamarao.metal.cpp")
            id("br.dev.pedrolamarao.metal.ixx")
        }
        
        metal {
            cpp {
                create("main")
            }
            ixx {
                create("main") {
                    compileOptions = listOf("-std=c++20")
                    includable( cpp.named("main").map { it.sources } )
                }
            }
        }
        """
        );

        GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .withArguments("compile-main-ixx")
            .withDebug(true)
            .build();

        try (var stream = Files.walk(projectDir.resolve("build/bmi")).filter(Files::isRegularFile)) {
            assertEquals( 1, stream.count() );
        }
    }
}
