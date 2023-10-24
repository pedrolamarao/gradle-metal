package br.dev.pedrolamarao.gradle.metal.base;

import org.gradle.api.Named;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.SetProperty;

/**
 * Gradle Metal component.
 */
public abstract class MetalComponent implements Named
{
    /**
     * Link dependencies.
     *
     * @return configurable collection
     */
    protected abstract ConfigurableFileCollection getLink ();

    /**
     * Output file.
     *
     * @return property
     */
    public abstract RegularFileProperty getOutput ();

    /**
     * Component source.
     *
     * @return configurable collection
     */
    public abstract ConfigurableFileCollection getSource ();

    /**
     * Component target.
     *
     * @return property
     */
    public abstract SetProperty<String> getTargets ();
}
