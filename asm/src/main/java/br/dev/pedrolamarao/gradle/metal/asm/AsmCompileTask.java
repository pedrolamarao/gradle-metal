// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.asm;

import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.*;
import org.gradle.process.ExecOperations;
import org.gradle.workers.WorkAction;
import org.gradle.workers.WorkParameters;
import org.gradle.workers.WorkerExecutor;

import javax.inject.Inject;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public abstract class AsmCompileTask extends SourceTask
{
    final WorkerExecutor workerExecutor;

    @InputFiles
    public abstract ConfigurableFileCollection getModules ();

    @Input
    public abstract ListProperty<String> getCompileOptions ();

    @OutputDirectory
    public abstract DirectoryProperty getOutputDirectory ();

    @Inject
    public AsmCompileTask (WorkerExecutor workerExecutor)
    {
        this.workerExecutor = workerExecutor;
    }

    public interface CompileParameters extends WorkParameters
    {
        DirectoryProperty getBaseDirectory ();

        ListProperty<String> getOptions ();

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

            try
            {
                final var source = parameters.getSourceFile().get().getAsFile().toPath();

                final var output = toOutputPath(
                    parameters.getBaseDirectory().get().getAsFile().toPath(),
                    source,
                    parameters.getOutputDirectory().get().getAsFile().toPath()
                );

                Files.createDirectories(output.getParent());

                final var command = new ArrayList<String>();
                command.add("clang");
                command.addAll(parameters.getOptions().get());
                command.add("--compile");
                command.add("--output=%s".formatted(output));
                command.add("--language=assembler");
                command.add(source.toString());

                execOperations.exec(it ->
                {
                    it.commandLine(command);
                });
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
        final var queue = workerExecutor.noIsolation();

        // remove old objects
        getProject().delete(outputDirectory);

        // assemble objects from sources
        getSource().forEach(source ->
        {
            queue.submit(CompileAction.class, parameters ->
            {
                parameters.getBaseDirectory().set(baseDirectory);
                parameters.getOutputDirectory().set(outputDirectory);
                parameters.getOptions().set(getCompileOptions());
                parameters.getSourceFile().set(source);
            });
        });
    }

    static Path toOutputPath (Path base, Path source, Path output)
    {
        final var relative = base.relativize(source);
        final var target = output.resolve("%X".formatted(relative.hashCode()));
        return target.resolve(source.getFileName() + ".o");
    }
}
