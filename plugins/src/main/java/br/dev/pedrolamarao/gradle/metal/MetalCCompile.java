// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal;

import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

@CacheableTask
public abstract class MetalCCompile extends MetalCompile
{
    @Input
    public abstract ListProperty<String> getIncludePath ();

    public MetalCCompile ()
    {
        getCompiler().convention("clang");
    }

    @Override
    protected final void addLanguageOptions (ListProperty<String> args)
    {
        getIncludePath().get().forEach(path -> args.add("--include-directory=%s".formatted(path)));
        args.add("--language=c");
    }

    @TaskAction
    public void compile ()
    {
        super.compile();
    }
}
