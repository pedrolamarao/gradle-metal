package br.dev.pedrolamarao.gradle.metal.commands;

import br.dev.pedrolamarao.gradle.metal.base.Metal;
import br.dev.pedrolamarao.gradle.metal.base.MetalBasePlugin;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Commands database plugin.
 */
public class MetalCommandsPlugin implements Plugin<Project>
{
    @Override
    public void apply (Project project)
    {
        project.getPluginManager().apply(MetalBasePlugin.class);

        final var commands = project.getConfigurations().dependencyScope("commands");
        final var commandsDependencies = project.getConfigurations().named(Metal.COMMANDS_DEPENDENCIES);
        commandsDependencies.configure(it -> it.extendsFrom(commands.get()));

        project.getTasks().register("commands").configure(task ->
        {
            task.dependsOn(commandsDependencies.map(it -> it.getBuildDependencies()));
            task.setGroup("metal");

            task.doLast(__ ->
            {
                if (commandsDependencies.get().isEmpty()) {
                    task.getLogger().warn("{}: no compile databases to aggregate",task);
                    return;
                }

                final var list = new ArrayList<>();
                commandsDependencies.get().getAsFileTree().forEach(file -> {
                    final var parsed = (List<?>) new groovy.json.JsonSlurper().parse(file);
                    list.addAll(parsed);
                });

                final var builder = new groovy.json.JsonBuilder();
                builder.call(list);

                try (var writer = Files.newBufferedWriter(project.file("compile_commands.json").toPath())) {
                    writer.write(builder.toPrettyString());
                }
                catch (IOException e) { throw new RuntimeException(e); }
            });
        });
    }
}
