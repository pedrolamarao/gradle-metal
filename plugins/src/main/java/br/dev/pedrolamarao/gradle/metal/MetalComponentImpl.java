package br.dev.pedrolamarao.gradle.metal;

import groovy.lang.Closure;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.FileTreeElement;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.services.ServiceReference;
import org.gradle.api.specs.Spec;
import org.gradle.api.tasks.util.PatternFilterable;
import org.gradle.api.tasks.util.PatternSet;
import org.gradle.internal.Factory;

import javax.inject.Inject;
import java.util.Set;

abstract class MetalComponentImpl implements MetalComponent
{
    private final PatternSet patternSet;

    @Inject
    public MetalComponentImpl ()
    {
        patternSet = getPatternSetFactory().create();
    }

    @ServiceReference
    protected abstract Property<MetalService> getMetal ();

    @Inject
    protected abstract Factory<PatternSet> getPatternSetFactory ();

    /**
     * Compiled object files
     *
     * @return property
     */
    public abstract ConfigurableFileCollection getObjectFiles ();

    /**
     * Compiled commands files.
     *
     * @return property
     */
    public abstract ConfigurableFileCollection getCommandFiles ();

    // MetalComponent

    @Override
    public Provider<String> getTarget ()
    {
        return getMetal().map(MetalService::getTarget);
    }

    // PatternFilterable

    @Override
    public Set<String> getIncludes ()
    {
        return patternSet.getIncludes();
    }

    @Override
    public Set<String> getExcludes ()
    {
        return patternSet.getExcludes();
    }

    @Override
    public PatternFilterable setIncludes (Iterable<String> includes)
    {
        return patternSet.setIncludes(includes);
    }

    @Override
    public PatternFilterable setExcludes (Iterable<String> excludes)
    {
        return patternSet.setExcludes(excludes);
    }

    @Override
    public PatternFilterable include (String... includes)
    {
        return patternSet.include(includes);
    }

    @Override
    public PatternFilterable include (Iterable<String> includes)
    {
        return patternSet.include(includes);
    }

    @Override
    public PatternFilterable include (Spec<FileTreeElement> includeSpec)
    {
        return patternSet.include(includeSpec);
    }

    @Override
    public PatternFilterable include (Closure includeSpec)
    {
        return patternSet.include(includeSpec);
    }

    @Override
    public PatternFilterable exclude (String... excludes)
    {
        return patternSet.exclude(excludes);
    }

    @Override
    public PatternFilterable exclude (Iterable<String> excludes)
    {
        return patternSet.exclude(excludes);
    }

    @Override
    public PatternFilterable exclude (Spec<FileTreeElement> excludeSpec)
    {
        return patternSet.exclude(excludeSpec);
    }

    @Override
    public PatternFilterable exclude (Closure excludeSpec)
    {
        return patternSet.exclude(excludeSpec);
    }
}
