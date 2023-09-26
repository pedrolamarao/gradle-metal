package br.dev.pedrolamarao.gradle.metal.base;

import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class MetalApplicationPlugin extends MetalComponentPlugin implements Plugin<Project>
{
    @Override
    public void apply (Project project)
    {
        project.getPluginManager().apply(MetalBasePlugin.class);

        final var metal = project.getExtensions().getByType(MetalExtension.class);
        final var tasks = project.getTasks();

        final var applications = (NamedDomainObjectContainer<?>) metal.getExtensions().getByName("applications");
        final var application = (MetalApplication) applications.create("main");
        configure(project,application);

        tasks.named("assemble").configure(it -> it.dependsOn(application.getLinkTask()));
    }
}
