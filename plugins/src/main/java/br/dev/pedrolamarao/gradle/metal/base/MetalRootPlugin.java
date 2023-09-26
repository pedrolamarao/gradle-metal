package br.dev.pedrolamarao.gradle.metal.base;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class MetalRootPlugin implements Plugin<Project>
{
    @Override
    public void apply (Project project)
    {
        project.getPluginManager().apply(MetalBasePlugin.class);

        final var commandsDependencies = project.getConfigurations().create(MetalBasePlugin.COMMANDS_DEPENDENCIES);
        commandsDependencies.setCanBeConsumed(false);
        commandsDependencies.setCanBeResolved(true);
        commandsDependencies.attributes(it -> it.attribute(NativeCapability.ATTRIBUTE,NativeCapability.COMMANDS));

        project.getTasks().register("commands").configure(task ->
        {
            task.dependsOn(commandsDependencies.getBuildDependencies());

            task.doLast(__ ->
            {
                if (commandsDependencies.isEmpty()) {
                    task.getLogger().warn("{}: no compile databases to aggregate",task);
                    return;
                }

                final var list = new ArrayList<>();
                commandsDependencies.forEach(file -> {
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
