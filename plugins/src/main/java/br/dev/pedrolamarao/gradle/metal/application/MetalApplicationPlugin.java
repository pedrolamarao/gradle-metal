package br.dev.pedrolamarao.gradle.metal.application;

import br.dev.pedrolamarao.gradle.metal.base.MetalApplication;
import br.dev.pedrolamarao.gradle.metal.base.MetalComponentPlugin;
import br.dev.pedrolamarao.gradle.metal.base.MetalExtension;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Application convention plugin.
 *
 * <p>
 *     Configures a conventional <code>main</code> application.
 * </p>
 */
public class MetalApplicationPlugin extends MetalComponentPlugin implements Plugin<Project>
{
    /**
     * {@inheritDoc}
     */
    @Override
    public void apply (Project project)
    {
        project.getPluginManager().apply(MetalApplicationBasePlugin.class);

        final var metal = project.getExtensions().getByType(MetalExtension.class);

        final var applications = (NamedDomainObjectContainer<?>) metal.getExtensions().getByName("applications");
        final var application = (MetalApplication) applications.create("main");
        configure(project,application);
    }
}
