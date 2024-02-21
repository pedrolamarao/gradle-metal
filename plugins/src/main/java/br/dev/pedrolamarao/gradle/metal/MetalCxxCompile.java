// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal;

import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

import java.util.ArrayList;
import java.util.List;

/**
 * Gradle Metal C++ compile task.
 */
@CacheableTask
public abstract class MetalCxxCompile extends MetalCompileImpl
{
    /**
     * Compiler import path.
     *
     * @return property
     */
    @Input
    public abstract ListProperty<String> getImportPath ();

    /**
     * Compiler include path.
     *
     * @return property
     */
    @Input
    public abstract ListProperty<String> getIncludePath ();

    /**
     * Constructor.
     */
    public MetalCxxCompile ()
    {
        getCompiler().convention("clang++");
    }

    @Override
    Provider<List<String>> getCommand ()
    {
        return getProviders().provider(() ->
        {
            final var list = new ArrayList<String>();
            list.add(getMetal().get().locateTool(getCompiler().get()).toString());
            list.add("--target=%s".formatted(getTarget().get()));
            list.addAll(getOptions().get());
            getImportPath().get().forEach(path -> list.add("-fprebuilt-module-path=%s".formatted(path)));
            getIncludePath().get().forEach(path -> list.add("--include-directory=%s".formatted(path)));
            list.add("--compile");
            return list;
        });
    }

    /**
     * Compile action.
     */
    @TaskAction
    public void compile ()
    {
        super.compile();
    }
}
