// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.ixx;

import br.dev.pedrolamarao.gradle.metal.cxx.MetalCxxCompileBaseTask;
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
 * Aggregate C++ module interface compile commands database task.
 */
public abstract class MetalIxxCommandsTask extends MetalIxxCompileBaseTask
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
    public void generate () throws Exception
    {
        final var baseDirectory = getProject().getProjectDir().toPath();
        final var objectDirectory = getObjectDirectory().get().toPath();

        final var modules = scan();

        // prepare compile arguments list
        final var baseArgs = new ArrayList<String>();
        baseArgs.add(getCompiler().get().toString().replace("\\","\\\\"));
        baseArgs.add("--target=%s".formatted(getTarget().get()));
        baseArgs.addAll(getCompileOptions().get());
        getInclude().forEach(file -> baseArgs.add("--include-directory=%s".formatted(file).replace("\\","\\\\")));
        getImport().forEach(file -> baseArgs.add("-fprebuilt-module-path=%s".formatted(file).replace("\\","\\\\")));
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
        final var output = getTargetDirectory().map(it -> it.file("compile_commands.json")).get();
        try (var writer = Files.newBufferedWriter(output.getAsFile().toPath(),UTF_8)) {
            writer.write("[\n");
            writer.write( String.join(",\n", commandList) );
            writer.write("]");
        }
    }
}
