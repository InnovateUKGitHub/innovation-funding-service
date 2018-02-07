package org.innovateuk.ifs.assessment.interview.workflow.configuration;

import org.innovateuk.ifs.assessment.interview.resource.AssessmentInterviewPanelEvent;
import org.innovateuk.ifs.assessment.interview.resource.AssessmentInterviewPanelState;
import org.innovateuk.ifs.assessment.interview.workflow.actions.FeedbackResponseAssessmentInterviewPanelAction;
import org.innovateuk.ifs.assessment.interview.workflow.actions.NotifyAssessmentInterviewPanelAction;
import org.innovateuk.ifs.workflow.WorkflowStateMachineListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.LinkedHashSet;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.assessment.interview.resource.AssessmentInterviewPanelEvent.*;
import static org.innovateuk.ifs.assessment.interview.resource.AssessmentInterviewPanelState.*;

/**
 * Describes the workflow for assessment interview panel.
 */
@Configuration
@EnableStateMachineFactory(name = "assessmentInterviewPanelStateMachineFactory")
public class AssessmentInterviewPanelWorkflow extends StateMachineConfigurerAdapter<AssessmentInterviewPanelState, AssessmentInterviewPanelEvent> {

    @Autowired
    private NotifyAssessmentInterviewPanelAction notifyAssessmentInterviewPanelAction;

    @Autowired
    private FeedbackResponseAssessmentInterviewPanelAction feedbackResponseAssessmentInterviewPanelAction;

    @Override
    public void configure(StateMachineConfigurationConfigurer<AssessmentInterviewPanelState, AssessmentInterviewPanelEvent> config) throws Exception {
        config.withConfiguration().listener(new WorkflowStateMachineListener<>());
    }

    @Override
    public void configure(StateMachineStateConfigurer<AssessmentInterviewPanelState, AssessmentInterviewPanelEvent> states) throws Exception {
        states.withStates()
                .initial(CREATED)
                .states(new LinkedHashSet<>(asList(AssessmentInterviewPanelState.values())));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<AssessmentInterviewPanelState, AssessmentInterviewPanelEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(CREATED).target(AWAITING_FEEDBACK_RESPONSE)
                .event(NOTIFY)
                .action(notifyAssessmentInterviewPanelAction)
                .and()
                .withExternal()
                .source(AWAITING_FEEDBACK_RESPONSE).target(AWAITING_FEEDBACK_RESPONSE)
                .event(NOTIFY)
                .and()
                .withExternal()
                .source(AWAITING_FEEDBACK_RESPONSE).target(SUBMITTED_FEEDBACK_RESPONSE)
                .event(RESPOND)
                .action(feedbackResponseAssessmentInterviewPanelAction);
    }
}