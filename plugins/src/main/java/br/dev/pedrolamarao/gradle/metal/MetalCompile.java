package br.dev.pedrolamarao.gradle.metal;

import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.services.ServiceReference;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.SourceTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.process.ExecOperations;
import org.gradle.workers.WorkAction;
import org.gradle.workers.WorkParameters;
import org.gradle.workers.WorkerExecutor;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public abstract class MetalCompile extends SourceTask
{
    @Input
    public abstract Property<String> getCompiler ();

    @Input
    public abstract ListProperty<String> getOptions ();

    @OutputDirectory
    public abstract DirectoryProperty getOutputDirectory ();

    @Input
    public abstract Property<String> getTarget ();

    @ServiceReference
    protected abstract Property<MetalService> getMetal ();

    @Inject
    protected abstract WorkerExecutor getWorkers ();

    public MetalCompile ()
    {
        getTarget().convention(getMetal().map(MetalService::getTarget));
    }

    protected void getLanguageArguments (List<String> args) { }

    interface CompileParameter extends WorkParameters
    {
        Property<String> getCompiler ();

        ListProperty<String> getOptions ();

        DirectoryProperty getOutputDirectory ();

        RegularFileProperty getSource ();

        Property<String> getTarget ();
    }

    static abstract class CompileAction implements WorkAction<CompileParameter>
    {
        @Inject
        protected abstract ExecOperations getExec ();

        @Inject
        public CompileAction () { }

        @Override
        public void execute ()
        {
            final var parameters = getParameters();

            final var compiler = parameters.getCompiler().get();
            final var options = parameters.getOptions().get();
            final var source = parameters.getSource().getAsFile().get();
            final var target = parameters.getTarget().get();

            final var output = parameters.getOutputDirectory()
                .file("%s/%s.%s".formatted(hash(source),source.getName(),"o"))
                .get().getAsFile();

            try
            {
                Files.createDirectories(output.toPath().getParent());
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }

            final var args = new ArrayList<String>();
            args.add("--target=%s".formatted(target));
            args.addAll(options);
            args.add("--compile");
            args.add("--output=%s".formatted(output));
            args.add(source.toString());

            getExec().exec(it -> {
                it.executable(compiler);
                it.args(args);
            });
        }
    }

    @TaskAction
    public void compile ()
    {
        final var workers = getWorkers().noIsolation();

        final var compiler = getMetal().get().locateTool(getCompiler().get());
        final var options = getOptions();
        final var output = getOutputDirectory().dir(getTarget().get());
        final var target = getTarget();

        getSource().forEach(source ->
        {
            workers.submit(CompileAction.class, parameters ->
            {
                parameters.getCompiler().set(compiler.toString());
                parameters.getOutputDirectory().set(output);
                parameters.getOptions().set(options);
                parameters.getSource().set(source);
                parameters.getTarget().set(target);
            });
        });
    }

    // see: https://en.wikipedia.org/wiki/Fowler–Noll–Vo_hash_function

    static final int FNV_OFFSET_32 = 0x811c9dc5;

    static final int FNV_PRIME_32 = 0x01000193;

    static int hash (byte[] bytes)
    {
        int hash = FNV_OFFSET_32;
        for (byte b : bytes) {
            hash = hash * FNV_PRIME_32;
            hash = hash ^ b;
        }
        return hash;
    }

    static int hash (String string)
    {
        return hash(string.getBytes());
    }

    static int hash (File file)
    {
        return hash(file.toString().getBytes());
    }

    static int hash (Path path)
    {
        return hash(path.toString().getBytes());
    }
}
