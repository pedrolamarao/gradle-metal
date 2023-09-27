package br.dev.pedrolamarao.gradle.metal.asm;

import org.gradle.api.DefaultTask;
import org.gradle.api.Named;
import org.gradle.api.NonNullApi;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.TaskOutputs;
import org.gradle.api.tasks.TaskProvider;

import javax.inject.Inject;

@NonNullApi
public class MetalAsmSources implements Named
{
    private final TaskProvider<MetalAsmCommandsTask> commandsTask;

    private final ListProperty<String> compileOptions;

    private final TaskProvider<MetalAsmCompileTask> compileTask;

    private final String name;

    @Inject
    public MetalAsmSources (TaskProvider<MetalAsmCommandsTask> commandsTask, ListProperty<String> compileOptions, TaskProvider<MetalAsmCompileTask> compileTask, String name)
    {
        this.commandsTask = commandsTask;
        this.compileOptions = compileOptions;
        this.compileTask = compileTask;
        this.name = name;
    }

    public ListProperty<String> getCompileOptions ()
    {
        return compileOptions;
    }

    @Override
    public String getName ()
    {
        return name;
    }

    public Provider<TaskOutputs> getOutputs ()
    {
        return compileTask.map(DefaultTask::getOutputs);
    }

    public void includable (Object... sources)
    {
        commandsTask.configure(it -> it.getIncludables().from(sources));
        compileTask.configure(it -> it.getIncludables().from(sources));
    }
}
