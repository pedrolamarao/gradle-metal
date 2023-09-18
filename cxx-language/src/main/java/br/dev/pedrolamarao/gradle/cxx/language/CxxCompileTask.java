package br.dev.pedrolamarao.gradle.cxx.language;

import org.gradle.api.Project;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.*;
import org.gradle.api.tasks.options.Option;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public abstract class CxxCompileTask extends SourceTask
{
    @Input
    public abstract ListProperty<String> getOptions ();

    @OutputDirectory
    public abstract DirectoryProperty getOutputDirectory ();

    @Input @Option(option="target",description="code generation target") @Optional
    public abstract Property<String> getTarget ();

    @TaskAction
    public void compile () throws InterruptedException
    {
        final var processes = new ArrayList<Process>();

        getSource().forEach(source ->
        {
            try
            {
                final var target = toTargetPath(getProject(),source.toPath());
                Files.createDirectories(target.getParent());

                final var command = new ArrayList<String>();
                command.add("clang");
                if (getTarget().isPresent()) {
                    command.add("-target");
                    command.add(getTarget().get());
                }
                command.addAll(getOptions().get());
                command.add("-v");
                command.add("-c");
                command.add(source.toString());
                command.add("-o");
                command.add(target.toString());
                getLogger().info("{}", command);

                final var processBuilder = new ProcessBuilder();
                processBuilder.command(command);
                processBuilder.redirectError( getTemporaryDir().toPath().resolve("error").toFile() );
                processBuilder.redirectOutput( getTemporaryDir().toPath().resolve("out").toFile() );
                final var process = processBuilder.start();
                processes.add(process);
            }
            catch (RuntimeException e) { throw e; }
            catch (Exception e) { throw new RuntimeException(e); }
        });

        for (var process : processes) {
            final var status = process.waitFor();
            if (status != 0) {
                getLogger().error("clang failed: {}",status);
                throw new RuntimeException("clang failed: %s".formatted(status));
            }
        }
    }

    Path toTargetPath (Project project, Path source)
    {
        final var relative = project.getProjectDir().toPath().relativize(source);
        final var target = getOutputDirectory().get().getAsFile().toPath().resolve("%X".formatted(relative.hashCode()));
        return target.resolve(source.getFileName() + ".o");
    }
}
