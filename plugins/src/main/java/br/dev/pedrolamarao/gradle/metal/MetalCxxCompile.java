package br.dev.pedrolamarao.gradle.metal;

import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Input;

@CacheableTask
public abstract class MetalCxxCompile extends MetalCompile
{
    @Input
    public abstract ListProperty<String> getImportPath ();

    @Input
    public abstract ListProperty<String> getIncludePath ();

    public MetalCxxCompile ()
    {
        getCompiler().convention("clang++");
    }

    @Override
    protected final void addLanguageOptions (ListProperty<String> args)
    {
        getImportPath().get().forEach(path -> args.add("-fprebuilt-module-path=%s".formatted(path)));
        getIncludePath().get().forEach(path -> args.add("--include-directory=%s".formatted(path)));
        args.add("--language=c++");
    }
}
