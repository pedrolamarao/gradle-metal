package br.dev.pedrolamarao.gradle.metal.c;

import br.dev.pedrolamarao.gradle.metal.base.MetalBasePlugin;
import br.dev.pedrolamarao.gradle.metal.base.MetalExtension;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.Directory;

import static br.dev.pedrolamarao.gradle.metal.base.Metal.COMMANDS_ELEMENTS;
import static br.dev.pedrolamarao.gradle.metal.base.Metal.INCLUDABLE_DEPENDENCIES;

/**
 * C language support plugin.
 */
public class MetalCPlugin implements Plugin<Project>
{
    /**
     * {@inheritDoc}
     */
    @Override
    public void apply (Project project)
    {
        project.getPluginManager().apply(MetalBasePlugin.class);

        final var metal = project.getExtensions().getByType(MetalExtension.class);

        final var c = project.getObjects().domainObjectContainer(MetalCSourceSet.class, name -> createSourceSet(project,name));
        metal.getExtensions().add("c", c);
    }

    static MetalCSourceSet createSourceSet (Project project, String name)
    {
        final var configurations = project.getConfigurations();
        final var layout = project.getLayout();
        final var metal = project.getExtensions().getByType(MetalExtension.class);
        final var objects = project.getObjects();
        final var tasks = project.getTasks();

        final var linkables = objects.fileCollection();

        final var commandsTask = tasks.register("commands-%s-c".formatted(name),MetalCCommandsTask.class);
        final var compileTask = tasks.register("compile-%s-c".formatted(name),MetalCCompileTask.class);
        linkables.from(compileTask);
        final var sourceSet = objects.newInstance(MetalCSourceSet.class,linkables,name);
        sourceSet.getCompileOptions().convention(metal.getCompileOptions());
        sourceSet.getInclude().from(configurations.named(INCLUDABLE_DEPENDENCIES));
        sourceSet.getSources().from(layout.getProjectDirectory().dir("src/%s/c".formatted(name)));
        sourceSet.getTargets().convention(metal.getTargets());

        final var commandsDirectory = layout.getBuildDirectory().dir("db/%s/c".formatted(name));
        final var objectDirectory = layout.getBuildDirectory().dir("obj/%s/c".formatted(name));

        commandsTask.configure(task ->
        {
            task.getCompileOptions().convention(sourceSet.getCompileOptions());
            task.getIncludables().from(sourceSet.getInclude());
            task.getObjectDirectory().set(objectDirectory.map(Directory::getAsFile));
            task.getOutputDirectory().set(commandsDirectory);
            task.setSource(sourceSet.getSources());
            task.getTarget().convention(metal.getTarget());
        });

        compileTask.configure(task ->
        {
            task.onlyIf("target is enabled",it -> sourceSet.getTargets().zip(task.getTarget(),(targets,target) -> targets.isEmpty() || targets.contains(target)).get());
            task.getCompileOptions().convention(sourceSet.getCompileOptions());
            task.getIncludables().from(sourceSet.getInclude());
            task.getOutputDirectory().set(objectDirectory);
            task.setSource(sourceSet.getSources());
            task.getTarget().convention(metal.getTarget());
        });

        configurations.named(COMMANDS_ELEMENTS).configure(it -> it.getOutgoing().artifact(commandsTask));

        tasks.named("compile").configure(it -> it.dependsOn(compileTask));

        return sourceSet;
    }
}
