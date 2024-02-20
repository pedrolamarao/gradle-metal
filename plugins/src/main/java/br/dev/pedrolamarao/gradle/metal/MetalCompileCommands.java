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
        final var compileDirectory = getCompileDirectory().get();
        final var options = getOptions().get();
        final var output = getOutput().get();

        try (var writer = Files.newBufferedWriter(output.getAsFile().toPath(),UTF_8)) {
            writer.write("[\n");
            final String[] comma = {""};
            getSource().forEach(file ->
            {
                final var arguments = options.stream()
                    .collect(Collectors.joining("\", \"", "\"", "\""));

                final var compileOutput =
                    new File(compileDirectory,"%X/%s.%s".formatted(hash(file),file.getName(),"o"));

                try {
                    // ARGH
                    writer.write(comma[0]);
                    comma[0] = ",\n";

                    writer.write(
                        template.formatted(
                            arguments.replace("\\","\\\\"),
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
