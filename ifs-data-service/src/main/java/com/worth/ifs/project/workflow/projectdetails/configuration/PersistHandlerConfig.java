package com.worth.ifs.project.workflow.projectdetails.configuration;

import com.worth.ifs.project.workflow.projectdetails.ProjectDetailsWorkflowEventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.annotation.WithStateMachine;
import org.springframework.statemachine.recipes.persist.PersistStateMachineHandler;

/**
 * The {@link PersistStateMachineHandler} is being used for the Project Details workflow
 * and is configured here.
 * This allows having multiple instances of one state machine, so each individual
 * state can be transferred to the next, depending on its starting position.
 */
@WithStateMachine(name = "projectDetailsStateMachine")
public class PersistHandlerConfig {

    @Autowired
    private StateMachine<String, String> stateMachine;

    public PersistHandlerConfig() {
    	// no-arg constructor
    }

    @Bean
    public ProjectDetailsWorkflowEventHandler persist() {
        return new ProjectDetailsWorkflowEventHandler(persistStateMachineHandler());
    }

    @Bean
    public PersistStateMachineHandler persistStateMachineHandler() {
        return new PersistStateMachineHandler(stateMachine);
    }
}
