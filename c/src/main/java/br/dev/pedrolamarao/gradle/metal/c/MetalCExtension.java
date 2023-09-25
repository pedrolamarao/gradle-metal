package br.dev.pedrolamarao.gradle.metal.c;

import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.plugins.ExtensionAware;

public abstract class MetalCExtension implements ExtensionAware
{
    private final NamedDomainObjectContainer<MetalCSources> sources;

    public MetalCExtension (NamedDomainObjectContainer<MetalCSources> sources)
    {
        this.sources = sources;
    }

    public NamedDomainObjectContainer<MetalCSources> getSources ()
    {
        return sources;
    }
}
