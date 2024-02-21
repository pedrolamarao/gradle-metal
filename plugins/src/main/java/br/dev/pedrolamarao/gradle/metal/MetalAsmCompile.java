// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal;

import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.TaskAction;

/**
 * Gradle Metal assembler "compile" task.
 */
@CacheableTask
public abstract class MetalAsmCompile extends MetalCompile
{
    /**
     * Constructor.
     */
    public MetalAsmCompile ()
    {
        getCompiler().convention("clang");
    }

    @Override
    protected final void addLanguageOptions (ListProperty<String> list)
    {
        list.add("--language=assembler");
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
