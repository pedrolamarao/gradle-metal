// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.cxx;

import org.gradle.api.file.FileCollection;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.TaskAction;
import org.gradle.process.ExecOperations;
import org.gradle.workers.WorkerExecutor;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public abstract class IxxCompileTask extends IxxCompileBaseTask
{
    final ExecOperations exec;

    final ObjectFactory objects;

    final WorkerExecutor workers;

    @Internal @Nonnull
    public FileCollection getInterfaceFiles ()
    {
        final var collection = objects.fileCollection();
        final var baseDirectory = getProject().getProjectDir().toPath();
        final var outputDirectory = getOutputDirectory().get().getAsFile().toPath();
        getSource().forEach(source -> collection.from( toOutputPath(baseDirectory,source.toPath(),outputDirectory,".bmi") ));
        return collection;
    }

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
        final var dependencyGraph = scan();

        // prepare base arguments
        final var baseArgs = new ArrayList<String>();
        baseArgs.add("clang++");
        baseArgs.addAll(getCompileOptions().get());
        getHeaderDependencies().forEach(file -> baseArgs.add("--include-directory=%s".formatted(file)));
        getModuleDependencies().forEach(file -> baseArgs.add("-fmodule-file=%s".formatted(file)));

        // remove old objects
        final var outputDirectory = getOutputDirectory().get().getAsFile().toPath();
        getProject().delete(outputDirectory);

        // compile objects from sources
        final var outputList = new ArrayList<Path>();
        for (var sourceFile : dependencyGraph.stream().map(IxxDependency::file).toList())
        {
            final var outputPath = toOutputPath(baseDirectory, sourceFile.toPath(), outputDirectory, ".bmi");
            Files.createDirectories(outputPath.getParent());

            // prepare compiler arguments
            final var compileArgs = new ArrayList<>(baseArgs);
            outputList.forEach(file -> compileArgs.add("-fmodule-file=%s".formatted(file)));
            compileArgs.add("--language=c++-module");
            compileArgs.add("--precompile");
            compileArgs.add("--output=%s".formatted(outputPath));
            compileArgs.add(sourceFile.toString());

            exec.exec(it -> {
                it.commandLine(compileArgs);
            });

            outputList.add(outputPath);
        }
    }
}
