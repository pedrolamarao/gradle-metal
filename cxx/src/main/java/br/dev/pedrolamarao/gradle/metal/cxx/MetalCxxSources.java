package br.dev.pedrolamarao.gradle.metal.cxx;

import org.gradle.api.Named;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.provider.ListProperty;

import javax.inject.Inject;

public class MetalCxxSources implements Named
{
    private final ListProperty<String> compileOptions;

    private final ConfigurableFileCollection headers;

    private final ConfigurableFileCollection modules;

    private final String name;

    private final SourceDirectorySet sources;

    @Inject
    public MetalCxxSources (ListProperty<String> compileOptions, ConfigurableFileCollection headers, ConfigurableFileCollection modules, String name, SourceDirectorySet sources)
    {
        this.compileOptions = compileOptions;
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
}
