package br.dev.pedrolamarao.gradle.metal.cxx;

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
public class MetalCxxSources implements Named
{
    private final ListProperty<String> compileOptions;

    private final TaskProvider<CxxCompileTask> compileTask;

    private final ConfigurableFileCollection headers;

    private final ConfigurableFileCollection modules;

    private final String name;

    private final SourceDirectorySet sources;

    @Inject
    public MetalCxxSources (ListProperty<String> compileOptions, TaskProvider<CxxCompileTask> compileTask, ConfigurableFileCollection headers, ConfigurableFileCollection modules, String name, SourceDirectorySet sources)
    {
        this.compileOptions = compileOptions;
        this.compileTask = compileTask;
        this.headers = headers;
        this.modules = modules;
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

    public ConfigurableFileCollection getModules ()
    {
        return modules;
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

    public void module (Object... sources)
    {
        compileTask.configure(it -> it.getModuleDependencies().from(sources));
    }
}
