package com.worth.ifs.assessment.workflow.configuration;

import com.worth.ifs.assessment.workflow.AssessmentWorkflowEventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.annotation.WithStateMachine;
import org.springframework.statemachine.recipes.persist.PersistStateMachineHandler;

@WithStateMachine
public class PersistHandlerConfig {

    @Autowired
    private StateMachine<String, String> stateMachine;

    public PersistHandlerConfig() {

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
