// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.asm;

import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;
import org.gradle.process.ExecOperations;
import org.gradle.workers.WorkAction;
import org.gradle.workers.WorkParameters;
import org.gradle.workers.WorkerExecutor;

import javax.inject.Inject;
import java.nio.file.Files;
import java.util.ArrayList;

public abstract class MetalAsmCompileTask extends MetalAsmCompileBaseTask
{
    final WorkerExecutor workerExecutor;

    @OutputDirectory
    public abstract DirectoryProperty getOutputDirectory ();

    @Inject
    public MetalAsmCompileTask (WorkerExecutor workerExecutor)
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

            final var sourcePath = parameters.getSourceFile().get().getAsFile().toPath();

            final var outputPath = toOutputPath(
                parameters.getBaseDirectory().get().getAsFile().toPath(),
                sourcePath,
                parameters.getOutputDirectory().get().getAsFile().toPath()
            );

            final var compileArgs = new ArrayList<>(parameters.getCompileArgs().get());
            compileArgs.add("--output=%s".formatted(outputPath));
            compileArgs.add(sourcePath.toString());

            try
            {
                Files.createDirectories(outputPath.getParent());
                execOperations.exec(it -> it.commandLine(compileArgs));
            }
            catch (RuntimeException e) { throw e; }
            catch (Exception e) { throw new RuntimeException(e); }
        }
    }

    @TaskAction
    public void compile ()
    {
        final var baseDirectory = getProject().getProjectDir();
        final var outputDirectory = getOutputDirectory();
        final var workers = workerExecutor.noIsolation();

        // prepare arguments
        final var compileArgs = new ArrayList<String>();
        compileArgs.add("clang");
        compileArgs.addAll(getCompileOptions().get());
        getHeaderDependencies().forEach(file -> compileArgs.add("--include-directory=%s".formatted(file)));
        compileArgs.add("--language=assembler");
        compileArgs.add("--compile");

        // remove old objects
        getProject().delete(outputDirectory);

        // assemble objects from sources
        getSource().forEach(source ->
        {
            workers.submit(CompileAction.class, parameters ->
            {
                parameters.getBaseDirectory().set(baseDirectory);
                parameters.getCompileArgs().set(compileArgs);
                parameters.getOutputDirectory().set(outputDirectory);
                parameters.getSourceFile().set(source);
            });
        });
    }
}
