package br.dev.pedrolamarao.gradle.metal.base;

import br.dev.pedrolamarao.gradle.metal.asm.MetalAsmSources;
import br.dev.pedrolamarao.gradle.metal.c.MetalCSources;
import br.dev.pedrolamarao.gradle.metal.cpp.MetalCppSources;
import br.dev.pedrolamarao.gradle.metal.cxx.MetalCxxSources;
import br.dev.pedrolamarao.gradle.metal.ixx.MetalIxxSources;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Project;

/**
 * Component project plugin.
 */
public abstract class MetalComponentPlugin
{
    /**
     * Configure component.
     *
     * @param project    project
     * @param component  component
     */
    protected void configure (Project project, MetalComponent component)
    {
        final var plugins = project.getPluginManager();

        final var name = component.getName();

        project.getLogger().info("gradle-metal: creating main component: {}",component);

        // if cpp plugin then create cpp sources

        plugins.withPlugin("br.dev.pedrolamarao.metal.cpp",plugin ->
        {
            final var metal = project.getExtensions().getByType(MetalExtension.class);
            final var cpp = (NamedDomainObjectContainer<?>) metal.getExtensions().getByName("cpp");
            final var sources = (MetalCppSources) cpp.create(name);
            sources.getTargets().convention(component.getTargets());
            project.getLogger().info("gradle-metal: creating main sources: {}",sources);
        });

        // if asm plugin then create asm sources

        plugins.withPlugin("br.dev.pedrolamarao.metal.asm",plugin ->
        {
            final var metal = project.getExtensions().getByType(MetalExtension.class);
            final var asm = (NamedDomainObjectContainer<?>) metal.getExtensions().getByName("asm");
            final var sources = (MetalAsmSources) asm.create(name);
            sources.getTargets().convention(component.getTargets());
            component.getSources().from(sources.getOutputs());
            project.getLogger().info("gradle-metal: creating main sources: {}",sources);
        });

        // if c plugin then create c sources

        plugins.withPlugin("br.dev.pedrolamarao.metal.c",plugin ->
        {
            final var metal = project.getExtensions().getByType(MetalExtension.class);
            final var c = (NamedDomainObjectContainer<?>) metal.getExtensions().getByName("c");
            final var sources = (MetalCSources) c.create(name);
            sources.getTargets().convention(component.getTargets());
            component.getSources().from(sources.getOutputs());
            project.getLogger().info("gradle-metal: creating main sources: {}",sources);
        });

        // if cxx plugin then create cxx sources

        plugins.withPlugin("br.dev.pedrolamarao.metal.cxx",plugin ->
        {
            final var metal = project.getExtensions().getByType(MetalExtension.class);
            final var cxx = (NamedDomainObjectContainer<?>) metal.getExtensions().getByName("cxx");
            final var sources = (MetalCxxSources) cxx.create(name);
            sources.getTargets().convention(component.getTargets());
            component.getSources().from(sources.getOutputs());
            project.getLogger().info("gradle-metal: creating main sources: {}",sources);
        });

        // if ixx plugin then create ixx sources

        plugins.withPlugin("br.dev.pedrolamarao.metal.ixx",plugin ->
        {
            final var metal = project.getExtensions().getByType(MetalExtension.class);
            final var ixx = (NamedDomainObjectContainer<?>) metal.getExtensions().getByName("ixx");
            final var sources = (MetalIxxSources) ixx.create(name);
            sources.getTargets().convention(component.getTargets());
            project.getLogger().info("gradle-metal: creating main sources: {}",sources);
        });

        // if cpp sources then wire to other sources' includables

        project.afterEvaluate(it ->
        {
            final var metal = it.getExtensions().getByType(MetalExtension.class);

            final var cppContainer = (NamedDomainObjectContainer<?>) metal.getExtensions().findByName("cpp");
            if (cppContainer == null) return;
            final var cpp = (MetalCppSources) cppContainer.getByName(name);

            final var asmContainer = (NamedDomainObjectContainer<?>) metal.getExtensions().findByName("asm");
            if (asmContainer != null) {
                final var asm = (MetalAsmSources) asmContainer.getByName(name);
                asm.getIncludes().from( cpp.getSources() );
                project.getLogger().info("gradle-metal: wiring sources: {} -> {}",cpp,asm);
            }

            final var cContainer = (NamedDomainObjectContainer<?>) metal.getExtensions().findByName("c");
            if (cContainer != null) {
                final var c = (MetalCSources) cContainer.getByName(name);
                c.getIncludes().from( cpp.getSources() );
                project.getLogger().info("gradle-metal: wiring sources: {} -> {}",cpp,c);
            }

            final var cxxContainer = (NamedDomainObjectContainer<?>) metal.getExtensions().findByName("cxx");
            if (cxxContainer != null) {
                final var cxx = (MetalCxxSources) cxxContainer.getByName(name);
                cxx.getIncludes().from( cpp.getSources() );
                project.getLogger().info("gradle-metal: wiring sources: {} -> {}",cpp,cxx);
            }

            final var ixxContainer = (NamedDomainObjectContainer<?>) metal.getExtensions().findByName("ixx");
            if (ixxContainer != null) {
                final var ixx = (MetalIxxSources) ixxContainer.getByName(name);
                ixx.getIncludes().from( cpp.getSources() );
                project.getLogger().info("gradle-metal: wiring sources: {} -> {}",cpp,ixx);
            }
        });

        // if ixx sources then wire to other sources' importables

        project.afterEvaluate(it ->
        {
            final var metal = it.getExtensions().getByType(MetalExtension.class);

            final var ixxContainer = (NamedDomainObjectContainer<?>) metal.getExtensions().findByName("ixx");
            if (ixxContainer == null) return;
            final var ixx = (MetalIxxSources) ixxContainer.getByName(name);

            final var cxxContainer = (NamedDomainObjectContainer<?>) metal.getExtensions().findByName("cxx");
            if (cxxContainer != null) {
                final var cxx = (MetalCxxSources) cxxContainer.getByName(name);
                cxx.getImports().from( ixx.getOutputDirectory() );
                cxx.getSources().from( ixx.getOutputs() );
                project.getLogger().info("gradle-metal: wiring sources: {} -> {}",ixx,cxx);
            }
        });
    }

    /**
     * Wire dependency component to dependent component.
     *
     * @param project     project
     * @param dependent   dependent component
     * @param dependency  dependency component
     */
    protected void wire (Project project, MetalComponent dependent, MetalComponent dependency)
    {
        final var extensions = project.getExtensions();
        final var logger = project.getLogger();
        final var plugins = project.getPluginManager();

        plugins.withPlugin("br.dev.pedrolamarao.metal.asm",plugin ->
        {
            final var metal = extensions.getByType(MetalExtension.class);
            final var container = (NamedDomainObjectContainer<?>) metal.getExtensions().getByName("asm");
            final var sources = (MetalAsmSources) container.getByName(dependency.getName());
            dependent.getInternalSources().from(sources.getOutputs());
            logger.info("gradle-metal: wiring: {} -> {}", sources, dependent);
        });

        plugins.withPlugin("br.dev.pedrolamarao.metal.c",plugin ->
        {
            final var metal = extensions.getByType(MetalExtension.class);
            final var container = (NamedDomainObjectContainer<?>) metal.getExtensions().getByName("c");
            final var sources = (MetalCSources) container.getByName(dependency.getName());
            dependent.getInternalSources().from(sources.getOutputs());
            logger.info("gradle-metal: wiring: {} -> {}", sources, dependent);
        });

        plugins.withPlugin("br.dev.pedrolamarao.metal.cxx",plugin ->
        {
            final var metal = extensions.getByType(MetalExtension.class);
            final var container = (NamedDomainObjectContainer<?>) metal.getExtensions().getByName("cxx");
            final var sources = (MetalCxxSources) container.getByName(dependency.getName());
            dependent.getInternalSources().from(sources.getOutputs());
            logger.info("gradle-metal: wiring: {} -> {}", sources, dependent);
        });

        plugins.withPlugin("br.dev.pedrolamarao.metal.ixx",plugin ->
        {
            final var metal = extensions.getByType(MetalExtension.class);
            final var container = (NamedDomainObjectContainer<?>) metal.getExtensions().getByName("ixx");
            final var dependentIxx = (MetalIxxSources) container.getByName(dependent.getName());
            final var dependencyIxx = (MetalIxxSources) container.getByName(dependency.getName());
            dependentIxx.getImports().from(dependencyIxx.getOutputDirectory());
            logger.info("gradle-metal: wiring: {} -> {}", dependencyIxx, dependentIxx);
        });

        project.afterEvaluate(it ->
        {
            final var metal = it.getExtensions().getByType(MetalExtension.class);
            final var tasks = it.getTasks();

            final var asm = (NamedDomainObjectContainer<?>) metal.getExtensions().findByName("asm");
            final var ixx = (NamedDomainObjectContainer<?>) metal.getExtensions().findByName("ixx");
            final var c   = (NamedDomainObjectContainer<?>) metal.getExtensions().findByName("c");
            final var cpp = (NamedDomainObjectContainer<?>) metal.getExtensions().findByName("cpp");
            final var cxx = (NamedDomainObjectContainer<?>) metal.getExtensions().findByName("cxx");

            if (cpp != null)
            {
                // TODO: wire cpp includables
            }

            if (ixx != null)
            {
                if (cxx != null) {
                    final var dependentCxx = (MetalCxxSources) cxx.getByName(dependent.getName());
                    final var dependencyIxx = (MetalIxxSources) ixx.getByName(dependency.getName());
                    dependentCxx.getImports().from( dependencyIxx.getOutputDirectory() );
                    // TODO: wire inputs and outputs, not tasks
                    tasks.named("commands-test-cxx").configure(task -> task.dependsOn("compile-main-ixx"));
                    tasks.named("compile-test-cxx").configure(task -> task.dependsOn("compile-main-ixx"));
                    it.getLogger().info("gradle-metal: wiring: {} -> {}",dependencyIxx,dependentCxx);
                }
            }
        });
    }
}
