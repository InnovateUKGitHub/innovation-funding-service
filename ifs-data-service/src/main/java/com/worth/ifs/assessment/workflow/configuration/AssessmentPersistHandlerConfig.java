package com.worth.ifs.assessment.workflow.configuration;

import com.worth.ifs.assessment.repository.AssessmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.recipes.persist.PersistStateMachineHandler;

/**
 * The {@link PersistStateMachineHandler} is being used for the assessor workflow
 * and is configured here.
 * This allows having multiple instances of one state machine, so each individual
 * state can be transferred to the next, depending on its starting position.
 */
@Configuration
public class AssessmentPersistHandlerConfig {

    @Autowired
    @Qualifier("assessmentStateMachine")
    private StateMachine<String, String> stateMachine;

    @Autowired
    private AssessmentRepository assessmentRepository;

    public AssessmentPersistHandlerConfig() {
    	// no-arg constructor
    }

    @Bean
    public AssessmentWorkflowEventHandler assessmentWorkflowEventHandler() {
        return new AssessmentWorkflowEventHandler(new PersistStateMachineHandler(stateMachine), assessmentRepository);
    }
}
