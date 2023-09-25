// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.cxx;

import org.gradle.api.model.ObjectFactory;
import org.gradle.api.tasks.TaskAction;
import org.gradle.process.ExecOperations;
import org.gradle.workers.WorkerExecutor;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public abstract class IxxCompileTask extends IxxCompileBaseTask
{
    final ExecOperations exec;

    final ObjectFactory objects;

    final WorkerExecutor workers;

    @Inject
    public IxxCompileTask (ExecOperations exec, ObjectFactory objects, WorkerExecutor workers)
    {
        this.exec = exec;
        this.objects = objects;
        this.workers = workers;
    }

    @TaskAction
    public void compile () throws ClassNotFoundException, IOException
    {
        final var baseDirectory = getProject().getProjectDir().toPath();

        // discover dependencies from sources
        final var modules = scan();

        // prepare base arguments
        final var baseArgs = new ArrayList<String>();
        baseArgs.add("clang++");
        baseArgs.addAll(getCompileOptions().get());
        getHeaderDependencies().forEach(file -> baseArgs.add("--include-directory=%s".formatted(file)));
        getModuleDependencies().forEach(file -> baseArgs.add("-fprebuilt-module-path=%s".formatted(file)));
        baseArgs.add("-fprebuilt-module-path=%s".formatted(getOutputDirectory().get()));
        baseArgs.add("--language=c++-module");
        baseArgs.add("--precompile");

        // remove old objects
        final var outputDirectory = getOutputDirectory().get().getAsFile().toPath();
        getProject().delete(outputDirectory);

        // compile objects from sources
        Files.createDirectories(outputDirectory);
        for (var module : modules)
        {
            final var moduleName = module.provides().get(0);
            final var outputPath = outputDirectory.resolve( moduleName.replace(":","-") + ".pcm" );

            // prepare compiler arguments
            final var compileArgs = new ArrayList<>(baseArgs);
            compileArgs.add("--output=%s".formatted(outputPath));
            compileArgs.add(module.source().toString());

            exec.exec(it -> {
                it.commandLine(compileArgs);
            });
        }
    }
}
