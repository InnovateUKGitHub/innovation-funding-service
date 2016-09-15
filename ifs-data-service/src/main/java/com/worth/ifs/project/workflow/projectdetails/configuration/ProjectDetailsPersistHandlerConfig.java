package com.worth.ifs.project.workflow.projectdetails.configuration;

import com.worth.ifs.project.repository.ProjectDetailsProcessRepository;
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
    @Qualifier("projectDetailsStateMachine")
    private StateMachine<String, String> stateMachine;

    @Autowired
    private ProjectDetailsProcessRepository projectDetailsProcessRepository;

    public ProjectDetailsPersistHandlerConfig() {
    	// no-arg constructor
    }

    @Bean
    public ProjectDetailsWorkflowEventHandler projectDetailsWorkflowEventHandler() {
        return new ProjectDetailsWorkflowEventHandler(new PersistStateMachineHandler(stateMachine), projectDetailsProcessRepository);
    }
}
