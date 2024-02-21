// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal;

import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.services.ServiceReference;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.SourceTask;
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


abstract class MetalCompileImpl extends SourceTask implements MetalCompile
{
    @ServiceReference
    protected abstract Property<MetalService> getMetal ();

    @Inject
    protected abstract ProviderFactory getProviders ();

    @Inject
    protected abstract WorkerExecutor getWorkers ();

    @Internal
    abstract Provider<List<String>> getCommand ();

    interface CompileParameter extends WorkParameters
    {
        ListProperty<String> getCommand ();

        DirectoryProperty getOutputDirectory ();

        RegularFileProperty getSource ();
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

            final var commandBase = parameters.getCommand().get();
            final var source = parameters.getSource().getAsFile().get();
            final var output = parameters.getOutputDirectory()
                .file("%X/%s.%s".formatted(hash(source),source.getName(),"o"))
                .get().getAsFile();

            try
            {
                Files.createDirectories(output.toPath().getParent());
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }

            final var command = new ArrayList<>(commandBase);
            command.add("--output=%s".formatted(output));
            command.add(source.toString());

            getExec().exec(it -> it.commandLine(command));
        }
    }

    void compile ()
    {
        final var workers = getWorkers().noIsolation();

        final var options = getCommand();
        final var outputDirectory = getOutputDirectory();

        getSource().forEach(source ->
        {
            workers.submit(CompileAction.class,parameters ->
            {
                parameters.getCommand().set(options);
                parameters.getOutputDirectory().set(outputDirectory);
                parameters.getSource().set(source);
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
