// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.c;

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

public abstract class CCompileTask extends SourceTask
{
    final WorkerExecutor workerExecutor;

    @InputFiles
    public abstract ConfigurableFileCollection getHeaderDependencies ();

    @Input
    public abstract ListProperty<String> getCompileOptions ();

    @OutputDirectory
    public abstract DirectoryProperty getOutputDirectory ();

    @Inject
    public CCompileTask (WorkerExecutor workerExecutor)
    {
        this.workerExecutor = workerExecutor;
    }

    public interface CompileParameters extends WorkParameters
    {
        ConfigurableFileCollection getHeaderDependencies ();

        ListProperty<String> getOptions ();

        RegularFileProperty getOutput ();

        RegularFileProperty getSource ();
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

            final var output = parameters.getOutput().getAsFile().get();

            try
            {
                Files.createDirectories(output.toPath().getParent());

                final var command = new ArrayList<String>();
                command.add("clang");
                parameters.getHeaderDependencies().forEach(it -> command.add("--include-directory=%s".formatted(it)));
                command.addAll(parameters.getOptions().get());
                command.add("--compile");
                command.add("--output=%s".formatted(output));
                command.add("--language=c");
                command.add(parameters.getSource().get().toString());

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
        final var baseDirectory = getProject().getProjectDir().toPath();
        final var outputDirectory = getOutputDirectory().get().getAsFile().toPath();
        final var queue = workerExecutor.noIsolation();

        // remove old objects
        getProject().delete(getOutputDirectory());

        // compile objects from sources
        getSource().forEach(source ->
        {
            queue.submit(CompileAction.class, parameters ->
            {
                final var output = toOutputPath(baseDirectory,source.toPath(),outputDirectory);
                parameters.getHeaderDependencies().from(getHeaderDependencies());
                parameters.getOutput().set(output.toFile());
                parameters.getOptions().set(getCompileOptions());
                parameters.getSource().set(source);
            });
        });
    }

    static Path toOutputPath (Path base, Path source, Path outputDirectory)
    {
        final var relative = base.relativize(source);
        final var target = outputDirectory.resolve("%X".formatted(relative.hashCode()));
        return target.resolve(source.getFileName() + ".o");
    }
}
