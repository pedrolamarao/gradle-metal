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
            project.getLogger().info("gradle-metal: creating main sources: {}",sources);
        });

        // if asm plugin then create asm sources

        plugins.withPlugin("br.dev.pedrolamarao.metal.asm",plugin ->
        {
            final var metal = project.getExtensions().getByType(MetalExtension.class);
            final var asm = (NamedDomainObjectContainer<?>) metal.getExtensions().getByName("asm");
            final var sources = (MetalAsmSources) asm.create(name);
            component.source(sources.getOutputs());
            project.getLogger().info("gradle-metal: creating main sources: {}",sources);
        });

        // if c plugin then create c sources

        plugins.withPlugin("br.dev.pedrolamarao.metal.c",plugin ->
        {
            final var metal = project.getExtensions().getByType(MetalExtension.class);
            final var c = (NamedDomainObjectContainer<?>) metal.getExtensions().getByName("c");
            final var sources = (MetalCSources) c.create(name);
            component.source(sources.getOutputs());
            project.getLogger().info("gradle-metal: creating main sources: {}",sources);
        });

        // if cxx plugin then create cxx sources

        plugins.withPlugin("br.dev.pedrolamarao.metal.cxx",plugin ->
        {
            final var metal = project.getExtensions().getByType(MetalExtension.class);
            final var cxx = (NamedDomainObjectContainer<?>) metal.getExtensions().getByName("cxx");
            final var sources = (MetalCxxSources) cxx.create(name);
            component.source(sources.getOutputs());
            project.getLogger().info("gradle-metal: creating main sources: {}",sources);
        });

        // if ixx plugin then create ixx sources

        plugins.withPlugin("br.dev.pedrolamarao.metal.ixx",plugin ->
        {
            final var metal = project.getExtensions().getByType(MetalExtension.class);
            final var ixx = (NamedDomainObjectContainer<?>) metal.getExtensions().getByName("ixx");
            final var sources = (MetalIxxSources) ixx.create(name);
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
                asm.includable( cpp.getSources().getSourceDirectories() );
                project.getLogger().info("gradle-metal: wiring sources: {} -> {}",cpp,asm);
            }

            final var cContainer = (NamedDomainObjectContainer<?>) metal.getExtensions().findByName("c");
            if (cContainer != null) {
                final var c = (MetalCSources) cContainer.getByName(name);
                c.includable( cpp.getSources().getSourceDirectories() );
                project.getLogger().info("gradle-metal: wiring sources: {} -> {}",cpp,c);
            }

            final var cxxContainer = (NamedDomainObjectContainer<?>) metal.getExtensions().findByName("cxx");
            if (cxxContainer != null) {
                final var cxx = (MetalCxxSources) cxxContainer.getByName(name);
                cxx.includable( cpp.getSources().getSourceDirectories() );
                project.getLogger().info("gradle-metal: wiring sources: {} -> {}",cpp,cxx);
            }

            final var ixxContainer = (NamedDomainObjectContainer<?>) metal.getExtensions().findByName("ixx");
            if (ixxContainer != null) {
                final var ixx = (MetalIxxSources) ixxContainer.getByName(name);
                ixx.includable( cpp.getSources().getSourceDirectories() );
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
                cxx.importable( ixx.getOutputDirectory() );
                cxx.source( ixx.getOutputs() );
                project.getLogger().info("gradle-metal: wiring sources: {} -> {}",ixx,cxx);
            }
        });
    }
}
