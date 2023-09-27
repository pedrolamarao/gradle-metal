package br.dev.pedrolamarao.gradle.metal.ixx;

import org.gradle.api.Named;
import org.gradle.api.NonNullApi;
import org.gradle.api.file.Directory;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.TaskProvider;

import javax.inject.Inject;

@NonNullApi
public class MetalIxxSources implements Named
{
    private final ListProperty<String> compileOptions;

    private final TaskProvider<MetalIxxCompileTask> compileTask;

    private final String name;

    private final SourceDirectorySet sources;

    @Inject
    public MetalIxxSources (ListProperty<String> compileOptions, TaskProvider<MetalIxxCompileTask> compileTask, String name, SourceDirectorySet sources)
    {
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

    public Provider<Directory> getOutputDirectory ()
    {
        return compileTask.flatMap(MetalIxxCompileTask::getOutputDirectory);
    }

    public SourceDirectorySet getSources ()
    {
        return sources;
    }

    public void importable (Object... sources)
    {
        compileTask.configure(it -> it.getImportables().from(sources));
    }
}
