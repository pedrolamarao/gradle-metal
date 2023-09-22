// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.cxx;

import org.gradle.api.tasks.*;
import org.gradle.workers.WorkerExecutor;

import javax.inject.Inject;
import java.nio.file.Path;

public abstract class CxxCompileTask extends CxxCompileBaseTask
{
    final WorkerExecutor workerExecutor;

    @Inject
    public CxxCompileTask (WorkerExecutor workerExecutor)
    {
        this.workerExecutor = workerExecutor;
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
            queue.submit(CxxCompileWorkAction.class, parameters ->
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
