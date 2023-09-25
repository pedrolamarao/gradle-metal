package br.dev.pedrolamarao.gradle.metal.c;

import org.gradle.api.Named;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.provider.ListProperty;

import javax.inject.Inject;

public class MetalCSources implements Named
{
    private final ListProperty<String> compileOptions;

    private final ConfigurableFileCollection headers;

    private final String name;

    private final SourceDirectorySet sources;

    @Inject
    public MetalCSources (ListProperty<String> compileOptions, ConfigurableFileCollection headers, String name, SourceDirectorySet sources)
    {
        this.compileOptions = compileOptions;
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
}
