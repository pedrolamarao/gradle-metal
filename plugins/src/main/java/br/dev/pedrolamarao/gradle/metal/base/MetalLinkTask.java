// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.base;

import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFile;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.*;
import org.gradle.process.ExecOperations;

import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;

/**
 * Link native objects.
 */
public abstract class MetalLinkTask extends MetalSourceTask
{
    /**
     * Additional linkable sources. (Probably obsolete.)
     *
     * @return collection
     */
    @InputFiles
    public abstract ConfigurableFileCollection getLinkables ();

    /**
     * Linker executable path.
     *
     * @return provider
     */
    @Input
    public Provider<File> getLinker ()
    {
        return getProviders().gradleProperty("metal.path")
            .orElse(getProviders().environmentVariable("PATH"))
            .map(it -> Metal.toExecutableFile(it,"clang"));
    }

    /**
     * Link options.
     *
     * @return property
     */
    @Input
    public abstract ListProperty<String> getLinkOptions ();

    /**
     * Output file.
     *
     * @return provider
     */
    @OutputFile
    public Provider<RegularFile> getOutput ()
    {
        final var target = getTarget().orElse("default").get();
        final var name = getProject().getName();
        return getOutputDirectory().map(it -> it.file("%s/%s.exe".formatted(target,name)));
    }

    /**
     * Output base directory.
     *
     * @return property
     */
    @Internal
    public abstract DirectoryProperty getOutputDirectory ();

    /**
     * Exec operations service.
     *
     * @return service
     */
    @Inject
    protected abstract ExecOperations getExec ();

    /**
     * Link objects.
     */
    @TaskAction
    public void link ()
    {
        final var output = getOutput().get().getAsFile().toPath();

        final var linkArgs = new ArrayList<String>();
        if (getTarget().isPresent()) {
            var linkTarget = getTarget().get();
            // workaround to clang incorrectly attempting to link with gcc
            linkTarget = switch (linkTarget) {
                case "i686-elf" -> "i686-linux-elf";
                case "x86_64-elf" -> "x86_64-linux-elf";
                default -> linkTarget;
            };
            linkArgs.add("--target=%s".formatted(linkTarget));
            linkArgs.add("-fuse-ld=lld");
        }
        linkArgs.addAll(getLinkOptions().get());
        linkArgs.add("--output=%s".formatted(output));
        getLinkables().forEach(file -> linkArgs.add(file.toString()));
        getSource().forEach(file -> linkArgs.add(file.toString()));

        getExec().exec(it ->
        {
            it.executable(getLinker().get());
            it.args(linkArgs);
        });
    }
}
