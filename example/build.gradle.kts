plugins {
    id("base")
}

val commands by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved  = true
}

dependencies {
    commands(project(":bar","commandsElements"))
    commands(project(":foo","commandsElements"))
    commands(project(":meh","commandsElements"))
}

tasks.register("commands") {
    dependsOn(commands.buildDependencies)
    group = "metal"
    doLast {
        val list = mutableListOf<Any>()
        commands.forEach {
            val json = groovy.json.JsonSlurper().parse(it) as List<*>
            json.forEach { item -> if (item != null) list.add(item) }
        }
        val builder = groovy.json.JsonBuilder()
        builder.call(list)
        file("compile_commands.json").writer().use {
            it.write(builder.toPrettyString())
        }
    }
}

tasks.wrapper.configure {
    gradleVersion = "8.3"
}