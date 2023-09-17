package br.dev.pedrolamarao.gradle.cxx.application;

import br.dev.pedrolamarao.gradle.cxx.language.CxxCompileTask;
import br.dev.pedrolamarao.gradle.cxx.language.CxxExtension;
import br.dev.pedrolamarao.gradle.cxx.language.CxxLanguagePlugin;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.nio.file.Path;
import java.nio.file.Paths;

public class CxxApplicationPlugin implements Plugin<Project>
{
    @Override
    public void apply (Project project)
    {
        project.getPluginManager().apply(CxxLanguagePlugin.class);
        final var compileTask = project.getTasks().register("compile");
        compileTask.get().setGroup("cxx");
        project.afterEvaluate(p ->
        {
            final var extension = p.getExtensions().getByType(CxxExtension.class);
            final var sourceSets = extension.getSourceSets();
            sourceSets.forEach(sourceSet ->
            {
                final var sourceSetName = sourceSet.getName();
                final var compileSourceSetTaskName = "compile%s".formatted(sourceSetName);
                final var compileSourceSetTask = p.getTasks().register(compileSourceSetTaskName);
                sourceSet.forEach(source ->
                {
                    final var target = toTarget(project,source.toPath());
                    final var sourceName = source.getName();
                    final var compileSourceTaskName = "compile%s%s".formatted(sourceSetName,sourceName);
                    final var compileSourceTask = p.getTasks().register(compileSourceTaskName,CxxCompileTask.class);
                    compileSourceTask.get().getSource().set(source);
                    compileSourceTask.get().getTarget().set(target.toFile());
                    compileSourceSetTask.get().getDependsOn().add(compileSourceTask);
                    p.getLogger().info("{}",compileSourceTaskName);
                });
                p.getLogger().info("{}",compileSourceSetTaskName);
                compileTask.get().getDependsOn().add(compileSourceSetTask);
            });
        });
    }

    static Path toTarget (Project project, Path source)
    {
        project.getLogger().info("toTarget: 1: {}",source);
        final var relative = project.getProjectDir().toPath().relativize(source);
        project.getLogger().info("toTarget: 2: {}",relative);
        final var target = project.getBuildDir().toPath().resolve("obj").resolve(relative);
        project.getLogger().info("toTarget: 3: {}",target);
        return target.resolveSibling(target.getFileName() + ".o");
    }
}
