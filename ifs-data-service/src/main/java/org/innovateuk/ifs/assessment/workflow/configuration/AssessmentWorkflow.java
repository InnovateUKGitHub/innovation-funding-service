package org.innovateuk.ifs.assessment.workflow.configuration;

import org.innovateuk.ifs.assessment.resource.AssessmentOutcomes;
import org.innovateuk.ifs.assessment.resource.AssessmentStates;
import org.innovateuk.ifs.assessment.workflow.actions.FundingDecisionAction;
import org.innovateuk.ifs.assessment.workflow.actions.RejectAction;
import org.innovateuk.ifs.assessment.workflow.actions.WithdrawAction;
import org.innovateuk.ifs.assessment.workflow.actions.WithdrawCreatedAction;
import org.innovateuk.ifs.assessment.workflow.guards.AssessmentCompleteGuard;
import org.innovateuk.ifs.assessment.workflow.guards.CompetitionInAssessmentGuard;
import org.innovateuk.ifs.assessment.workflow.guards.ProcessOutcomeGuard;
import org.innovateuk.ifs.workflow.WorkflowStateMachineListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.LinkedHashSet;

import static org.innovateuk.ifs.assessment.resource.AssessmentOutcomes.*;
import static org.innovateuk.ifs.assessment.resource.AssessmentStates.*;
import static java.util.Arrays.asList;

/**
 * Describes the workflow for assessment. This is from accepting a competition to submitting the application.
 * A persistent configuration is used, so we can apply different states to different assessments.
 */
@Configuration
@EnableStateMachine(name = "assessmentStateMachine")
public class AssessmentWorkflow extends StateMachineConfigurerAdapter<AssessmentStates, AssessmentOutcomes> {

    @Autowired
    private RejectAction rejectAction;

    @Autowired
    private WithdrawCreatedAction withdrawCreatedAction;

    @Autowired
    private WithdrawAction withdrawAction;

    @Autowired
    private FundingDecisionAction fundingDecisionAction;

    @Autowired
    private ProcessOutcomeGuard processOutcomeExistsGuard;

    @Autowired
    private AssessmentCompleteGuard assessmentCompleteGuard;

    @Autowired
    private CompetitionInAssessmentGuard competitionInAssessmentGuard;

    @Override
    public void configure(StateMachineConfigurationConfigurer<AssessmentStates, AssessmentOutcomes> config) throws Exception {
        config.withConfiguration().listener(new WorkflowStateMachineListener<>());
    }

    @Override
    public void configure(StateMachineStateConfigurer<AssessmentStates, AssessmentOutcomes> states) throws Exception {
        states.withStates()
                .initial(PENDING)
                .states(new LinkedHashSet<>(asList(AssessmentStates.values())))
                .choice(DECIDE_IF_READY_TO_SUBMIT);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<AssessmentStates, AssessmentOutcomes> transitions) throws Exception {
        transitions
                .withExternal()
                    .source(CREATED).target(CREATED)
                    .event(WITHDRAW)
                    .action(withdrawCreatedAction)
                    .and()
                .withExternal()
                    .source(CREATED).target(PENDING)
                    .event(NOTIFY)
                    .and()
                .withExternal()
                    .source(PENDING).target(REJECTED)
                    .event(REJECT)
                    .action(rejectAction)
                    .guard(processOutcomeExistsGuard)
                    .and()
                .withExternal()
                    .source(PENDING).target(WITHDRAWN)
                    .event(WITHDRAW)
                    .action(withdrawAction)
                    .guard(processOutcomeExistsGuard)
                    .and()
                .withExternal()
                    .source(PENDING).target(ACCEPTED)
                    .event(ACCEPT)
                    .and()
                .withExternal()
                    .source(ACCEPTED).target(REJECTED)
                    .event(REJECT)
                    .action(rejectAction)
                    .guard(processOutcomeExistsGuard)
                    .and()
                .withExternal()
                    .source(ACCEPTED).target(WITHDRAWN)
                    .event(WITHDRAW)
                    .action(withdrawAction)
                    .guard(processOutcomeExistsGuard)
                    .and()
                .withExternal()
                    .source(ACCEPTED).target(DECIDE_IF_READY_TO_SUBMIT)
                    .event(FEEDBACK)
                    .and()
                .withExternal()
                    .source(ACCEPTED).target(DECIDE_IF_READY_TO_SUBMIT)
                    .event(FUNDING_DECISION)
                    .action(fundingDecisionAction)
                    .guard(processOutcomeExistsGuard)
                    .and()
                .withExternal()
                    .source(OPEN).target(REJECTED)
                    .event(REJECT)
                    .action(rejectAction)
                    .guard(processOutcomeExistsGuard)
                    .and()
                .withExternal()
                    .source(OPEN).target(WITHDRAWN)
                    .event(WITHDRAW)
                    .action(withdrawAction)
                    .guard(processOutcomeExistsGuard)
                    .and()
                .withExternal()
                    .source(OPEN).target(DECIDE_IF_READY_TO_SUBMIT)
                    .event(FEEDBACK)
                    .and()
                .withExternal()
                    .source(OPEN).target(DECIDE_IF_READY_TO_SUBMIT)
                    .event(FUNDING_DECISION)
                    .action(fundingDecisionAction)
                    .guard(processOutcomeExistsGuard)
                    .and()
                .withExternal()
                    .source(READY_TO_SUBMIT).target(REJECTED)
                    .event(REJECT)
                    .action(rejectAction)
                    .guard(processOutcomeExistsGuard)
                    .and()
                .withExternal()
                    .source(READY_TO_SUBMIT).target(WITHDRAWN)
                    .event(WITHDRAW)
                    .action(withdrawAction)
                    .guard(processOutcomeExistsGuard)
                    .and()
                .withExternal()
                    .source(READY_TO_SUBMIT).target(DECIDE_IF_READY_TO_SUBMIT)
                    .event(FEEDBACK)
                    .and()
                .withExternal()
                    .source(READY_TO_SUBMIT).target(DECIDE_IF_READY_TO_SUBMIT)
                    .event(FUNDING_DECISION)
                    .action(fundingDecisionAction)
                    .guard(processOutcomeExistsGuard)
                    .and()
                .withChoice()
                    .source(DECIDE_IF_READY_TO_SUBMIT)
                    .first(READY_TO_SUBMIT, assessmentCompleteGuard)
                    .last(OPEN)
                    .and()
                .withExternal()
                    .source(READY_TO_SUBMIT).target(SUBMITTED)
                    .event(SUBMIT)
                    .guard(competitionInAssessmentGuard);
    }
}
