// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal;

import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.Directory;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.services.ServiceReference;
import org.gradle.api.tasks.*;
import org.gradle.process.ExecOperations;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

/**
 * Gradle Metal linker task.
 */
@CacheableTask
public abstract class MetalLink extends SourceTask
{
    // properties

    /**
     * Linker library path.
     *
     * @return property
     */
    @Input
    public abstract ListProperty<Directory> getLibraryPath ();

    /**
     * Linker library dependencies.
     *
     * @return property
     */
    @IgnoreEmptyDirectories
    @InputFiles
    @PathSensitive(PathSensitivity.ABSOLUTE)
    public abstract ConfigurableFileCollection getLinkDependencies ();

    /**
     * Linker tool.
     *
     * @return property
     */
    @Input
    public abstract Property<String> getLinker ();

    /**
     * Linker options.
     *
     * @return property
     */
    @Input
    public abstract ListProperty<String> getOptions ();

    /**
     * Linker output file.
     *
     * @return property
     */
    @OutputFile
    public abstract RegularFileProperty getOutput ();

    /**
     * Linker target.
     *
     * @return property
     */
    @Input
    public abstract Property<String> getTarget ();

    // services

    /**
     * ExecOperations service.
     *
     * @return service
     */
    @Inject
    protected abstract ExecOperations getExec ();

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
    @Inject
    public MetalLink ()
    {
        getLinker().convention("clang++");
        getTarget().convention(getMetal().map(MetalService::getTarget));
    }

    /**
     * Link action.
     */
    @TaskAction
    public void link ()
    {
        final var metal = getMetal().get();

        final var host = metal.getHost().get();
        final var linker = metal.locateTool(getLinker().get());
        final var options = getOptions().get();
        final var output = getOutput().getAsFile().get();
        final var target = getTarget().map(this::targetMapper).get();

        try
        {
            Files.createDirectories(output.toPath().getParent());
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        final var args = new ArrayList<String>();
        args.add("--target=%s".formatted(target));
        if (! target.contentEquals(host)) args.add("-fuse-ld=lld");
        args.addAll(options);
        getLibraryPath().get().forEach(path -> args.add("--library-directory=%s".formatted(path)));
        args.add("--output=%s".formatted(output));
        getSource().forEach(source -> args.add(source.toString()));
        getLinkDependencies().forEach(linkable -> args.add(linkable.toString()));

        getExec().exec(it -> {
            it.executable(linker);
            it.args(args);
        });
    }

    String targetMapper (String target)
    {
        return switch (target) {
            case "i686-elf" -> "i686-linux-elf";
            default -> target;
        };
    }
}
