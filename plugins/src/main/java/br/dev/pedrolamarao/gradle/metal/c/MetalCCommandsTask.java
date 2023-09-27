// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.c;

import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.stream.Collectors;

public abstract class MetalCCommandsTask extends MetalCCompileBaseTask
{
    @Input
    public abstract Property<File> getObjectDirectory ();

    @OutputFile
    public abstract RegularFileProperty getOutputFile ();

    static final String template =
    """
      {
        "directory": "%s",
        "arguments": [ %s ],
        "file": "%s",
        "output": "%s"
      }
    """;

    @TaskAction
    public void generate () throws IOException
    {
        final var baseDirectory = getProject().getProjectDir().toPath();
        final var objectDirectory = getObjectDirectory().get().toPath();

        // prepare arguments
        final var baseArgs = new ArrayList<String>();
        baseArgs.add("clang");
        baseArgs.addAll(getCompileOptions().get());
        getIncludables().forEach(file -> baseArgs.add("--include-directory=%s".formatted(file).replace("\\","\\\\")));
        baseArgs.add("--language=c");
        baseArgs.add("--compile");

        final var directory = getProject().getProjectDir().toString().replace("\\","\\\\");

        final var list = new ArrayList<String>();
        getSource().forEach(source ->
        {
            final var file = source.toString().replace("\\","\\\\");
            final var output = toOutputPath(baseDirectory,source.toPath(),objectDirectory).toString().replace("\\","\\\\");

            final var compileArgs = new ArrayList<>(baseArgs);
            compileArgs.add("--output=%s".formatted(output));
            compileArgs.add(file);
            final var arguments = compileArgs.stream().collect(Collectors.joining("\", \"","\"","\""));

            list.add( template.formatted(directory, arguments, file, output) );
        });

        try (var writer = Files.newBufferedWriter(getOutputFile().get().getAsFile().toPath(),StandardCharsets.UTF_8)) {
            writer.write("[\n");
            writer.write( String.join(",\n",list) );
            writer.write("]");
        }
    }
}
