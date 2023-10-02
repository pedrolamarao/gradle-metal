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

/**
 * Assembler compile base task.
 */
public abstract class MetalAsmCompileBaseTask extends MetalCompileTask
{
    /**
     * Compiler executable path.
     *
     * @return provider
     */
    @Input
    public Provider<File> getCompiler ()
    {
        return getProviders().gradleProperty("metal.path")
            .orElse(getProviders().environmentVariable("PATH"))
            .map(it -> Metal.toExecutableFile(it,"clang"));
    }

    /**
     * Include path.
     *
     * @return collection
     */
    @InputFiles
    public abstract ConfigurableFileCollection getIncludables ();

    /**
     * Generate compiler arguments
     * .
     * @param formatter  file path formatter
     * @return arguments
     */
    protected List<String> toCompileArguments (Function<File,String> formatter)
    {
        final var arguments = new ArrayList<String>();
        if (getTarget().isPresent()) arguments.add("--target=%s".formatted(getTarget().get()));
        arguments.addAll(getCompileOptions().get());
        getIncludables().forEach(file -> arguments.add("--include-directory=%s".formatted(formatter.apply(file))));
        arguments.add("--compile");
        arguments.add("--language=assembler");
        return arguments;
    }
}
