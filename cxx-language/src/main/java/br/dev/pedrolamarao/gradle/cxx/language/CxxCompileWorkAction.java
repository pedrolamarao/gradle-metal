package br.dev.pedrolamarao.gradle.cxx.language;

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

        try
        {
            Files.createDirectories(parameters.getOutput().getAsFile().get().toPath().getParent());

            final var command = new ArrayList<String>();
            command.add("clang");
            if (parameters.getTarget() != null) {
                command.add("-target");
                command.add(parameters.getTarget().get());
            }
            command.addAll(parameters.getOptions().get());
            command.add("-c");
            command.add(parameters.getSource().get().toString());
            command.add("-o");
            command.add(parameters.getOutput().getAsFile().get().toString());
//            getLogger().info("{}", command);

            execOperations.exec(it ->
            {
                it.commandLine(command);
            });
        }
        catch (RuntimeException e) { throw e; }
        catch (Exception e) { throw new RuntimeException(e); }
    }
}
