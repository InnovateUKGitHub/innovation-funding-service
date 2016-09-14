package com.worth.ifs.assessment.workflow.configuration;

import com.worth.ifs.assessment.workflow.AssessmentWorkflowEventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.annotation.WithStateMachine;
import org.springframework.statemachine.recipes.persist.PersistStateMachineHandler;

/**
 * The {@link PersistStateMachineHandler} is being used for the assessor workflow
 * and is configured here.
 * This allows having multiple instances of one state machine, so each individual
 * state can be transferred to the next, depending on its starting position.
 */
@WithStateMachine(name = "assessmentStateMachine")
public class AssessmentPersistHandlerConfig {

    @Autowired
    private StateMachine<String, String> stateMachine;

    public AssessmentPersistHandlerConfig() {
    	// no-arg constructor
    }

    @Bean
    public AssessmentWorkflowEventHandler persist() {
        return new AssessmentWorkflowEventHandler(persistStateMachineHandler());
    }

    @Bean
    public PersistStateMachineHandler persistStateMachineHandler() {
        return new PersistStateMachineHandler(stateMachine);
    }
}
