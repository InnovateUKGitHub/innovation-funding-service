package com.worth.ifs.project.workflow.projectdetails.actions;

import com.worth.ifs.project.domain.Project;
import com.worth.ifs.project.domain.ProjectDetailsProcess;
import com.worth.ifs.project.domain.ProjectUser;
import com.worth.ifs.project.repository.ProjectDetailsProcessRepository;
import com.worth.ifs.project.workflow.projectdetails.configuration.ProjectDetailsWorkflowEventHandler;
import com.worth.ifs.workflow.domain.ActivityState;
import com.worth.ifs.workflow.domain.ProcessOutcome;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

/**
 * The {@code {@link ProjectCreatedAction}} is triggered when a new Project has been created.
 * The purpose of this action is to create a new Process for Project Details for that Project.
 *
 * For more info see {@link ProjectDetailsWorkflowEventHandler}
 */
public class ProjectCreatedAction extends BaseProjectDetailsAction {

    @Autowired
    private ProjectDetailsProcessRepository projectDetailsProcessRepository;

    @Override
    protected void doExecute(Project project, ProjectDetailsProcess projectDetails, ProjectUser projectUser,
                             ActivityState initialState, Optional<ProcessOutcome> processOutcome) {

        ProjectDetailsProcess projectDetailsProcess = new ProjectDetailsProcess(projectUser, project, initialState);
        projectDetailsProcessRepository.save(projectDetailsProcess);
    }
}
