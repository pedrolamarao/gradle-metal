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

@CacheableTask
public abstract class MetalLink extends SourceTask
{
    // properties

    @Input
    public abstract ListProperty<Directory> getLibraryPath ();

    @IgnoreEmptyDirectories
    @InputFiles
    @PathSensitive(PathSensitivity.ABSOLUTE)
    protected abstract ConfigurableFileCollection getLinkableDependencies ();

    @Input
    public abstract Property<String> getLinker ();

    @Input
    public abstract ListProperty<String> getOptions ();

    @OutputFile
    public abstract RegularFileProperty getOutput ();

    @Input
    public abstract Property<String> getTarget ();

    // services

    @Inject
    protected abstract ExecOperations getExec ();

    @ServiceReference
    protected abstract Property<MetalService> getMetal ();

    // task

    @Inject
    public MetalLink ()
    {
        getLinker().convention("clang++");
        getTarget().convention(getMetal().map(MetalService::getTarget));
    }

    @TaskAction
    public void link ()
    {
        final var linker = getMetal().get().locateTool(getLinker().get());
        final var options = getOptions().get();
        final var output = getOutput().getAsFile().get();
        final var target = getTarget().get();

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
        args.addAll(options);
        getLibraryPath().get().forEach(path -> args.add("--library-directory=%s".formatted(path)));
        args.add("--output=%s".formatted(output));
        getSource().forEach(source -> args.add(source.toString()));
        getLinkableDependencies().forEach(linkable -> args.add(linkable.toString()));

        getExec().exec(it -> {
            it.executable(linker);
            it.args(args);
        });
    }
}
