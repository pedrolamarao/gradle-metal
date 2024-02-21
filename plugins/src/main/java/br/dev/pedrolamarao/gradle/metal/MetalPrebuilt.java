package br.dev.pedrolamarao.gradle.metal;

import org.gradle.api.Action;
import org.gradle.api.NonNullApi;
import org.gradle.api.artifacts.ConfigurablePublishArtifact;
import org.gradle.api.artifacts.ConfigurationContainer;

import javax.inject.Inject;

import static br.dev.pedrolamarao.gradle.metal.Metal.LINKABLE_ELEMENTS;

/**
 * Prebuilt component extension.
 */
@NonNullApi
public abstract class MetalPrebuilt
{
    /**
     * Configuration container.
     *
     * @return container
     */
    @Inject
    protected abstract ConfigurationContainer getConfigurations ();

    /**
     * Constructor.
     */
    @Inject
    public MetalPrebuilt ()
    {
    }

    /**
     * Publishes component include source.
     *
     * @param notation source notation
     */
    public void includable (Object notation)
    {
        getConfigurations().named(Metal.INCLUDABLE_ELEMENTS).configure(configuration ->
            configuration.getOutgoing().artifact(notation)
        );
    }

    /**
     * Publishes component include source.
     *
     * @param notation  source notation
     * @param configure configuration action
     */
    public void includable (Object notation, Action<? super ConfigurablePublishArtifact> configure)
    {
        getConfigurations().named(Metal.INCLUDABLE_ELEMENTS).configure(configuration ->
            configuration.getOutgoing().artifact(notation,configure)
        );
    }

    /**
     * Publishes component link source.
     *
     * @param notation source notation
     */
    public void linkable (Object notation)
    {
        getConfigurations().named(LINKABLE_ELEMENTS).configure(configuration ->
            configuration.getOutgoing().artifact(notation)
        );
    }

    /**
     * Publishes component link source.
     *
     * @param notation  source notation
     * @param configure configuration action
     */
    public void linkable (Object notation, Action<? super ConfigurablePublishArtifact> configure)
    {
        getConfigurations().named(LINKABLE_ELEMENTS).configure(configuration ->
            configuration.getOutgoing().artifact(notation,configure)
        );
    }
}
