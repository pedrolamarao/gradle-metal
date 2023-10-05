package br.dev.pedrolamarao.gradle.metal.base;

import org.gradle.api.provider.Property;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.services.ServiceReference;
import org.gradle.api.tasks.Input;
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
    public abstract Property<String> getTarget ();

    /**
     * Metal service.
     *
     * @return service
     */
    @ServiceReference
    protected abstract Property<MetalService> getMetal ();

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
    protected MetalSourceTask ()
    {
        getTarget().convention( getProviders().gradleProperty("metal.target").orElse( getMetal().flatMap(MetalService::getHostTarget) ) );
    }
}
