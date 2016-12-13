package org.innovateuk.ifs.project.workflow.configuration;

import org.innovateuk.ifs.project.resource.ProjectOutcomes;
import org.innovateuk.ifs.project.resource.ProjectState;
import org.innovateuk.ifs.workflow.WorkflowStateMachineListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import static org.innovateuk.ifs.project.resource.ProjectOutcomes.GOL_APPROVED;
import static org.innovateuk.ifs.project.resource.ProjectOutcomes.PROJECT_CREATED;
import static org.innovateuk.ifs.project.resource.ProjectState.LIVE;
import static org.innovateuk.ifs.project.resource.ProjectState.SETUP;

/**
 * Describes the workflow for Project Setup.
 */
@Configuration
@EnableStateMachine(name = "projectStateMachine")
public class ProjectWorkflow extends StateMachineConfigurerAdapter<ProjectState, ProjectOutcomes> {

    @Override
    public void configure(StateMachineConfigurationConfigurer<ProjectState, ProjectOutcomes> config) throws Exception {
        config.withConfiguration().listener(new WorkflowStateMachineListener<>());

    }

    @Override
    public void configure(StateMachineStateConfigurer<ProjectState, ProjectOutcomes> states) throws Exception {
        states.withStates()
                .initial(SETUP)
                .end(LIVE);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<ProjectState, ProjectOutcomes> transitions) throws Exception {
        transitions
                .withExternal()
                .source(SETUP)
                .event(PROJECT_CREATED)
                .target(SETUP)
                .and()
                .withExternal()
                .source(SETUP)
                .event(GOL_APPROVED)
                .target(LIVE);
    }
}
