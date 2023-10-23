// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.ixx;

import org.gradle.api.tasks.TaskAction;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

/**
 * Compile C++ module interface sources task.
 */
public abstract class MetalIxxCompileTask extends MetalIxxCompileBaseTask
{
    /**
     * Compile sources.
     *
     * @throws ClassNotFoundException if reflection failure
     * @throws IOException if I/O failure
     */
    @TaskAction
    public void compile () throws ClassNotFoundException, IOException
    {
        // discover dependencies from sources
        final var modules = scan();

        // prepare compile arguments
        final var baseArgs = new ArrayList<String>();
        baseArgs.add("--target=%s".formatted(getTarget().get()));
        baseArgs.addAll(getCompileOptions().get());
        getInclude().forEach(file -> baseArgs.add("--include-directory=%s".formatted(file)));
        getImport().forEach(file -> baseArgs.add("-fprebuilt-module-path=%s".formatted(file)));
        baseArgs.add("-fprebuilt-module-path=%s".formatted(getTargetDirectory().get()));
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
                it.executable(getCompiler().get());
                it.args(compileArgs);
            });
        }
    }
}
