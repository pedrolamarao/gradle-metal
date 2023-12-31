package br.dev.pedrolamarao.gradle.metal.base;

import org.gradle.api.GradleException;
import org.gradle.api.artifacts.ConfigurationContainer;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Metal utilities.
 */
public class Metal
{
    /**
     * Commands dependencies configuration name.
     */
    public static final String COMMANDS_DEPENDENCIES = "commandsDependencies";

    /**
     * Commands elements configuration name.
     */
    public static final String COMMANDS_ELEMENTS = "commandsElements";

    /**
     * Empty elements configuration name. (Probably obsolete.)
     */
    public static final String EMPTY_ELEMENTS = "emptyElements";

    /**
     * Executable dependencies configuration name.
     */
    public static final String EXECUTABLE_DEPENDENCIES = "executableDependencies";

    /**
     * Executable elements configuration name.
     */
    public static final String EXECUTABLE_ELEMENTS = "executableElements";

    /**
     * Importable dependencies configuration name.
     */
    public static final String IMPORTABLE_DEPENDENCIES = "importableDependencies";

    /**
     * Importable elements configuration name.
     */
    public static final String IMPORTABLE_ELEMENTS = "importableElements";

    /**
     * Includable dependencies configuration name.
     */
    public static final String INCLUDABLE_DEPENDENCIES = "includableDependencies";

    /**
     * Includable elements configuration name.
     */
    public static final String INCLUDABLE_ELEMENTS = "includableElements";

    /**
     * Linkable dependencies configuration name.
     */
    public static final String LINKABLE_DEPENDENCIES = "linkableDependencies";

    /**
     * Linkable elements configuration name.
     */
    public static final String LINKABLE_ELEMENTS = "linkableElements";

    public static void maybeCreateConfigurations (ConfigurationContainer configurations, String name)
    {
        if (configurations.findByName("%sApi".formatted(name)) != null) return;

        final var api = configurations.dependencyScope("%sApi".formatted(name), configuration -> {
            configuration.setDescription("metal api component dependencies");
            configuration.extendsFrom(configurations.getByName("api"));
        });
        final var implementation = configurations.dependencyScope("%sImplementation".formatted(name), configuration -> {
            configuration.setDescription("metal implementation component dependencies");
            configuration.extendsFrom(api.get());
            configuration.extendsFrom(configurations.getByName("implementation"));
        });
        configurations.resolvable(name + Metal.EXECUTABLE_DEPENDENCIES, configuration -> {
            configuration.attributes(it -> {
                it.attribute(MetalCapability.ATTRIBUTE, MetalCapability.EXECUTABLE);
                it.attribute(MetalVisibility.ATTRIBUTE, MetalVisibility.RUN);
            });
            configuration.extendsFrom(implementation.get());
            configuration.setDescription("metal executable dependencies");
        });
        configurations.resolvable(name + Metal.IMPORTABLE_DEPENDENCIES, configuration -> {
            configuration.attributes(it -> {
                it.attribute(MetalCapability.ATTRIBUTE, MetalCapability.IMPORTABLE);
                it.attribute(MetalVisibility.ATTRIBUTE, MetalVisibility.COMPILE);
            });
            configuration.extendsFrom(implementation.get());
            configuration.setDescription("metal importable dependencies");
        });
        configurations.resolvable(name + Metal.INCLUDABLE_DEPENDENCIES, configuration -> {
            configuration.attributes(it -> {
                it.attribute(MetalCapability.ATTRIBUTE, MetalCapability.INCLUDABLE);
                it.attribute(MetalVisibility.ATTRIBUTE, MetalVisibility.COMPILE);
            });
            configuration.extendsFrom(implementation.get());
            configuration.setDescription("metal includable dependencies");
        });
        configurations.resolvable(name + Metal.LINKABLE_DEPENDENCIES, configuration -> {
            configuration.attributes(it -> {
                it.attribute(MetalCapability.ATTRIBUTE, MetalCapability.LINKABLE);
                it.attribute(MetalVisibility.ATTRIBUTE, MetalVisibility.COMPILE);
            });
            configuration.extendsFrom(implementation.get());
            configuration.setDescription("metal linkable dependencies");
        });
    }

    /**
     * Find executable file in path.
     *
     * @param list  path
     * @param name  executable name
     * @return      executable file path
     * @throws GradleException if file not found
     */
    public static File toExecutableFile (String list, String name)
    {
        for (var item : list.split(File.pathSeparator))
        {
            final var directory = Paths.get(item);
            if (! Files.isDirectory(directory)) continue;
            final var file = directory.resolve(name);
            if (Files.isExecutable(file)) return file.toFile();
            final var file_exe = file.resolveSibling(name + ".exe");
            if (Files.isExecutable(file_exe)) return file_exe.toFile();
        }

        throw new GradleException("executable file not found: " + name);
    }
}
