package br.dev.pedrolamarao.gradle.metal.application;

import br.dev.pedrolamarao.gradle.metal.base.MetalBasePlugin;
import br.dev.pedrolamarao.gradle.metal.base.MetalComponentPlugin;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Application base plugin.
 */
public class MetalApplicationBasePlugin extends MetalComponentPlugin implements Plugin<Project>
{
    /**
     * {@inheritDoc}
     */
    @Override
    public void apply (Project project)
    {
        project.getPluginManager().apply(MetalBasePlugin.class);
    }
}
