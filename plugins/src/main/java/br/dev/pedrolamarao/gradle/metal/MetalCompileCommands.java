package br.dev.pedrolamarao.gradle.metal;

import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.services.ServiceReference;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.SourceTask;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static br.dev.pedrolamarao.gradle.metal.MetalCompile.hash;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Gradle Metal compile commands task.
 */
public abstract class MetalCompileCommands extends SourceTask
{
    // properties

    /**
     * Compiler output directory.
     *
     * @return property
     */
    @Input
    public abstract Property<File> getCompileDirectory ();

    /**
     * Compiler tool.
     *
     * @return property
     */
    @Input
    public abstract Property<String> getCompiler ();

    /**
     * Compiler options.
     *
     * @return property
     */
    @Input
    public abstract ListProperty<String> getOptions ();

    /**
     * Generated commands database file.
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

    /**
     * Gradle Metal service.
     *
     * @return service
     */
    @ServiceReference
    protected abstract Property<MetalService> getMetal ();

    // task

    /**
     * Constructor.
     */
    public MetalCompileCommands ()
    {
        getTarget().convention(getMetal().map(MetalService::getTarget));
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
        final var tool = getMetal().get().locateTool(getCompiler().get());
        final var directory = getProject().getProjectDir();
        final var options = getOptions().get();
        final var output = getOutput().get();

        try (var writer = Files.newBufferedWriter(output.getAsFile().toPath(),UTF_8)) {
            final String[] comma = {""};
            writer.write("[\n");
            getSource().forEach(file ->
            {
                final var arguments = Stream.concat(Stream.of(tool.toString()),options.stream())
                    .collect(Collectors.joining("\", \"", "\"", "\""));

                final var compileOutput =
                    new File(directory,"%X/%s.%s".formatted(hash(file),file.getName(),"o"));

                try {
                    // ARGH
                    writer.write(comma[0]);
                    comma[0] = ",\n";

                    writer.write(
                        template.formatted(
                            arguments.replace("\\","\\\\"),
                            directory.toString().replace("\\","\\\\"),
                            file.toString().replace("\\","\\\\"),
                            compileOutput.toString().replace("\\","\\\\")
                        )
                    );
                }
                catch (IOException e) { throw new RuntimeException(e); }
            });
            writer.write("\n]");
        }
    }
}
