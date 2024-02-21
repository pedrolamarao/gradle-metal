package br.dev.pedrolamarao.gradle.metal;

import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.SourceTask;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Collectors;

import static br.dev.pedrolamarao.gradle.metal.MetalCompileImpl.hash;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Gradle Metal compile commands task.
 */
public abstract class MetalCompileCommands extends SourceTask
{
    // properties

    /**
     * Compiler base directory.
     *
     * @return property
     */
    @Input
    public abstract Property<File> getDirectory ();

    /**
     * Compiler output directory.
     *
     * @return property
     */
    @Input
    public abstract Property<File> getCompileDirectory ();

    /**
     * Compile command.
     *
     * @return property
     */
    @Input
    public abstract ListProperty<String> getCompileCommand ();

    /**
     * Output file.
     *
     * @return property
     */
    @OutputFile
    public abstract RegularFileProperty getOutput ();

    /**
     * Compiler target.
     *
     * @return property
     */
    @Input
    public abstract Property<String> getTarget ();

    // services

    // task

    /**
     * Constructor.
     */
    public MetalCompileCommands ()
    {
    }

    private static final String template =
          """
          {
            "arguments": [ %s ],
            "directory": "%s",
            "file": "%s",
            "output": "%s"
          }""";

    /**
     * Generate action.
     *
     * @throws IOException in case of failure
     */
    @TaskAction
    public void generate () throws IOException
    {
        final var directory = getDirectory().get().toString().replace("\\","\\\\");
        final var compileDirectory = getCompileDirectory().get();
        final var arguments = getCompileCommand().get().stream()
            .collect(Collectors.joining("\", \"", "\"", "\""))
            .replace("\\","\\\\");
        final var taskOutput = getOutput().get();

        try (var writer = Files.newBufferedWriter(taskOutput.getAsFile().toPath(),UTF_8)) {
            final String[] comma = {""};
            writer.write("[\n");
            getSource().forEach(file ->
            {
                final var output = new File(compileDirectory,"%X/%s.%s".formatted(hash(file),file.getName(),"o"));

                try {
                    // ARGH
                    writer.write(comma[0]);
                    comma[0] = ",\n";

                    writer.write(
                        template.formatted(
                            arguments,
                            directory,
                            file.toString().replace("\\","\\\\"),
                            output.toString().replace("\\","\\\\")
                        )
                    );
                }
                catch (IOException e) { throw new RuntimeException(e); }
            });
            writer.write("\n]");
        }
    }
}
