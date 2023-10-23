package br.dev.pedrolamarao.gradle.metal.base;

import org.gradle.api.Named;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.provider.SetProperty;

/**
 * Gradle Metal sources.
 */
public abstract class MetalSourceSet implements Named
{
    /**
     * Sources.
     *
     * @return configurable collection
     */
    public abstract ConfigurableFileCollection getSources ();

    /**
     * Targets.
     *
     * @return property
     */
    public abstract SetProperty<String> getTargets ();
}
