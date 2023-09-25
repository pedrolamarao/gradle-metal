package br.dev.pedrolamarao.gradle.metal.cxx;

import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PluginFunctionalTest
{
    @TempDir Path projectDir;

    @Test
    public void compileCppCxx () throws IOException
    {
        Files.createDirectories(projectDir.resolve("src/main/cpp"));

        final var mainCpp =
            """
            inline
            int greet (int argc, char * argv [])
            {
                return 0;
            }
            """;

        Files.writeString(projectDir.resolve("src/main/cpp/greet.h"),mainCpp);

        Files.createDirectories(projectDir.resolve("src/main/cxx"));

        final var mainCxx =
            """
            #include <greet.h>
            
            int main (int argc, char * argv [])
            {
                return greet(argc,argv);
            }
            """;

        Files.writeString(projectDir.resolve("src/main/cxx/main.cxx"),mainCxx);

        final var buildGradleKts =
            """
            plugins {
                id("br.dev.pedrolamarao.metal.cxx")
            }
            
            metal {
                cpp {
                    sources {
                        create("main")
                    }
                }
                cxx {
                    sources {
                        create("main")
                    }
                }
            }
            """;

        Files.writeString(projectDir.resolve("build.gradle.kts"), buildGradleKts);

        final var result = GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .withArguments("compile-main-cxx")
            .withDebug(true)
            .build();

        try (var stream = Files.walk(projectDir.resolve("build/obj")).filter(Files::isRegularFile)) {
            assertEquals( 1, stream.count() );
        }
    }

    @Test
    public void compileCxxCpp () throws IOException
    {
        Files.createDirectories(projectDir.resolve("src/main/cpp"));

        final var mainCpp =
            """
            inline
            int greet (int argc, char * argv [])
            {
                return 0;
            }
            """;

        Files.writeString(projectDir.resolve("src/main/cpp/greet.h"),mainCpp);

        Files.createDirectories(projectDir.resolve("src/main/cxx"));

        final var mainCxx =
            """
            #include <greet.h>
            
            int main (int argc, char * argv [])
            {
                return greet(argc,argv);
            }
            """;

        Files.writeString(projectDir.resolve("src/main/cxx/main.cxx"),mainCxx);

        final var buildGradleKts =
            """
            plugins {
                id("br.dev.pedrolamarao.metal.cxx")
            }
            
            metal {
                cxx {
                    sources {
                        create("main")
                    }
                }
                cpp {
                    sources {
                        create("main")
                    }
                }
            }
            """;

        Files.writeString(projectDir.resolve("build.gradle.kts"), buildGradleKts);

        final var result = GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .withArguments("compile-main-cxx")
            .withDebug(true)
            .build();

        try (var stream = Files.walk(projectDir.resolve("build/obj")).filter(Files::isRegularFile)) {
            assertEquals( 1, stream.count() );
        }
    }

    @Test
    public void compileIxx () throws IOException
    {
        Files.createDirectories(projectDir.resolve("src/main/ixx"));

        final var mainCxx =
            """
            export module br.dev.pedrolamarao;
            
            export namespace br::dev::pedrolamarao
            {
                int greet (int argc, char * argv[])
                {
                    return 0;
                }
            }
            """;

        Files.writeString(projectDir.resolve("src/main/ixx/greet.ixx"),mainCxx);

        final var buildGradleKts =
            """
            plugins {
                id("br.dev.pedrolamarao.metal.cxx")
            }
            
            metal {
                ixx {
                    sources {
                        create("main") {
                            compileOptions = listOf("-std=c++20")
                        }
                    }
                }
            }
            """;

        Files.writeString(projectDir.resolve("build.gradle.kts"), buildGradleKts);

        final var result = GradleRunner.create()
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
    public void compileCxxIxx () throws IOException
    {
        Files.createDirectories(projectDir.resolve("src/main/cxx"));
        Files.createDirectories(projectDir.resolve("src/main/ixx"));

        final var greetCxx =
            """
            module br.dev.pedrolamarao;
            
            namespace br::dev::pedrolamarao
            {
                int greet (int argc, char * argv[])
                {
                    return 0;
                }
            }
            """;

        Files.writeString(projectDir.resolve("src/main/cxx/greet.cxx"),greetCxx);

        final var greetIxx =
            """
            export module br.dev.pedrolamarao;
            
            export namespace br::dev::pedrolamarao
            {
                int greet (int argc, char * argv[]);
            }
            """;

        Files.writeString(projectDir.resolve("src/main/ixx/greet.ixx"),greetIxx);

        final var buildGradleKts =
            """
            plugins {
                id("br.dev.pedrolamarao.metal.cxx")
            }
            
            metal {
                cxx {
                    sources {
                        create("main") {
                            compileOptions = listOf("-std=c++20")
                        }
                    }
                }
                ixx {
                    sources {
                        create("main") {
                            compileOptions = listOf("-std=c++20")
                        }
                    }
                }
            }
            """;

        Files.writeString(projectDir.resolve("build.gradle.kts"), buildGradleKts);

        final var result = GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .withArguments("--info","compile-main-cxx")
            .withDebug(true)
            .build();

        try (var stream = Files.walk(projectDir.resolve("build/bmi")).filter(Files::isRegularFile)) {
            assertEquals( 1, stream.count() );
        }

        try (var stream = Files.walk(projectDir.resolve("build/obj")).filter(Files::isRegularFile)) {
            assertEquals( 1, stream.count() );
        }
    }
}
