// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.ixx;

import br.dev.pedrolamarao.gradle.metal.cxx.MetalCxxCompileBaseTask;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.stream.Collectors;

public abstract class MetalIxxCommandsTask extends MetalIxxCompileBaseTask
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
    public void generate () throws Exception
    {
        final var baseDirectory = getProject().getProjectDir().toPath();
        final var objectDirectory = getObjectDirectory().get().toPath();

        final var modules = scan();

        // prepare compile arguments list
        final var baseArgs = new ArrayList<String>();
        baseArgs.add("clang++");
        if (getTarget().isPresent()) baseArgs.add("--target=%s".formatted(getTarget().get()));
        baseArgs.addAll(getCompileOptions().get());
        getIncludables().forEach(file -> baseArgs.add("--include-directory=%s".formatted(file).replace("\\","\\\\")));
        getImportables().forEach(file -> baseArgs.add("-fprebuilt-module-path=%s".formatted(file).replace("\\","\\\\")));
        baseArgs.add("-fprebuilt-module-path=%s".formatted(getObjectDirectory().get()).replace("\\","\\\\"));
        baseArgs.add("--precompile");
        baseArgs.add("--language=c++-module");

        // prepare directory field
        final var directory = getProject().getProjectDir().toString().replace("\\","\\\\");

        final var commandList = new ArrayList<String>();
        modules.forEach(module ->
        {
            // prepare file and output fields
            final var file = module.source().toString().replace("\\","\\\\");
            final var output = MetalCxxCompileBaseTask.toOutputPath(baseDirectory,module.source().toPath(),objectDirectory,".bmi").toString().replace("\\","\\\\");

            // prepare compile arguments field
            final var compileArgs = new ArrayList<>(baseArgs);
            compileArgs.add("--output=%s".formatted(output));
            compileArgs.add(file);
            final var arguments = compileArgs.stream().collect(Collectors.joining("\", \"","\"","\""));

            // format fields
            commandList.add( template.formatted(directory, arguments, file, output) );
        });

        // aggregate fields
        try (var writer = Files.newBufferedWriter(getOutputFile().get().getAsFile().toPath(),StandardCharsets.UTF_8)) {
            writer.write("[\n");
            writer.write( String.join(",\n", commandList) );
            writer.write("]");
        }
    }
}
