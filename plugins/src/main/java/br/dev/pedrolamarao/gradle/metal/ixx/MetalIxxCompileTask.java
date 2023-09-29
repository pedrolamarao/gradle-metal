// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.ixx;

import org.gradle.api.tasks.TaskAction;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public abstract class MetalIxxCompileTask extends MetalIxxCompileBaseTask
{
    @TaskAction
    public void compile () throws ClassNotFoundException, IOException
    {
        // discover dependencies from sources
        final var modules = scan();

        // prepare compile arguments
        final var baseArgs = new ArrayList<String>();
        baseArgs.add("clang++");
        if (getTarget().isPresent()) baseArgs.add("--target=%s".formatted(getTarget().get()));
        baseArgs.addAll(getCompileOptions().get());
        getIncludables().forEach(file -> baseArgs.add("--include-directory=%s".formatted(file)));
        getImportables().forEach(file -> baseArgs.add("-fprebuilt-module-path=%s".formatted(file)));
        baseArgs.add("-fprebuilt-module-path=%s".formatted(getOutputDirectory().get()));
        baseArgs.add("--precompile");
        baseArgs.add("--language=c++-module");

        // remove old objects
        final var outputDirectory = getTargetDirectory().get().getAsFile().toPath();
        getProject().delete(outputDirectory);

        // compile objects from sources
        Files.createDirectories(outputDirectory);
        for (var module : modules)
        {
            final var moduleName = module.provides().get(0);
            final var outputPath = outputDirectory.resolve( moduleName.replace(":","-") + ".pcm" );

            // finish compile arguments
            final var compileArgs = new ArrayList<>(baseArgs);
            compileArgs.add("--output=%s".formatted(outputPath));
            compileArgs.add(module.source().toString());

            getProject().exec(it -> {
                it.commandLine(compileArgs);
            });
        }
    }
}
