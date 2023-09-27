package br.dev.pedrolamarao.gradle.metal.cxx;

import org.gradle.api.Named;
import org.gradle.api.NonNullApi;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.TaskOutputs;
import org.gradle.api.tasks.TaskProvider;

import javax.inject.Inject;

@NonNullApi
public class MetalCxxSources implements Named
{
    private final TaskProvider<MetalCxxCommandsTask> commandsTask;

    private final ListProperty<String> compileOptions;

    private final TaskProvider<MetalCxxCompileTask> compileTask;

    private final String name;

    @Inject
    public MetalCxxSources (TaskProvider<MetalCxxCommandsTask> commandsTask, ListProperty<String> compileOptions, TaskProvider<MetalCxxCompileTask> compileTask, String name)
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
        return compileTask.map(MetalCxxCompileTask::getOutputs);
    }

    public void importable (Object... sources)
    {
        commandsTask.configure(it -> it.getImportables().from(sources));
        compileTask.configure(it -> it.getImportables().from(sources));
    }

    public void includable (Object... sources)
    {
        commandsTask.configure(it -> it.getIncludables().from(sources));
        compileTask.configure(it -> it.getIncludables().from(sources));
    }

    public void source (Object... sources)
    {
        commandsTask.configure(it -> it.source(sources));
        compileTask.configure(it -> it.source(sources));
    }
}
