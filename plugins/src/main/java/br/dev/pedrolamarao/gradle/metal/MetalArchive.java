package br.dev.pedrolamarao.gradle.metal;

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
public abstract class MetalArchive extends SourceTask
{
    @Input
    public abstract Property<String> getArchiver ();

    @Input
    public abstract ListProperty<String> getOptions ();

    @OutputFile
    public abstract RegularFileProperty getOutput ();

    @Input
    public abstract Property<String> getTarget ();

    @Inject
    protected abstract ExecOperations getExec ();

    @ServiceReference
    protected abstract Property<MetalService> getMetal ();

    @Inject
    public MetalArchive ()
    {
        getArchiver().convention("llvm-ar");
        getTarget().convention(getMetal().map(MetalService::getTarget));
    }

    @TaskAction
    public void archive ()
    {
        final var archiver = getMetal().get().locateTool(getArchiver().get());
        final var options = getOptions().get();
        final var output = getOutput().getAsFile().get();

        try
        {
            Files.createDirectories(output.toPath().getParent());
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        final var args = new ArrayList<String>();
        args.add("rcs");
        args.addAll(options);
        args.add(output.toString());
        getSource().forEach(source -> args.add(source.toString()));

        getExec().exec(it -> {
            it.executable(archiver);
            it.args(args);
        });
    }
}
