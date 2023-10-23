package br.dev.pedrolamarao.gradle.metal.cxx;

import br.dev.pedrolamarao.gradle.metal.base.Metal;
import br.dev.pedrolamarao.gradle.metal.base.MetalBasePlugin;
import br.dev.pedrolamarao.gradle.metal.base.MetalExtension;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.Directory;

import static br.dev.pedrolamarao.gradle.metal.base.Metal.IMPORTABLE_DEPENDENCIES;
import static br.dev.pedrolamarao.gradle.metal.base.Metal.INCLUDABLE_DEPENDENCIES;

/**
 * C++ language support plugin.
 */
public class MetalCxxPlugin implements Plugin<Project>
{
    /**
     * {@inheritDoc}
     */
    @Override
    public void apply (Project project)
    {
        project.getPluginManager().apply(MetalBasePlugin.class);

        final var metal = project.getExtensions().getByType(MetalExtension.class);

        final var cxx = project.getObjects().domainObjectContainer(MetalCxxSourceSet.class, name -> createSources(project,name));
        metal.getExtensions().add("cxx", cxx);
    }

    static MetalCxxSourceSet createSources (Project project, String name)
    {
        final var configurations = project.getConfigurations();
        final var layout = project.getLayout();
        final var metal = project.getExtensions().getByType(MetalExtension.class);
        final var objects = project.getObjects();
        final var tasks = project.getTasks();

        final var linkables = objects.fileCollection();

        final var commandsTask = tasks.register("commands-%s-cxx".formatted(name),MetalCxxCommandsTask.class);
        final var compileTask = tasks.register("compile-%s-cxx".formatted(name),MetalCxxCompileTask.class);
        linkables.from(compileTask);
        final var sourceSet = objects.newInstance(MetalCxxSourceSet.class,linkables,name);
        sourceSet.getCompileOptions().convention(metal.getCompileOptions());
        sourceSet.getImport().from(configurations.named(IMPORTABLE_DEPENDENCIES));
        sourceSet.getInclude().from(configurations.named(INCLUDABLE_DEPENDENCIES));
        sourceSet.getSources().from(layout.getProjectDirectory().dir("src/%s/cxx".formatted(name)));
        sourceSet.getTargets().convention(metal.getTargets());

        final var commandsDirectory = layout.getBuildDirectory().dir("db/%s/cxx".formatted(name));
        final var objectDirectory = layout.getBuildDirectory().dir("obj/%s/cxx".formatted(name));

        commandsTask.configure(task ->
        {
            task.getCompileOptions().set(sourceSet.getCompileOptions());
            task.getImport().from(sourceSet.getImport());
            task.getInclude().from(sourceSet.getInclude());
            task.getObjectDirectory().set(objectDirectory.map(Directory::getAsFile));
            task.getOutputDirectory().set(commandsDirectory);
            task.setSource(sourceSet.getSources().plus(sourceSet.getCompile()));
            task.getTarget().convention(metal.getTarget());
        });

        compileTask.configure(task ->
        {
            task.onlyIf("target is enabled",it -> sourceSet.getTargets().zip(task.getTarget(),(targets,target) -> targets.isEmpty() || targets.contains(target)).get());
            task.getCompileOptions().set(sourceSet.getCompileOptions());
            task.getImport().from(sourceSet.getImport());
            task.getInclude().from(sourceSet.getInclude());
            task.getOutputDirectory().set(objectDirectory);
            task.setSource(sourceSet.getSources().plus(sourceSet.getCompile()));
            task.getTarget().convention(metal.getTarget());
        });

        configurations.named(Metal.COMMANDS_ELEMENTS).configure(it -> it.getOutgoing().artifact(commandsTask));

        tasks.named("compile").configure(it -> it.dependsOn(compileTask));

        return sourceSet;
    }
}
