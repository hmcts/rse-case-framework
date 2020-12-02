package uk.gov.hmcts

import org.gradle.api.tasks.Exec

class AngularExec extends Exec {
    AngularExec() {
        getInputs().files(project.fileTree(project.projectDir).matching {
            include "*.json"
        });
        getInputs().dir(project.file('src'))
        workingDir project.getRootProject().projectDir
        commandLine "docker-compose", "-p", name, "up", "--build", "--no-deps", "frontend"
    }

    void ngCommand(String command) {
        environment("NG_COMMAND", command)
    }
}
