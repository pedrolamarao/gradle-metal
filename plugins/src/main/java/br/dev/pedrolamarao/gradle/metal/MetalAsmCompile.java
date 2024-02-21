// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal;

import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.TaskAction;

import java.util.ArrayList;
import java.util.List;

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
    Provider<List<String>> getCommand ()
    {
        return getProviders().provider(() ->
        {
            final var list = new ArrayList<String>();
            list.add(getMetal().get().locateTool(getCompiler().get()).toString());
            list.add("--target=%s".formatted(getTarget().get()));
            list.addAll(getOptions().get());
            list.add("--compile");
            list.add("--language=assembler");
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
