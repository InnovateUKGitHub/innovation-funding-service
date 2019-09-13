package org.innovateuk.ifs.project.core.workflow.configuration.actions;

import org.innovateuk.ifs.grant.service.GrantProcessService;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.resource.ProjectEvent;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

@Component
public class ProjectLiveAction extends BaseProjectAction {

    @Autowired
    private GrantProcessService grantProcessService;

    @Override
    protected void doExecute(Project project, StateContext<ProjectState, ProjectEvent> context) {
        grantProcessService.createGrantProcess(project.getApplication().getId());
    }
}
