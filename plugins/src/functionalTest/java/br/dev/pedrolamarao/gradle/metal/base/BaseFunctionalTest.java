package br.dev.pedrolamarao.gradle.metal.base;

import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class BaseFunctionalTest
{
    @TempDir Path projectDir;

    @Test
    public void apply () throws IOException
    {
        Files.writeString(projectDir.resolve("build.gradle.kts"),
        """
        plugins {
             id("br.dev.pedrolamarao.metal.base")
        }
         
        metal {
            archiveOptions = listOf()
            compileOptions = listOf()
            linkOptions = listOf()
        }
        """
        );

        GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .withDebug(true)
            .build();
    }

    @Test
    public void host () throws Exception
    {
        Files.writeString(projectDir.resolve("build.gradle.kts"),
            """
            plugins {
                 id("br.dev.pedrolamarao.metal.base")
            }
            
            tasks.register("host") {
                doLast {
                    System.out.printf("%s",metal.host.get())
                }
            }
            """
        );

        final var result = GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .withDebug(true)
            .withArguments("--quiet","host")
            .build();
        assertFalse( result.getOutput().isEmpty() );
    }

    @Test
    public void locateTool () throws Exception
    {
        Files.writeString(projectDir.resolve("build.gradle.kts"),
        """
        plugins {
             id("br.dev.pedrolamarao.metal.base")
        }
        
        tasks.register("locateTool") {
            doLast {
                val clang = metal.locateTool("clang")
                System.out.printf("%s",clang)
            }
        }
        """
        );

        final var result = GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .withDebug(true)
            .withArguments("--quiet","locateTool")
            .build();
        assertFalse( result.getOutput().isEmpty() );
    }

    @Test
    public void target () throws Exception
    {
        Files.writeString(projectDir.resolve("build.gradle.kts"),
        """
        plugins {
             id("br.dev.pedrolamarao.metal.base")
        }
        
        tasks.register("target") {
            doLast {
                System.out.printf("%s",metal.target.get())
            }
        }
        """
        );

        final var result = GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .withDebug(true)
            .withArguments("--quiet","target","-Pmetal.target=foo")
            .build();
        assertEquals( "foo", result.getOutput() );
    }
}