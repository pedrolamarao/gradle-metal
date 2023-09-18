package br.dev.pedrolamarao.gradle.cxx.language;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Array;
import java.util.ArrayList;

public abstract class CxxCompileTask extends SourceTask
{
    @Input
    public abstract ListProperty<String> getOptions ();

    @OutputFile
    public abstract DirectoryProperty getTargetDirectory ();

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
                command.addAll(getOptions().get());
                command.add("-c");
                command.add(source.toString());
                command.add("-o");
                command.add(target.toString());
                getLogger().info("{}", command);

                final var processBuilder = new ProcessBuilder();
                processBuilder.command(command);
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
            }
        }
    }

    Path toTargetPath (Project project, Path source)
    {
        project.getLogger().info("toTargetPath: 1: {}",source);
        final var relative = project.getProjectDir().toPath().relativize(source);
        project.getLogger().info("toTargetPath: 2: {}",relative);
        final var target = getTargetDirectory().get().getAsFile().toPath().resolve("%X".formatted(relative.hashCode()));
        project.getLogger().info("toTargetPath: 3: {}",target);
        return target.resolve(source.getFileName() + ".o");
    }
}
