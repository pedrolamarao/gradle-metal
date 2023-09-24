// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.cxx;

import org.gradle.api.file.RegularFile;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.stream.Collectors;

public abstract class IxxCommandsTask extends IxxCompileBaseTask
{
    @Input
    public abstract Property<File> getObjectDirectory ();

    @Internal
    public Provider<RegularFile> getOutputFile ()
    {
        return getOutputDirectory().file("compile_commands.json");
    }

    static final String template = """
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

        final var dependencies = scan();

        // prepare arguments
        final var baseArgs = new ArrayList<String>();
        baseArgs.add("clang++");
        baseArgs.addAll(getCompileOptions().get());
        getHeaderDependencies().forEach(file -> baseArgs.add("--include-directory=%s".formatted(file).replace("\\","\\\\")));
        getModuleDependencies().getAsFileTree().forEach(file -> baseArgs.add("-fmodule-file=%s".formatted(file).replace("\\","\\\\")));

        final var directory = getProject().getProjectDir().toString().replace("\\","\\\\");

        final var commandList = new ArrayList<String>();
        final var moduleList = new ArrayList<String>();
        dependencies.stream().map(IxxDependency::file).forEach(source ->
        {
            final var file = source.toString().replace("\\","\\\\");
            final var output = toOutputPath(baseDirectory,source.toPath(),objectDirectory,".bmi").toString().replace("\\","\\\\");

            final var compileArgs = new ArrayList<>(baseArgs);
            moduleList.forEach(it -> compileArgs.add("-fmodule-file=%s".formatted(it).replace("\\","\\\\")));
            compileArgs.add("--language=c++-module");
            compileArgs.add("--precompile");
            compileArgs.add("--output=%s".formatted(output));
            compileArgs.add(file);
            final var arguments = compileArgs.stream().collect(Collectors.joining("\", \"","\"","\""));

            commandList.add( template.formatted(directory, arguments, file, output) );

            moduleList.add(output);
        });

        try (var writer = Files.newBufferedWriter(getOutputFile().get().getAsFile().toPath(),StandardCharsets.UTF_8)) {
            writer.write("[\n");
            writer.write( String.join(",\n", commandList) );
            writer.write("]");
        }
    }
}
