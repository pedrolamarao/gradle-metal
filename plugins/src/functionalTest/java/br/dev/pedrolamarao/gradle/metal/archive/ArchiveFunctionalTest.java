package br.dev.pedrolamarao.gradle.metal.archive;

import br.dev.pedrolamarao.gradle.metal.MetalTestBase;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Files;

public class ArchiveFunctionalTest extends MetalTestBase
{
    @Test
    public void apply () throws IOException
    {
        Files.writeString(projectDir.resolve("build.gradle.kts"),
        """
        plugins {
             id("br.dev.pedrolamarao.metal.archive")
        }
        
        metal {
            archives {
                main {
                    archiveOptions = listOf()
                }
            }
        }
        """
        );

        GradleRunner.create()
            .withArguments("--configuration-cache")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();
    }

    @ParameterizedTest
    @ValueSource(strings={"asm","c","cpp","cxx","ixx"})
    public void language (String language) throws IOException
    {
        Files.writeString(projectDir.resolve("build.gradle.kts"),
        """
        plugins {
             id("br.dev.pedrolamarao.metal.archive")
             id("br.dev.pedrolamarao.metal.%s")
        }
         
        metal {
            %s {
                main {
                    compileOptions = listOf()
                }
            }
            archives {
                main {
                    archiveOptions = listOf()
                }
            }
        }
        """.formatted(language,language)
        );

        GradleRunner.create()
            .withArguments("--configuration-cache")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();
    }

    @Test
    public void testCxx () throws IOException
    {
        final var testLanguageDir = projectDir.resolve("src/test/cxx");

        Files.createDirectories(testLanguageDir);

        Files.writeString(projectDir.resolve("build.gradle.kts"),
        """
        plugins {
            id("br.dev.pedrolamarao.metal.archive")
            id("br.dev.pedrolamarao.metal.cxx")
        }
        """
        );

        Files.writeString(testLanguageDir.resolve("test.cxx"),
        """
        int main (int argc, char * argv[])
        {
            return 0;
        }
        """
        );

        GradleRunner.create()
            .withArguments("--configuration-cache","run-test")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();
    }

    @Test
    public void testDependency () throws IOException
    {
        final var fooDir = projectDir.resolve("foo");
        Files.createDirectories(fooDir);
        Files.writeString(fooDir.resolve("build.gradle.kts"),
        """
        plugins {
            id("br.dev.pedrolamarao.metal.archive")
            id("br.dev.pedrolamarao.metal.cxx")
        }
        """);

        final var barDir = projectDir.resolve("bar");
        Files.createDirectories(barDir);
        Files.writeString(barDir.resolve("build.gradle.kts"),
        """
        plugins {
            id("br.dev.pedrolamarao.metal.archive")
            id("br.dev.pedrolamarao.metal.cxx")
        }
        
        dependencies {
            testImplementation(project(":foo"))
        }
        """);

        final var fooCxxDir = fooDir.resolve("src/main/cxx");
        Files.createDirectories(fooCxxDir);
        Files.writeString(fooCxxDir.resolve("foo.cxx"),
        """
        int foo (int argc, char * argv[])
        {
            return 0;
        }
        """);

        final var barCxxDir = projectDir.resolve("bar/test/cxx");
        Files.createDirectories(barCxxDir);
        Files.writeString(barCxxDir.resolve("bar.cxx"),
        """
        int foo (int argc, char * argv[]);
        
        int main (int argc, char * argv[])
        {
            return foo(argc,argv);
        }
        """);

        Files.writeString(projectDir.resolve("settings.gradle.kts"),
        """
        include("bar")
        include("foo")
        """);

        GradleRunner.create()
            .withArguments("--configuration-cache","check")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();
    }
}
