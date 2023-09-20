// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.cxx;

import org.gradle.process.ExecOperations;
import org.gradle.workers.WorkAction;

import javax.inject.Inject;
import java.nio.file.Files;
import java.util.ArrayList;

abstract class CxxCompileWorkAction implements WorkAction<CxxCompileWorkParameters>
{
    final ExecOperations execOperations;

    @Inject
    public CxxCompileWorkAction (ExecOperations execOperations)
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
            parameters.getModuleDependencies().forEach(file -> command.add("-fmodule-file=%s".formatted(file)));
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
