// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.c;

import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.*;
import org.gradle.workers.WorkerExecutor;

import javax.inject.Inject;
import java.nio.file.Path;

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
            queue.submit(CCompileWorkAction.class, parameters ->
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
