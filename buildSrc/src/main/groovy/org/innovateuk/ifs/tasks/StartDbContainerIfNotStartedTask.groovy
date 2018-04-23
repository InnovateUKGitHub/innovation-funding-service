package org.innovateuk.ifs.tasks

import com.bmuschko.gradle.docker.tasks.AbstractDockerRemoteApiTask
import org.gradle.api.tasks.Internal
import org.gradle.api.GradleScriptException
import com.github.dockerjava.api.exception.NotFoundException

class StartDbContainerIfNotStartedTask extends AbstractDockerRemoteApiTask {

    @Internal
    String containerId

    void runRemoteCommand(dockerClient) {
        def inspectCommand = dockerClient.inspectContainerCmd(containerId)

        def inspectResponse
        try {
            inspectResponse = inspectCommand.exec()
        } catch (NotFoundException e) {
            throw new GradleScriptException("container [$containerId] is not found", e)
        }

        def status = inspectResponse.state.status

        if("running" == status || "restarting" == status) {
            logger.info("container [$containerId] is $status - nothing to do")
        } else if("created" == status || "paused" == status || "exited" == status || "dead" == status) {

            logger.info("container [$containerId] is $status - starting container")

            def startCommand = dockerClient.startContainerCmd(containerId)
            startCommand.exec()
        } else if("removing" == status){
            throw new GradleScriptException("container [$containerId] is in unrecoverable state - $status", null)
        } else {
            throw new GradleScriptException("container [$containerId] is in unrecognised state - $status", null)
        }
    }
}