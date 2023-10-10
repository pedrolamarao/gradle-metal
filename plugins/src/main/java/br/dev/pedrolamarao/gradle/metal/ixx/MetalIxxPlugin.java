package br.dev.pedrolamarao.gradle.metal.ixx;

import br.dev.pedrolamarao.gradle.metal.base.Metal;
import br.dev.pedrolamarao.gradle.metal.base.MetalBasePlugin;
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

        final var ixx = project.getObjects().domainObjectContainer(MetalIxxSources.class, name -> createSources(project,name));
        metal.getExtensions().add("ixx", ixx);
    }

    static MetalIxxSources createSources (Project project, String name)
    {
        final var configurations = project.getConfigurations();
        final var layout = project.getLayout();
        final var metal = project.getExtensions().findByType(MetalExtension.class);
        final var objects = project.getObjects();
        final var tasks = project.getTasks();

        final var commandsTask = tasks.register("commands-%s-ixx".formatted(name), MetalIxxCommandsTask.class);
        final var compileTask = tasks.register("compile-%s-ixx".formatted(name), MetalIxxCompileTask.class);

        final var sourceSet = objects.newInstance(MetalIxxSources.class,compileTask,name);
        sourceSet.getCompileOptions().convention( metal.getCompileOptions() );
        sourceSet.getImports().from( configurations.named(Metal.IMPORTABLE_DEPENDENCIES) );
        sourceSet.getIncludes().from( configurations.named(Metal.INCLUDABLE_DEPENDENCIES) );
        sourceSet.getSources().from( layout.getProjectDirectory().dir("src/%s/ixx".formatted(name)) );

        final var commandsDirectory = layout.getBuildDirectory().dir("db/%s/ixx".formatted(name));
        final var objectDirectory = layout.getBuildDirectory().dir("bmi/%s/ixx".formatted(name));

        commandsTask.configure(task ->
        {
            task.getCompileOptions().convention(sourceSet.getCompileOptions());
            task.getIncludables().from(sourceSet.getIncludes());
            task.getImportables().from(sourceSet.getImports());
            task.getObjectDirectory().set(objectDirectory.map(Directory::getAsFile));
            task.getOutputDirectory().set(commandsDirectory);
            task.setSource(sourceSet.getSources());
        });
        configurations.named(Metal.COMMANDS_ELEMENTS).configure(it -> it.getOutgoing().artifact(commandsTask));

        compileTask.configure(task ->
        {
            task.getCompileOptions().convention(sourceSet.getCompileOptions());
            task.getIncludables().from(sourceSet.getIncludes());
            task.getImportables().from(sourceSet.getImports());
            task.getOutputDirectory().set(objectDirectory);
            task.setSource(sourceSet.getSources());
        });

        project.afterEvaluate(__ ->
        {
            if (sourceSet.getPublic().get()) {
                configurations.named(Metal.IMPORTABLE_ELEMENTS).configure(configuration -> {
                    configuration.getOutgoing().artifact(compileTask.map(MetalIxxCompileTask::getTargetDirectory),it->it.builtBy(compileTask));
                });
            }
        });

        return sourceSet;
    }
}
