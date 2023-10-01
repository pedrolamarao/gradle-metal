// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.c;

import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Aggregate C compile commands database task.
 */
public abstract class MetalCCommandsTask extends MetalCCompileBaseTask
{
    /**
     * Objects base directory.
     *
     * @return provider
     */
    @Input
    public abstract Property<File> getObjectDirectory ();

    static final String template =
    """
      {
        "directory": "%s",
        "arguments": [ %s ],
        "file": "%s",
        "output": "%s"
      }
    """;

    /**
     * Aggregate compile commands.
     *
     * @throws IOException if IO failure
     */
    @TaskAction
    public void generate () throws IOException
    {
        final var baseDirectory = getProject().getProjectDir().toPath();
        final var objectDirectory = getObjectDirectory().get().toPath();

        // prepare compile arguments list
        final var baseArgs = toCompileArguments(file -> file.toString().replace("\\","\\\\"));

        // prepare directory field
        final var directory = getProject().getProjectDir().toString().replace("\\","\\\\");

        final var list = new ArrayList<String>();
        getSource().forEach(source ->
        {
            // prepare file and output fields
            final var file = source.toString().replace("\\","\\\\");
            final var output = toOutputPath(baseDirectory,source.toPath(),objectDirectory,".o")
                .toString().replace("\\","\\\\");

            // prepare compile arguments field
            final var compileArgs = new ArrayList<>(baseArgs);
            compileArgs.add("--output=%s".formatted(output));
            compileArgs.add(file);
            final var arguments = compileArgs.stream().collect(Collectors.joining("\", \"","\"","\""));

            // format fields
            list.add( template.formatted(directory, arguments, file, output) );
        });

        // aggregate fields
        final var output = getTargetDirectory().map(it -> it.file("compile_commands.json")).get();
        try (var writer = Files.newBufferedWriter(output.getAsFile().toPath(),UTF_8)) {
            writer.write("[\n");
            writer.write( String.join(",\n",list) );
            writer.write("]");
        }
    }
}
