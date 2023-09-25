package br.dev.pedrolamarao.gradle.metal.cxx;

import br.dev.pedrolamarao.gradle.metal.base.MetalExtension;
import br.dev.pedrolamarao.gradle.metal.base.NativeBasePlugin;
import br.dev.pedrolamarao.gradle.metal.base.NativeCapability;
import br.dev.pedrolamarao.gradle.metal.cpp.MetalCppPlugin;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.util.function.Function;

import static br.dev.pedrolamarao.gradle.metal.cpp.MetalCppPlugin.CPP_INCLUDABLES;
import static br.dev.pedrolamarao.gradle.metal.cpp.MetalCppPlugin.CPP_INCLUDABLE_DEPENDENCIES;

public class MetalCxxPlugin implements Plugin<Project>
{
    public static final Function<String,String> CXX_IMPORTABLES = name -> "%s-importables".formatted(name);

    public static final String CXX_IMPORT_ELEMENTS = "cxxImportElements";

    public static final String CXX_IMPORT_DEPENDENCIES = "cxxImportDependencies";

    @Override
    public void apply (Project project)
    {
        project.getPluginManager().apply(NativeBasePlugin.class);
        project.getPluginManager().apply(MetalCppPlugin.class);

        final var metal = project.getExtensions().getByType(MetalExtension.class);

        final var cxx = project.getObjects().domainObjectContainer(MetalCxxSources.class, name -> createCxxSources(project,name));
        metal.getExtensions().create("cxx", MetalCxxExtension.class, cxx);

        final var ixx = project.getObjects().domainObjectContainer(MetalIxxSources.class, name -> createIxxSources(project,name));
        metal.getExtensions().create("ixx", MetalIxxExtension.class, ixx);

        final var nativeImplementation = project.getConfigurations().named("nativeImplementation");

        project.getConfigurations().create(CXX_IMPORT_DEPENDENCIES, configuration -> {
            configuration.setCanBeConsumed(false);
            configuration.setCanBeResolved(true);
            configuration.attributes(it -> it.attribute(NativeCapability.ATTRIBUTE,NativeCapability.IMPORTABLE));
            configuration.extendsFrom(nativeImplementation.get());
        });

        project.getConfigurations().create(CXX_IMPORT_ELEMENTS, configuration -> {
            configuration.setCanBeConsumed(true);
            configuration.setCanBeResolved(false);
            configuration.attributes(it -> it.attribute(NativeCapability.ATTRIBUTE,NativeCapability.IMPORTABLE));
        });
    }

    static MetalCxxSources createCxxSources (Project project, String name)
    {
        final var configurations = project.getConfigurations();
        final var layout = project.getLayout();
        final var objects = project.getObjects();
        final var providers = project.getProviders();
        final var tasks = project.getTasks();

        final var compileOptions = objects.listProperty(String.class);
        final var headers = objects.fileCollection();
        headers.from( configurations.named(CPP_INCLUDABLE_DEPENDENCIES) );
        headers.from( providers.provider(() -> configurations.maybeCreate(CPP_INCLUDABLES.apply(name)).getArtifacts().getFiles() ) );
        final var modules = objects.fileCollection();
        modules.from( configurations.named(CXX_IMPORT_DEPENDENCIES) );
        modules.from( providers.provider(() -> configurations.maybeCreate(CXX_IMPORTABLES.apply(name)).getArtifacts().getFiles() ) );
        final var sources = objects.sourceDirectorySet(name,name);
        sources.srcDir(layout.getProjectDirectory().dir("src/%s/cxx".formatted(name)));
        final var objectDirectory = layout.getBuildDirectory().dir("obj/%s/cxx".formatted(name));

        final var compileTask = tasks.register("compile-%s-cxx".formatted(name), CxxCompileTask.class, task ->
        {
            task.getCompileOptions().set(compileOptions);
            task.getHeaderDependencies().from(headers);
            task.getModuleDependencies().from(modules);
            task.getOutputDirectory().set(objectDirectory);
            task.setSource(sources);
        });

        return new MetalCxxSources(compileOptions, headers, modules, name, sources);
    }

    static MetalIxxSources createIxxSources (Project project, String name)
    {
        final var configurations = project.getConfigurations();
        final var layout = project.getLayout();
        final var objects = project.getObjects();
        final var providers = project.getProviders();
        final var tasks = project.getTasks();

        final var compileOptions = objects.listProperty(String.class);
        final var headers = objects.fileCollection();
        headers.from( configurations.named(CPP_INCLUDABLE_DEPENDENCIES) );
        headers.from( providers.provider(() -> configurations.maybeCreate(CPP_INCLUDABLES.apply(name)).getArtifacts().getFiles() ) );
        final var modules = objects.fileCollection();
        modules.from( configurations.named(CXX_IMPORT_DEPENDENCIES) );
        final var sourceDirectorySet = objects.sourceDirectorySet(name,name);
        sourceDirectorySet.srcDir(layout.getProjectDirectory().dir("src/%s/ixx".formatted(name)));
        final var objectDirectory = layout.getBuildDirectory().dir("bmi/%s/ixx".formatted(name));

        final var compileTask = tasks.register("compile-%s-ixx".formatted(name), IxxCompileTask.class, task ->
        {
            task.getCompileOptions().set(compileOptions);
            task.getHeaderDependencies().from(headers);
            task.getModuleDependencies().from(modules);
            task.getOutputDirectory().set(objectDirectory);
            task.setSource(sourceDirectorySet);
        });

        configurations.named(CXX_IMPORT_ELEMENTS).configure(configuration -> {
            configuration.getOutgoing().artifacts(compileTask.map(IxxCompileTask::getInterfaceFiles), artifact -> {
                artifact.builtBy(compileTask);
            });
        });

        final var importables = configurations.create(CXX_IMPORTABLES.apply(name),configuration ->
        {
            configuration.setCanBeConsumed(true);
            configuration.setCanBeResolved(false);
            configuration.setVisible(false);
            configuration.getOutgoing().artifacts(compileTask.map(IxxCompileTask::getInterfaceFiles),it -> it.builtBy(compileTask));
        });

        return new MetalIxxSources(compileOptions, name, sourceDirectorySet);
    }
}
