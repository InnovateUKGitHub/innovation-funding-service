package com.worth.ifs.project.workflow.projectdetails.configuration;

import com.worth.ifs.assessment.repository.AssessmentRepository;
import com.worth.ifs.project.repository.ProjectDetailsProcessRepository;
import com.worth.ifs.project.repository.ProjectRepository;
import com.worth.ifs.project.repository.ProjectUserRepository;
import com.worth.ifs.project.resource.ProjectDetailsOutcomes;
import com.worth.ifs.project.resource.ProjectDetailsState;
import com.worth.ifs.workflow.GenericPersistStateMachineHandler;
import com.worth.ifs.workflow.repository.ActivityStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.recipes.persist.PersistStateMachineHandler;

/**
 * The {@link PersistStateMachineHandler} is being used for the Project Details workflow
 * and is configured here.
 * This allows having multiple instances of one state machine, so each individual
 * state can be transferred to the next, depending on its starting position.
 */
@Configuration
public class ProjectDetailsPersistHandlerConfig {


    @Autowired
    private ProjectDetailsProcessRepository projectDetailsProcessRepository;

    @Autowired
    private ActivityStateRepository activityStateRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectUserRepository projectUserRepository;

    public ProjectDetailsPersistHandlerConfig() {
    	// no-arg constructor
    }

    // TODO DW - INFUND-4911 - just have ProjectDetailsWorkflowService as a @Component
//    @Bean
//    public ProjectDetailsWorkflowService projectDetailsWorkflowEventHandler() {
//        return new ProjectDetailsWorkflowService(new GenericPersistStateMachineHandler<>(stateMachine), projectDetailsProcessRepository,
//                activityStateRepository, projectRepository, projectUserRepository);
//    }
}
