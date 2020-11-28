package uk.gov.hmcts

import org.gradle.api.tasks.Exec

class AngularExec extends Exec {
    AngularExec() {
        getInputs().files(project.fileTree(project.projectDir).matching {
            include "*.json"
        });
        getInputs().dir(project.file('src'))
        workingDir project.getRootProject().projectDir
        commandLine "docker-compose", "run", "--no-deps", "frontend"
    }

    void ngCommand(String... arguments) {
        def c = getCommandLine()
        c.addAll(arguments)
        setCommandLine(c);
    }
}
