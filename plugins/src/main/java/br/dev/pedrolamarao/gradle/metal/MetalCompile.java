// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal;

import org.gradle.api.file.Directory;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.internal.file.FileOperations;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.services.ServiceReference;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.OutputDirectory;
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


/**
 * Gradle Metal compile task.
 */
public abstract class MetalCompile extends SourceTask
{
    // properties

    /**
     * Compiler tool.
     *
     * @return property
     */
    @Input
    public abstract Property<String> getCompiler ();

    /**
     * Compiler options.
     *
     * @return property
     */
    @Input
    public abstract ListProperty<String> getOptions ();

    /**
     * Compiler output directory.
     *
     * @return property
     */
    @Internal
    public abstract DirectoryProperty getOutputDirectory ();

    /**
     * Compiler target.
     *
     * @return property
     */
    @Input
    public abstract Property<String> getTarget ();

    /**
     * Compiler target directory.
     *
     * @return provider
     */
    @OutputDirectory
    public Provider<Directory> getTargetOutputDirectory ()
    {
        return getOutputDirectory().zip(getTarget(),Directory::dir);
    }

    // services

    /**
     * FileOperations service.
     *
     * @return service
     */
    @Inject
    protected abstract FileOperations getFiles ();

    /**
     * ProjectLayout service.
     *
     * @return service
     */
    @Inject
    protected abstract ProjectLayout getLayout ();

    /**
     * Gradle Metal service.
     *
     * @return service
     */
    @ServiceReference
    protected abstract Property<MetalService> getMetal ();

    /**
     * ObjectFactory service.
     *
     * @return service
     */
    @Inject
    protected abstract ObjectFactory getObjects ();

    /**
     * ProviderFactory service.
     *
     * @return service
     */
    @Inject
    protected abstract ProviderFactory getProviders ();

    /**
     * WorkerExecutor service.
     *
     * @return service
     */
    @Inject
    protected abstract WorkerExecutor getWorkers ();

    // task

    /**
     * Constructor.
     */
    public MetalCompile ()
    {
        getTarget().convention(getMetal().map(MetalService::getTarget));
    }

    /**
     * Add this task's language options to the specified list.
     *
     * @param list list to receive this task's language options
     */
    protected void addLanguageOptions (ListProperty<String> list) { };

    @Internal
    Provider<List<String>> getInternalOptions ()
    {
        return getProviders().provider(() ->
        {
            final var list = new ArrayList<String>();
            list.add("--target=%s".formatted(getTarget().get()));
            list.addAll(getOptions().get());
            list.add("--compile");
            return list;
        });
    }

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

    /**
     * Compile action.
     */
    public void compile ()
    {
        final var workers = getWorkers().noIsolation();

        final var compiler = getMetal().get().locateTool(getCompiler().get());
        final var options = getOptions();
        final var target = getTarget().get();

        final var outputDirectory = getTargetOutputDirectory().get();

        getSource().forEach(source ->
        {
            workers.submit(CompileAction.class, parameters ->
            {
                parameters.getCompiler().set(compiler.toString());
                parameters.getOutputDirectory().set(outputDirectory);
                parameters.getOptions().set(options);
                addLanguageOptions(parameters.getOptions());
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
