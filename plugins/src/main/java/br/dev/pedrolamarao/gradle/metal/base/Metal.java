package br.dev.pedrolamarao.gradle.metal.base;

public class Metal
{
    public static final String COMMANDS_ELEMENTS = "commandsElements";
    public static final String COMMANDS_DEPENDENCIES = "commands";
    public static final String EMPTY_ELEMENTS = "emptyElements";
    public static final String EXECUTABLE_DEPENDENCIES = "executableDependencies";
    public static final String EXECUTABLE_ELEMENTS = "executableElements";
    public static final String IMPORTABLE_DEPENDENCIES = "importableDependencies";
    public static final String IMPORTABLE_ELEMENTS = "importableElements";
    public static final String INCLUDABLE_DEPENDENCIES = "includableDependencies";
    public static final String INCLUDABLE_ELEMENTS = "includableElements";
    public static final String LINKABLE_DEPENDENCIES = "linkableDependencies";
    public static final String LINKABLE_ELEMENTS = "linkableElements";

    public static String toExecutablePath (String list, String name)
    {
        for (var item : list.split(":;"))
        {
            final var directory = Paths.get(item);
            if (! Files.isDirectory(directory)) continue;
            final var file = directory.resolve(name);
            if (Files.isExecutable(file)) return file.toString();
            final var file_exe = file.resolveSibling(name + ".exe");
            if (Files.isExecutable(file_exe)) return file_exe.toString();
        }

        throw new GradleException("executable file not found: " + name);
    }
}
