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
import java.util.ArrayList;

public abstract class MetalLinkTask extends MetalSourceTask
{
    @InputFiles
    public abstract ConfigurableFileCollection getLinkables ();

    @Input
    public abstract ListProperty<String> getLinkOptions ();

    @OutputFile
    public Provider<RegularFile> getOutput ()
    {
        final var target = getTarget().orElse("default").get();
        final var name = getProject().getName();
        return getOutputDirectory().map(it -> it.file("%s/%s.lib".formatted(target,name)));
    }

    @Internal
    public abstract DirectoryProperty getOutputDirectory ();

    @Inject
    protected abstract ExecOperations getExec ();

    @TaskAction
    public void link ()
    {
        final var output = getOutput().get().getAsFile().toPath();

        final var linkArgs = new ArrayList<String>();
        linkArgs.add("clang");
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
            it.commandLine(linkArgs);
        });
    }
}