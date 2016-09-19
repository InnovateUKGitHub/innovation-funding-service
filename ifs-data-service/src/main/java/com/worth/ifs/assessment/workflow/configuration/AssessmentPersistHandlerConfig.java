package com.worth.ifs.assessment.workflow.configuration;

import com.worth.ifs.application.repository.ApplicationRepository;
import com.worth.ifs.assessment.repository.AssessmentRepository;
import com.worth.ifs.assessment.resource.AssessmentOutcomes;
import com.worth.ifs.assessment.resource.AssessmentStates;
import com.worth.ifs.project.repository.ProjectRepository;
import com.worth.ifs.project.repository.ProjectUserRepository;
import com.worth.ifs.user.repository.ProcessRoleRepository;
import com.worth.ifs.workflow.GenericPersistStateMachineHandler;
import com.worth.ifs.workflow.repository.ActivityStateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateMachine;
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
    private StateMachine<AssessmentStates, AssessmentOutcomes> stateMachine;

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Autowired
    private ActivityStateRepository activityStateRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    @Autowired
    private ProcessRoleRepository processRoleRepository;

    public AssessmentPersistHandlerConfig() {
    	// no-arg constructor
    }

//    @Bean
//    public AssessmentWorkflowService assessmentWorkflowEventHandler() {
//        return new AssessmentWorkflowService(new GenericPersistStateMachineHandler<>(stateMachine), assessmentRepository, activityStateRepository,
//                applicationRepository, processRoleRepository);
//    }
}
