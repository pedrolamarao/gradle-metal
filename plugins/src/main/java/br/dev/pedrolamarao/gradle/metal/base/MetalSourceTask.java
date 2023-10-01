package br.dev.pedrolamarao.gradle.metal.base;

import org.gradle.api.provider.Property;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.SourceTask;

import javax.inject.Inject;

/**
 * Metal source task.
 */
public abstract class MetalSourceTask extends SourceTask
{
    /**
     * Metal target.
     *
     * @return property
     */
    @Input
    @Optional
    public abstract Property<String> getTarget ();

    /**
     * Provider factory service.
     *
     * @return service
     */
    @Inject
    protected abstract ProviderFactory getProviders ();

    /**
     * Constructor.
     */
    @Inject
    public MetalSourceTask ()
    {
        getTarget().convention(getProviders().gradleProperty("metal.target"));
    }
}
