package br.dev.pedrolamarao.gradle.metal.archive;

import br.dev.pedrolamarao.gradle.metal.application.MetalApplicationBasePlugin;
import br.dev.pedrolamarao.gradle.metal.base.MetalApplication;
import br.dev.pedrolamarao.gradle.metal.base.MetalArchive;
import br.dev.pedrolamarao.gradle.metal.base.MetalComponentPlugin;
import br.dev.pedrolamarao.gradle.metal.base.MetalExtension;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Archive convention plugin.
 *
 * <p>
 *     Configures conventional <code>main</code> archive and <code>test</code> application.
 * </p>
 */
public class MetalArchivePlugin extends MetalComponentPlugin implements Plugin<Project>
{
    /**
     * {@inheritDoc}
     */
    @Override
    public void apply (Project project)
    {
        project.getPluginManager().apply(MetalApplicationBasePlugin.class);
        project.getPluginManager().apply(MetalArchiveBasePlugin.class);

        final var metal = project.getExtensions().getByType(MetalExtension.class);
        final var tasks = project.getTasks();

        final var archives = (NamedDomainObjectContainer<?>) metal.getExtensions().getByName("archives");
        final var archive = (MetalArchive) archives.create("main");
        configure(project,archive);

        final var applications = (NamedDomainObjectContainer<?>) metal.getExtensions().getByName("applications");
        final var application = (MetalApplication) applications.create("test");
        configure(project,application);

        tasks.named("assemble").configure(it -> it.dependsOn(archive.getArchiveTask()));
    }
}
