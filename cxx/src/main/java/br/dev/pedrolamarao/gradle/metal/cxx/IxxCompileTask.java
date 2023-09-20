// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.cxx;

import org.gradle.api.file.FileCollection;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.*;
import org.gradle.process.ExecOperations;
import org.gradle.workers.WorkAction;
import org.gradle.workers.WorkParameters;
import org.gradle.workers.WorkerExecutor;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class IxxCompileTask extends CxxCompileBaseTask
{
    final ExecOperations exec;

    final ObjectFactory objects;

    final WorkerExecutor workers;

    @Internal @Nonnull
    public FileCollection getInterfaceFiles ()
    {
        final var collection = objects.fileCollection();
        final var baseDirectory = getProject().getProjectDir().toPath();
        final var outputDirectory = getOutputDirectory().get().getAsFile().toPath();
        getSource().forEach(source -> collection.from( toOutputPath(baseDirectory,source.toPath(),outputDirectory,".pcm") ));
        return collection;
    }

    @Inject
    public IxxCompileTask (ExecOperations exec, ObjectFactory objects, WorkerExecutor workers)
    {
        this.exec = exec;
        this.objects = objects;
        this.workers = workers;
    }

    public interface ScanParameter extends WorkParameters
    {
        ListProperty<String> getCompileArgs ();

        RegularFileProperty getOutputFile ();

        RegularFileProperty getSourceFile ();
    }

    public static abstract class ScanAction implements WorkAction<ScanParameter>
    {
        static final Logger logger = Logging.getLogger(ScanAction.class);

        @Inject
        public abstract ExecOperations getExecOperations ();

        @Override
        public void execute ()
        {
            final var parameters = getParameters();
            final var sourceFile = parameters.getSourceFile().getAsFile().get();

            final var buffer = new ByteArrayOutputStream();

            try
            {
                final var scanArgs = new ArrayList<String>();
                scanArgs.add("clang-scan-deps");
                scanArgs.add("--format=p1689");
                scanArgs.add("--");
                scanArgs.addAll(parameters.getCompileArgs().get());
                scanArgs.add(sourceFile.toString());

                getExecOperations().exec(it -> {
                    it.setCommandLine(scanArgs);
                    it.setStandardOutput(buffer);
                });
            }
            catch (RuntimeException e) { throw e; }
            catch (Exception e) { throw new RuntimeException(e); }

            final var sourceProvides = new ArrayList<String>();
            final var sourceRequires = new ArrayList<String>();

            try
            {
                final var json = (Map<?,?>) new groovy.json.JsonSlurper().parse( buffer.toByteArray() );
                final var rules = (List<?>) json.get("rules");
                for (var ruleObj : rules) {
                    final var rule = (Map<?,?>) ruleObj;
                    final var provides = (List<?>) rule.get("provides");
                    for (var provideObj : provides) {
                        final var provide = (Map<?,?>) provideObj;
                        final var logicalName = provide.get("logical-name");
                        sourceProvides.add(logicalName.toString());
                    }
                    final var requires = (List<?>) rule.get("requires");
                    if (requires == null) continue;
                    for (var requireObj : requires) {
                        final var require = (Map<?,?>) requireObj;
                        final var logicalName = require.get("logical-name");
                        sourceRequires.add(logicalName.toString());
                    }
                }
            }
            catch (RuntimeException e) { throw e; }
            catch (Exception e) { throw new RuntimeException(e); }

            logger.info("dependencies: {}: provides: {}",sourceFile,sourceProvides);
            logger.info("dependencies: {}: requires: {}",sourceFile,sourceRequires);
        }
    }

    @TaskAction
    public void compile () throws IOException
    {
        final var baseDirectory = getProject().getProjectDir().toPath();

        final var compileArgs = new ArrayList<String>();
        compileArgs.add("clang++");
        getHeaderDependencies().forEach(file -> compileArgs.add("--include-directory=%s".formatted(file)));
        getModuleDependencies().forEach(file -> compileArgs.add("-fmodule-file=%s".formatted(file)));
        compileArgs.addAll(getCompileOptions().get());
        compileArgs.add("--language=c++-module");
        compileArgs.add("--precompile");

        final var scanWorkers = workers.noIsolation();
        for (var sourceFile : getSource()) {
            final var outputPath = toOutputPath(baseDirectory, sourceFile.toPath(), getTemporaryDir().toPath(), ".deps");
            scanWorkers.submit(ScanAction.class, parameter ->
            {
                parameter.getCompileArgs().set(compileArgs);
                parameter.getOutputFile().set(outputPath.toFile());
                parameter.getSourceFile().set(sourceFile);
            });
        }
        scanWorkers.await();

        final var outputDirectory = getOutputDirectory().get().getAsFile().toPath();

        // #TODO: sort sources in dependency order
        if (getSource().getFiles().size() > 1) {
            getLogger().warn("Task currently compiles module interface files out of order");
        }

        for (var sourceFile : getSource())
        {
            final var output = toOutputPath(baseDirectory, sourceFile.toPath(), outputDirectory, ".pcm");
            Files.createDirectories(output.getParent());

            final var command = new ArrayList<>(compileArgs);
            command.add("--output=%s".formatted(output));
            command.add(sourceFile.toString());

            exec.exec(it -> {
                it.commandLine(command);
            });
        }
    }

    static Path toOutputPath (Path baseDirectory, Path source, Path outputDirectory, String extension)
    {
        final var relative = baseDirectory.relativize(source);
        final var output = outputDirectory.resolve("%X".formatted(relative.hashCode()));
        return output.resolve(source.getFileName() + extension);
    }
}
