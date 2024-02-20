// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal;

import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

/**
 * Gradle Metal C++ compile task.
 */
@CacheableTask
public abstract class MetalCxxCompile extends MetalCompile
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
    protected final void addLanguageOptions (ListProperty<String> list)
    {
        getImportPath().get().forEach(path -> list.add("-fprebuilt-module-path=%s".formatted(path)));
        getIncludePath().get().forEach(path -> list.add("--include-directory=%s".formatted(path)));
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
