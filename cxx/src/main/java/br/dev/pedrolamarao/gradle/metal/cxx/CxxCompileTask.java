// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.cxx;

import org.gradle.api.file.ConfigurableFileCollection;
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

public abstract class CxxCompileTask extends CxxCompileBaseTask
{
    final WorkerExecutor workerExecutor;

    @Inject
    public CxxCompileTask (WorkerExecutor workerExecutor)
    {
        this.workerExecutor = workerExecutor;
    }

    public interface CompileParameters extends WorkParameters
    {
        ConfigurableFileCollection getHeaderDependencies ();

        ConfigurableFileCollection getModuleDependencies ();

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
                command.add("clang++");
                parameters.getHeaderDependencies().forEach(file -> command.add("--include-directory=%s".formatted(file)));
                parameters.getModuleDependencies().getAsFileTree().forEach(file -> command.add("-fmodule-file=%s".formatted(file)));
                command.addAll(parameters.getOptions().get());
                command.add("--compile");
                command.add("--output=%s".formatted(output));
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

        // delete old objects
        getProject().delete(outputDirectory);

        // compile objects from sources
        getSource().forEach(source ->
        {
            queue.submit(CompileAction.class, parameters ->
            {
                final var output = toOutputPath(baseDirectory,source.toPath(),outputDirectory);
                parameters.getHeaderDependencies().from(getHeaderDependencies());
                parameters.getModuleDependencies().from(getModuleDependencies());
                parameters.getOptions().set(getCompileOptions());
                parameters.getOutput().set(output.toFile());
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
