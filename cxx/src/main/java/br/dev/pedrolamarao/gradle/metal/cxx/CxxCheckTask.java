package br.dev.pedrolamarao.gradle.metal.cxx;

import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.SourceTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.process.ExecOperations;

import javax.inject.Inject;
import java.util.ArrayList;

public abstract class CxxCheckTask extends SourceTask
{
    final ExecOperations execOperations;

    @InputDirectory
    public abstract DirectoryProperty getBuildPath ();

    @Input
    public abstract ListProperty<String> getOptions ();

    @Inject
    public CxxCheckTask (ExecOperations execOperations)
    {
        this.execOperations = execOperations;
    }

    @TaskAction
    public void check ()
    {
        final var command = new ArrayList<String>();
        command.add("clang-check");
        command.add("-p");
        command.add(getBuildPath().get().getAsFile().toString());
        command.addAll(getOptions().get());
        getSource().forEach(source -> command.add(source.toString()));

        execOperations.exec(it ->
        {
            it.commandLine(command);
        });
    }
}
