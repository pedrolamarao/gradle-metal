package br.dev.pedrolamarao.gradle.metal;

import org.gradle.api.file.Directory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Input;

import java.util.List;

@CacheableTask
public abstract class MetalCxxCompile extends MetalCompile
{
    @Input
    public abstract ListProperty<Directory> getImportPath ();

    @Input
    public abstract ListProperty<Directory> getIncludePath ();

    public MetalCxxCompile ()
    {
        getCompiler().convention("clang++");
    }

    protected final void getLanguageArguments (List<String> args)
    {
        getImportPath().get().forEach(path -> args.add("-fprebuilt-module-path=%s".formatted(path)));
        getIncludePath().get().forEach(path -> args.add("--include-directory=%s".formatted(path)));
        args.add("--language=c++");
    }
}
