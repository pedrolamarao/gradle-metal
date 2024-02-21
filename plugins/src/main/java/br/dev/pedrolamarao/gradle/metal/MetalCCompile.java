// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal;

import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

/**
 * Gradle Metal C compile task.
 */
@CacheableTask
public abstract class MetalCCompile extends MetalCompile
{
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
    public MetalCCompile ()
    {
        getCompiler().convention("clang");
    }

    @Override
    protected final void addLanguageOptions (ListProperty<String> list)
    {
        getIncludePath().get().forEach(path -> list.add("--include-directory=%s".formatted(path)));
        list.add("--language=c");
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
