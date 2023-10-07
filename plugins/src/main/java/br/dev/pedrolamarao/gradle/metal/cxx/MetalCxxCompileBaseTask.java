// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.cxx;

import br.dev.pedrolamarao.gradle.metal.base.MetalCompileTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Compile base task.
 */
public abstract class MetalCxxCompileBaseTask extends MetalCompileTask
{
    /**
     * Compile executable file.
     *
     * @return provider
     */
    @Input
    public Provider<File> getCompiler ()
    {
        return getMetal().flatMap(it -> it.locateTool("clang++"));
    }

    /**
     * Include path.
     *
     * @return collection
     */
    @InputFiles
    public abstract ConfigurableFileCollection getIncludables ();

    /**
     * Import path.
     *
     * @return collection
     */
    @InputFiles
    public abstract ConfigurableFileCollection getImportables ();

    /**
     * Generate compiler arguments.
     *
     * @param formatter file formatter
     * @return arguments
     */
    protected List<String> toCompileArguments (Function<File,String> formatter)
    {
        final var arguments = new ArrayList<String>();
        arguments.add("--target=%s".formatted(getTarget().get()));
        arguments.addAll(getCompileOptions().get());
        getImportables().forEach(file -> arguments.add("-fprebuilt-module-path=%s".formatted(formatter.apply(file))));
        getIncludables().forEach(file -> arguments.add("--include-directory=%s".formatted(formatter.apply(file))));
        arguments.add("--compile");
        return arguments;
    }
}
