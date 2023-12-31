// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.c;

import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.process.ExecOperations;
import org.gradle.workers.WorkAction;
import org.gradle.workers.WorkParameters;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

/**
 * Compile C sources task.
 */
@CacheableTask
public abstract class MetalCCompileTask extends MetalCCompileBaseTask
{
    /**
     * Compile worker parameters.
     */
    public interface CompileParameters extends WorkParameters
    {
        /**
         * Sources base directory.
         *
         * @return property
         */
        DirectoryProperty getBaseDirectory ();

        /**
         * Compiler executable.
         *
         * @return property
         */
        Property<File> getCompiler ();

        /**
         * Compiler arguments.
         *
         * @return property
         */
        ListProperty<String> getCompileArgs ();

        /**
         * Outputs base directory.
         *
         * @return property
         */
        DirectoryProperty getOutputDirectory ();

        /**
         * Source files.
         *
         * @return property
         */
        RegularFileProperty getSourceFile ();
    }

    /**
     * Compile worker action.
     */
    public static abstract class CompileAction implements WorkAction<CompileParameters>
    {
        /**
         * Exec operations service.
         *
         * @return service
         */
        @Inject
        public abstract ExecOperations getExec ();

        /**
         * {@inheritDoc}
         */
        @Override
        public void execute ()
        {
            final var parameters = getParameters();

            final var basePath = parameters.getBaseDirectory().get().getAsFile().toPath();
            final var objectPath = parameters.getOutputDirectory().get().getAsFile().toPath();

            final var sourcePath = parameters.getSourceFile().get().getAsFile().toPath();
            final var outputPath = toOutputPath(basePath, sourcePath, objectPath, ".o");

            // prepare compile arguments
            final var compileArgs = new ArrayList<>(parameters.getCompileArgs().get());
            compileArgs.add("--output=%s".formatted(outputPath));
            compileArgs.add(sourcePath.toString());

            try
            {
                Files.createDirectories(outputPath.getParent());
                getExec().exec(it -> {
                    it.executable(parameters.getCompiler().get());
                    it.args(compileArgs);
                });
            }
            catch (IOException e) { throw new RuntimeException(e); }
        }
    }

    /**
     * Compile sources.
     */
    @TaskAction
    public void compile ()
    {
        final var baseDirectory = getBaseDirectory().get();
        final var outputDirectory = getTargetDirectory();
        final var workers = getWorkers().noIsolation();

        // prepare compile args
        final var compileArgs = toCompileArguments(File::toString);

        // remove old objects
        getFiles().delete(getOutputDirectory());

        // compile objects from sources
        getSource().forEach(source ->
        {
            workers.submit(CompileAction.class, parameters ->
            {
                parameters.getBaseDirectory().set(baseDirectory);
                parameters.getCompiler().set(getCompiler());
                parameters.getCompileArgs().set(compileArgs);
                parameters.getOutputDirectory().set(outputDirectory);
                parameters.getSourceFile().set(source);
            });
        });
    }
}
