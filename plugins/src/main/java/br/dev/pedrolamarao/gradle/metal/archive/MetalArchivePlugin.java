package br.dev.pedrolamarao.gradle.metal.archive;

import br.dev.pedrolamarao.gradle.metal.base.MetalArchive;
import br.dev.pedrolamarao.gradle.metal.base.MetalBasePlugin;
import br.dev.pedrolamarao.gradle.metal.base.MetalComponentPlugin;
import br.dev.pedrolamarao.gradle.metal.base.MetalExtension;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class MetalArchivePlugin extends MetalComponentPlugin implements Plugin<Project>
{
    @Override
    public void apply (Project project)
    {
        project.getPluginManager().apply(MetalBasePlugin.class);

        final var metal = project.getExtensions().getByType(MetalExtension.class);
        final var tasks = project.getTasks();

        final var archives = (NamedDomainObjectContainer<?>) metal.getExtensions().getByName("archives");
        final var archive = (MetalArchive) archives.create("main");
        configure(project,archive);

        tasks.named("assemble").configure(it -> it.dependsOn(archive.getArchiveTask()));
    }
}
