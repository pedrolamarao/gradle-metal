package br.dev.pedrolamarao.gradle.metal;

import org.gradle.api.file.Directory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Input;

import java.util.List;

@CacheableTask
public abstract class MetalCCompile extends MetalCompile
{
    @Input
    public abstract ListProperty<Directory> getIncludePath ();

    public MetalCCompile ()
    {
        getCompiler().convention("clang");
    }

    protected final void getLanguageArguments (List<String> args)
    {
        getIncludePath().get().forEach(path -> args.add("--include-directory=%s".formatted(path)));
        args.add("--language=c++");
    }
}
