// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Prebuilt component plugin.
 */
public class MetalPrebuiltPlugin implements Plugin<Project>
{
    @Override
    public void apply (Project project)
    {
        final var extensions = project.getExtensions();
        final var plugins = project.getPlugins();

        plugins.apply(MetalBasePlugin.class);

        extensions.create("prebuilt", MetalPrebuilt.class);
    }
}
