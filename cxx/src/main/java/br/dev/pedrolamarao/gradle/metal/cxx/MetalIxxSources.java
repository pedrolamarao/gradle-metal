package br.dev.pedrolamarao.gradle.metal.cxx;

import org.gradle.api.Named;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.provider.ListProperty;

import javax.inject.Inject;

public class MetalIxxSources implements Named
{
    private final ListProperty<String> compileOptions;

    private final String name;

    private final SourceDirectorySet sources;

    @Inject
    public MetalIxxSources (ListProperty<String> compileOptions, String name, SourceDirectorySet sources)
    {
        this.compileOptions = compileOptions;
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
}
