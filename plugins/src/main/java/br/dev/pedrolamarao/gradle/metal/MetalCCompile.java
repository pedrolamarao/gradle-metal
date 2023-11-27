package br.dev.pedrolamarao.gradle.metal;

import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Input;

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
}
