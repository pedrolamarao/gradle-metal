package br.dev.pedrolamarao.gradle.cxx.application;

import br.dev.pedrolamarao.gradle.cxx.language.CxxCompileTask;
import br.dev.pedrolamarao.gradle.cxx.language.CxxExtension;
import br.dev.pedrolamarao.gradle.cxx.language.CxxLanguagePlugin;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class CxxApplicationPlugin implements Plugin<Project>
{
    @Override
    public void apply (Project project)
    {
        project.getPluginManager().apply(CxxLanguagePlugin.class);
        final var compileTask = project.getTasks().register("compile");
        compileTask.get().setGroup("build");
        project.afterEvaluate(p ->
        {
            final var extension = p.getExtensions().getByType(CxxExtension.class);
            final var sourceSets = extension.getSourceSets();
            sourceSets.forEach(sourceSet ->
            {
                final var sourceSetName = sourceSet.getName();
                final var compileSourceSetTaskName = "compile%s".formatted(sourceSetName);
                final var compileSourceSetTask = p.getTasks().register(compileSourceSetTaskName,CxxCompileTask.class);
                compileSourceSetTask.get().setSource(sourceSet.getAsFileTree());
                compileSourceSetTask.get().getOutputDirectory().set(project.getBuildDir().toPath().resolve("object/%s".formatted(sourceSetName)).toFile());
                p.getLogger().info("{}",compileSourceSetTaskName);
                compileTask.get().getDependsOn().add(compileSourceSetTask);
            });
        });
    }
}
