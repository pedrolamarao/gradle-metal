package br.dev.pedrolamarao.gradle.metal.ixx;

import br.dev.pedrolamarao.gradle.metal.base.Metal;
import br.dev.pedrolamarao.gradle.metal.base.MetalBasePlugin;
import br.dev.pedrolamarao.gradle.metal.base.MetalCompileTask;
import br.dev.pedrolamarao.gradle.metal.base.MetalExtension;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.Directory;

/**
 * C++ module interface language support.
 */
public class MetalIxxPlugin implements Plugin<Project>
{
    /**
     * {@inheritDoc}
     */
    @Override
    public void apply (Project project)
    {
        project.getPluginManager().apply(MetalBasePlugin.class);

        final var metal = project.getExtensions().getByType(MetalExtension.class);

        final var ixx = project.getObjects().domainObjectContainer(MetalIxxSourceSet.class, name -> createSources(project,name));
        metal.getExtensions().add("ixx", ixx);
    }

    static MetalIxxSourceSet createSources (Project project, String name)
    {
        final var configurations = project.getConfigurations();
        final var layout = project.getLayout();
        final var metal = project.getExtensions().getByType(MetalExtension.class);
        final var objects = project.getObjects();
        final var tasks = project.getTasks();

        Metal.maybeCreateConfigurations(configurations,name);

        final var compilables = objects.fileCollection();
        final var importables = objects.fileCollection();

        final var commandsTask = tasks.register("commands-%s-ixx".formatted(name), MetalIxxCommandsTask.class);
        final var compileTask = tasks.register("compile-%s-ixx".formatted(name), MetalIxxCompileTask.class);
        compilables.from(compileTask);
        importables.from(compileTask.flatMap(MetalCompileTask::getTargetDirectory));
        final var sourceSet = objects.newInstance(MetalIxxSourceSet.class,compilables,importables,name);
        sourceSet.getCompileOptions().convention(metal.getCompileOptions());
        sourceSet.getImport().from(configurations.named(name + Metal.IMPORTABLE_DEPENDENCIES));
        sourceSet.getInclude().from(configurations.named(name + Metal.INCLUDABLE_DEPENDENCIES));
        sourceSet.getSources().from(layout.getProjectDirectory().dir("src/%s/ixx".formatted(name)));
        sourceSet.getTargets().convention(metal.getTargets());

        final var commandsDirectory = layout.getBuildDirectory().dir("db/%s/ixx".formatted(name));
        final var objectDirectory = layout.getBuildDirectory().dir("bmi/%s/ixx".formatted(name));

        commandsTask.configure(task ->
        {
            task.getCompileOptions().convention(sourceSet.getCompileOptions());
            task.getImport().from(sourceSet.getImport());
            task.getInclude().from(sourceSet.getInclude());
            task.getObjectDirectory().convention(objectDirectory.map(Directory::getAsFile));
            task.getOutputDirectory().convention(commandsDirectory);
            task.setSource(sourceSet.getSources());
            task.getTarget().convention(metal.getTarget());
        });

        compileTask.configure(task ->
        {
            task.onlyIf("target is enabled",it -> sourceSet.getTargets().zip(task.getTarget(),(targets,target) -> targets.isEmpty() || targets.contains(target)).get());
            task.getCompileOptions().convention(sourceSet.getCompileOptions());
            task.getImport().from(sourceSet.getImport());
            task.getInclude().from(sourceSet.getInclude());
            task.getOutputDirectory().convention(objectDirectory);
            task.setSource(sourceSet.getSources());
            task.getTarget().convention(metal.getTarget());
        });

        configurations.named(Metal.COMMANDS_ELEMENTS).configure(it -> it.getOutgoing().artifact(commandsTask));

        project.afterEvaluate(__ ->
        {
            if (sourceSet.getPublic().get()) {
                configurations.named(Metal.IMPORTABLE_ELEMENTS).configure(configuration -> {
                    configuration.getOutgoing().artifact(compileTask.map(MetalIxxCompileTask::getTargetDirectory),it->it.builtBy(compileTask));
                });
            }
        });

        tasks.named("compile").configure(it -> it.dependsOn(compileTask));

        return sourceSet;
    }
}
