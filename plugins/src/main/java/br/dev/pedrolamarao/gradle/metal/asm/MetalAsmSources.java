package br.dev.pedrolamarao.gradle.metal.asm;

import org.gradle.api.DefaultTask;
import org.gradle.api.Named;
import org.gradle.api.NonNullApi;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.TaskOutputs;
import org.gradle.api.tasks.TaskProvider;

import javax.inject.Inject;

@NonNullApi
public class MetalAsmSources implements Named
{
    private final ListProperty<String> compileOptions;

    private final TaskProvider<MetalAsmCompileTask> compileTask;

    private final ConfigurableFileCollection headers;

    private final String name;

    private final SourceDirectorySet sources;

    @Inject
    public MetalAsmSources (ListProperty<String> compileOptions, TaskProvider<MetalAsmCompileTask> compileTask, ConfigurableFileCollection headers, String name, SourceDirectorySet sources)
    {
        this.compileOptions = compileOptions;
        this.compileTask = compileTask;
        this.headers = headers;
        this.name = name;
        this.sources = sources;
    }

    public ListProperty<String> getCompileOptions ()
    {
        return compileOptions;
    }

    public ConfigurableFileCollection getHeaders ()
    {
        return headers;
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

    public void header (Object... sources)
    {
        compileTask.configure(it -> it.getHeaderDependencies().from(sources));
    }
}
