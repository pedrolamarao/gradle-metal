// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal;

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
        final var configurations = project.getConfigurations();
        final var extensions = project.getExtensions();
        final var plugins = project.getPlugins();

        plugins.apply(MetalBasePlugin.class);

        extensions.create("prebuilt",Extension.class);

        final var api = configurations.dependencyScope("api", configuration -> {
            configuration.setDescription("prebuilt project api dependencies");
        });

        configurations.consumable(Metal.IMPORTABLE_ELEMENTS, configuration -> {
            configuration.attributes(it -> {
                it.attribute(MetalCapability.ATTRIBUTE, MetalCapability.IMPORTABLE);
                it.attribute(MetalVisibility.ATTRIBUTE, MetalVisibility.COMPILE);
            });
            configuration.extendsFrom(api.get());
            configuration.setDescription("prebuilt project importable elements");
        });

        configurations.consumable(Metal.INCLUDABLE_ELEMENTS, configuration -> {
            configuration.attributes(it -> {
                it.attribute(MetalCapability.ATTRIBUTE, MetalCapability.INCLUDABLE);
                it.attribute(MetalVisibility.ATTRIBUTE, MetalVisibility.COMPILE);
            });
            configuration.extendsFrom(api.get());
            configuration.setDescription("prebuilt project includable elements");
        });

        configurations.consumable(Metal.LINKABLE_ELEMENTS, configuration -> {
            configuration.attributes(it -> {
                it.attribute(MetalCapability.ATTRIBUTE, MetalCapability.LINKABLE);
                it.attribute(MetalVisibility.ATTRIBUTE, MetalVisibility.COMPILE);
            });
            configuration.extendsFrom(api.get());
            configuration.setDescription("prebuilt project linkable elements");
        });
    }
}
