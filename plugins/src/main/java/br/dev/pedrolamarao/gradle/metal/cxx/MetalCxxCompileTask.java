// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.cxx;

import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.TaskAction;
import org.gradle.process.ExecOperations;
import org.gradle.workers.WorkAction;
import org.gradle.workers.WorkParameters;
import org.gradle.workers.WorkerExecutor;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public abstract class MetalCxxCompileTask extends MetalCxxCompileBaseTask
{
    final WorkerExecutor workerExecutor;

    @Inject
    public MetalCxxCompileTask (WorkerExecutor workerExecutor)
    {
        this.workerExecutor = workerExecutor;
    }

    public interface CompileParameters extends WorkParameters
    {
        DirectoryProperty getBaseDirectory ();

        ListProperty<String> getCompileArgs ();

        DirectoryProperty getOutputDirectory ();

        RegularFileProperty getSourceFile ();
    }

    public static abstract class CompileAction implements WorkAction<CompileParameters>
    {
        final ExecOperations execOperations;

        @Inject
        public CompileAction (ExecOperations execOperations)
        {
            this.execOperations = execOperations;
        }

        @Override
        public void execute ()
        {
            final var parameters = getParameters();

            final var basePath = parameters.getBaseDirectory().get().getAsFile().toPath();
            final var objectPath = parameters.getOutputDirectory().get().getAsFile().toPath();

            final var sourcePath = parameters.getSourceFile().get().getAsFile().toPath();
            final var outputPath = toOutputPath(basePath, sourcePath, objectPath, ".o");

            // prepare compile arguments
            final var compileArgs = new ArrayList<>(parameters.getCompileArgs().get());
            compileArgs.add("--output=%s".formatted(outputPath));
            compileArgs.add(sourcePath.toString());

            try
            {
                Files.createDirectories(outputPath.getParent());
                execOperations.exec(it -> it.commandLine(compileArgs));
            }
            catch (IOException e) { throw new RuntimeException(e); }
        }
    }

    @TaskAction
    public void compile ()
    {
        final var baseDirectory = getProject().getProjectDir();
        final var outputDirectory = getTargetDirectory();
        final var queue = workerExecutor.noIsolation();

        // prepare arguments
        final var compileArgs = toCompileArguments(File::toString);

        // delete old objects
        getProject().delete(outputDirectory);

        // compile objects from sources
        getSource().forEach(source ->
        {
            queue.submit(CompileAction.class, parameters ->
            {
                parameters.getBaseDirectory().set(baseDirectory);
                parameters.getCompileArgs().set(compileArgs);
                parameters.getOutputDirectory().set(outputDirectory);
                parameters.getSourceFile().set(source);
            });
        });
    }
}
