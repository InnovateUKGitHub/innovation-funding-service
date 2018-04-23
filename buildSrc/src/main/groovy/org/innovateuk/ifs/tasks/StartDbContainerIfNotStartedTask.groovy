package org.innovateuk.ifs.tasks

import com.bmuschko.gradle.docker.tasks.AbstractDockerRemoteApiTask
import org.gradle.api.tasks.Internal



class StartDbContainerIfNotStartedTask extends AbstractDockerRemoteApiTask {

    @Internal
    String containerId

    void runRemoteCommand(dockerClient) {
        println "container id is $containerId"
        def inspectCommand = dockerClient.inspectContainerCmd(containerId)

        def inspectResponse
        try {
            inspectResponse = inspectCommand.exec()
        } catch (e) {
            println "container id $containerId is not found"
            return
        }

        println "container id $containerId is found in status $inspectResponse.state.status"

        def state = response.status.status

        if("running" == state) {
            println "container id $containerId is running - nothing to do"
        } else if("created" == state || "paused" == state || "exited" == state || "dead" == state) {

            println "container id $containerId is $state - starting container"

            def startCommand = dockerClient.startContainerCmd(containerId)
            startCommand.exec()
        } else {
            println "container id $containerId is $state - unable to handle this"

        }
    }
}