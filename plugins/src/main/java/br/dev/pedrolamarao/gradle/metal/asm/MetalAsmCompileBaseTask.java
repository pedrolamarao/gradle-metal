package br.dev.pedrolamarao.gradle.metal.asm;

import br.dev.pedrolamarao.gradle.metal.base.Metal;
import br.dev.pedrolamarao.gradle.metal.base.MetalCompileTask;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public abstract class MetalAsmCompileBaseTask extends MetalCompileTask
{
    @Input
    public Provider<String> getCompiler ()
    {
        return getProviders().gradleProperty("metal.path")
            .orElse(getProviders().environmentVariable("PATH"))
            .map(it -> Metal.toExecutablePath(it,"clang"));
    }

    @InputFiles
    public abstract ConfigurableFileCollection getIncludables ();

    protected List<String> toCompileArguments (Function<File,String> formatter)
    {
        final var arguments = new ArrayList<String>();
        arguments.add(getCompiler().get());
        if (getTarget().isPresent()) arguments.add("--target=%s".formatted(getTarget().get()));
        arguments.addAll(getCompileOptions().get());
        getIncludables().forEach(file -> arguments.add("--include-directory=%s".formatted(formatter.apply(file))));
        arguments.add("--compile");
        arguments.add("--language=assembler");
        return arguments;
    }
}
