package br.dev.pedrolamarao.gradle.metal;

import org.gradle.api.tasks.CacheableTask;

import java.util.List;

@CacheableTask
public abstract class MetalAsmCompile extends MetalCompile
{
    public MetalAsmCompile ()
    {
        getCompiler().convention("clang");
    }

    protected final void getLanguageArguments (List<String> args)
    {
        args.add("--language=assembler");
    }
}
