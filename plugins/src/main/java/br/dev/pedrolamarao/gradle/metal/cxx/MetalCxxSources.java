package br.dev.pedrolamarao.gradle.metal.cxx;

import org.gradle.api.DefaultTask;
import org.gradle.api.Named;
import org.gradle.api.NonNullApi;
import org.gradle.api.file.SourceDirectorySet;
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

    private final SourceDirectorySet sources;

    @Inject
    public MetalCxxSources (TaskProvider<MetalCxxCommandsTask> commandsTask, ListProperty<String> compileOptions, TaskProvider<MetalCxxCompileTask> compileTask, String name, SourceDirectorySet sources)
    {
        this.commandsTask = commandsTask;
        this.compileOptions = compileOptions;
        this.compileTask = compileTask;
        this.name = name;
        this.sources = sources;
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

    public SourceDirectorySet getSources ()
    {
        return sources;
    }

    public Provider<TaskOutputs> getOutputs ()
    {
        return compileTask.map(DefaultTask::getOutputs);
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
}
