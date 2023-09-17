package br.dev.pedrolamarao.gradle.cxx.language;

import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.plugins.ExtensionAware;

public abstract class CxxExtension implements ExtensionAware
{
    private final NamedDomainObjectContainer<SourceDirectorySet> sourceSets;

    public CxxExtension (ObjectFactory objectFactory)
    {
        sourceSets = objectFactory.domainObjectContainer(SourceDirectorySet.class,name -> objectFactory.sourceDirectorySet(name,name));
    }

    public NamedDomainObjectContainer<SourceDirectorySet> getSourceSets ()
    {
        return sourceSets;
    }
}
