// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.cxx;

import org.gradle.api.provider.ListProperty;
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
        final var queue = workerExecutor.noIsolation();

        getSource().forEach(source ->
        {
            queue.submit(CxxCompileWorkAction.class, parameters ->
            {
                final var output = toOutputPath(baseDirectory,source.toPath());
                parameters.getHeaderDependencies().from(getHeaderDependencies());
                parameters.getModuleDependencies().from(getModuleDependencies());
                parameters.getOptions().set(getCompileOptions());
                parameters.getOutput().set(output.toFile());
                parameters.getSource().set(source);
            });
        });
    }

    Path toOutputPath (Path base, Path source)
    {
        final var relative = base.relativize(source);
        final var target = getOutputDirectory().get().getAsFile().toPath().resolve("%X".formatted(relative.hashCode()));
        return target.resolve(source.getFileName() + ".o");
    }
}
