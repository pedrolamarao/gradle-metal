// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.asm;

import org.gradle.process.ExecOperations;
import org.gradle.workers.WorkAction;

import javax.inject.Inject;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

abstract class AsmCompileWorkAction implements WorkAction<AsmCompileWorkParameters>
{
    final ExecOperations execOperations;

    @Inject
    public AsmCompileWorkAction (ExecOperations execOperations)
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

    static Path toOutputPath (Path base, Path source, Path output)
    {
        final var relative = base.relativize(source);
        final var target = output.resolve("%X".formatted(relative.hashCode()));
        return target.resolve(source.getFileName() + ".o");
    }
}
