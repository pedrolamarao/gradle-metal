// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.cxx;

import org.gradle.api.file.FileCollection;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.TaskAction;
import org.gradle.process.ExecOperations;
import org.gradle.workers.WorkAction;
import org.gradle.workers.WorkParameters;
import org.gradle.workers.WorkerExecutor;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
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
        @Inject
        public abstract ExecOperations getExecOperations ();

        @Override
        public void execute ()
        {
            final var parameters = getParameters();
            final var sourceFile = parameters.getSourceFile().getAsFile().get();
            final var outputFile = parameters.getOutputFile().getAsFile().get();

            // obtain P1689 dependency information from sources
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

            // parse P1689 dependency information
            final var sourceProvides = new ArrayList<String>();
            final var sourceRequires = new ArrayList<String>();
            final IxxDependencies dependencies;
            try
            {
                final var json = (Map<?,?>) new groovy.json.JsonSlurper().parse( buffer.toByteArray() );
                final var rules = (List<?>) json.get("rules");
                for (var ruleObj : rules) {
                    final var rule = (Map<?,?>) ruleObj;
                    final var provides = (List<?>) rule.get("provides");
                    if (provides != null) {
                        for (var provideObj : provides) {
                            final var provide = (Map<?, ?>) provideObj;
                            final var logicalName = provide.get("logical-name");
                            sourceProvides.add(logicalName.toString());
                        }
                    }
                    final var requires = (List<?>) rule.get("requires");
                    if (requires != null) {
                        for (var requireObj : requires) {
                            final var require = (Map<?, ?>) requireObj;
                            final var logicalName = require.get("logical-name");
                            sourceRequires.add(logicalName.toString());
                        }
                    }
                }

                dependencies = new IxxDependencies(sourceFile,sourceProvides,sourceRequires);
            }
            catch (RuntimeException e) { throw e; }
            catch (Exception e) { throw new RuntimeException(e); }

            // serialize IxxDependencies
            try
            {
                Files.createDirectories(outputFile.toPath().getParent());
                try (var outputStream = new ObjectOutputStream(Files.newOutputStream(outputFile.toPath()))) {
                    outputStream.writeObject(dependencies);
                    outputStream.flush();
                }
            }
            catch (RuntimeException e) { throw e; }
            catch (Exception e) { throw new RuntimeException(e); }
        }
    }

    @TaskAction
    public void compile () throws ClassNotFoundException, IOException
    {
        final var baseDirectory = getProject().getProjectDir().toPath();

        // prepare base arguments
        final var baseArgs = new ArrayList<String>();
        baseArgs.add("clang++");
        baseArgs.addAll(getCompileOptions().get());
        getHeaderDependencies().forEach(file -> baseArgs.add("--include-directory=%s".formatted(file)));
        getModuleDependencies().forEach(file -> baseArgs.add("-fmodule-file=%s".formatted(file)));

        // prepare scanner arguments
        final var scanArgs = new ArrayList<>(baseArgs);
        scanArgs.add("--language=c++-module");
        scanArgs.add("--precompile");

        // discover dependencies from sources: assemble dependency files
        final var scanWorkers = workers.noIsolation();
        getProject().delete(getTemporaryDir());
        for (var sourceFile : getSource()) {
            final var outputPath = toOutputPath(baseDirectory, sourceFile.toPath(), getTemporaryDir().toPath(), ".deps");
            scanWorkers.submit(ScanAction.class, parameter -> {
                parameter.getCompileArgs().set(scanArgs);
                parameter.getOutputFile().set(outputPath.toFile());
                parameter.getSourceFile().set(sourceFile);
            });
        }
        scanWorkers.await();

        // discover dependencies from sources: parse dependency files
        final var dependencyGraph = new ArrayList<IxxDependencies>();
        for (var dependencyFile : objects.fileCollection().from(getTemporaryDir()).getAsFileTree()) {
            try (var stream = new ObjectInputStream(Files.newInputStream(dependencyFile.toPath()))) {
                final var dependencies = (IxxDependencies) stream.readObject();
                dependencyGraph.add(dependencies);
            }
        }

        // sort sources in dependency order
        dependencyGraph.sort((x,y) -> {
            for (var requires : y.requires())
                if (x.provides().contains(requires))
                    return -1;
            for (var provides : y.provides())
                if (x.requires().contains(provides))
                    return 1;
            return 0;
        });

        // remove old objects
        final var outputDirectory = getOutputDirectory().get().getAsFile().toPath();
        getProject().delete(outputDirectory);

        // compile objects from sources
        final var outputList = new ArrayList<Path>();
        for (var sourceFile : dependencyGraph.stream().map(IxxDependencies::file).toList())
        {
            final var outputPath = toOutputPath(baseDirectory, sourceFile.toPath(), outputDirectory, ".pcm");
            Files.createDirectories(outputPath.getParent());

            // prepare compiler arguments
            final var compileArgs = new ArrayList<>(baseArgs);
            outputList.forEach(file -> compileArgs.add("-fmodule-file=%s".formatted(file)));
            compileArgs.add("--language=c++-module");
            compileArgs.add("--precompile");
            compileArgs.add("--output=%s".formatted(outputPath));
            compileArgs.add(sourceFile.toString());

            exec.exec(it -> {
                it.commandLine(compileArgs);
            });

            outputList.add(outputPath);
        }
    }

    static Path toOutputPath (Path baseDirectory, Path source, Path outputDirectory, String extension)
    {
        final var relative = baseDirectory.relativize(source);
        final var output = outputDirectory.resolve("%X".formatted(relative.hashCode()));
        return output.resolve(source.getFileName() + extension);
    }
}
