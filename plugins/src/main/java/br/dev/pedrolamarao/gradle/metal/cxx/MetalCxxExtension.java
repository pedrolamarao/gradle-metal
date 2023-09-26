package br.dev.pedrolamarao.gradle.metal.cxx;

import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.plugins.ExtensionAware;

public abstract class MetalCxxExtension implements ExtensionAware
{
    private final NamedDomainObjectContainer<MetalCxxSources> sources;

    public MetalCxxExtension (NamedDomainObjectContainer<MetalCxxSources> sources)
    {
        this.sources = sources;
    }

    public NamedDomainObjectContainer<MetalCxxSources> getSources ()
    {
        return sources;
    }
}
