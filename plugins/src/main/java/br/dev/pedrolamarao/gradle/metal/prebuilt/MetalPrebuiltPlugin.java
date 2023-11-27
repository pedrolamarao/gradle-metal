package br.dev.pedrolamarao.gradle.metal.prebuilt;

import br.dev.pedrolamarao.gradle.metal.Metal;
import br.dev.pedrolamarao.gradle.metal.base.MetalBasePlugin;
import br.dev.pedrolamarao.gradle.metal.base.MetalExtension;
import org.gradle.api.Action;
import org.gradle.api.NonNullApi;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ConfigurablePublishArtifact;
import org.gradle.api.artifacts.ConfigurationContainer;

import javax.inject.Inject;

import static br.dev.pedrolamarao.gradle.metal.Metal.LINKABLE_ELEMENTS;

/**
 * Prebuilt component plugin.
 */
public class MetalPrebuiltPlugin implements Plugin<Project>
{
    /**
     * Prebuilt component extension.
     */
    @NonNullApi
    public abstract static class Extension
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
        public Extension () { }

        /**
         * Publishes component include source.
         *
         * @param notation  source notation
         */
        public void includable (Object notation)
        {
            getConfigurations().named(Metal.INCLUDABLE_ELEMENTS).configure(configuration -> {
                configuration.getOutgoing().artifact(notation);
            });
        }

        /**
         * Publishes component include source.
         *
         * @param notation   source notation
         * @param configure  configuration action
         */
        public void includable (Object notation, Action<? super ConfigurablePublishArtifact> configure)
        {
            getConfigurations().named(Metal.INCLUDABLE_ELEMENTS).configure(configuration -> {
                configuration.getOutgoing().artifact(notation, configure);
            });
        }

        /**
         * Publishes component link source.
         *
         * @param notation  source notation
         */
        public void linkable (Object notation)
        {
            getConfigurations().named(LINKABLE_ELEMENTS).configure(configuration ->
            {
                configuration.getOutgoing().artifact(notation);
            });
        }

        /**
         * Publishes component link source.
         *
         * @param notation   source notation
         * @param configure  configuration action
         */
        public void linkable (Object notation, Action<? super ConfigurablePublishArtifact> configure)
        {
            getConfigurations().named(LINKABLE_ELEMENTS).configure(configuration -> {
                configuration.getOutgoing().artifact(notation, configure);
            });
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void apply (Project project)
    {
        project.getPluginManager().apply(MetalBasePlugin.class);
        project.getExtensions().getByType(MetalExtension.class).getExtensions().create("prebuilt", Extension.class);
    }
}
