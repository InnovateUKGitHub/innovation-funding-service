package org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration;

import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.project.core.domain.PartnerOrganisation;
import org.innovateuk.ifs.project.finance.resource.ViabilityEvent;
import org.innovateuk.ifs.project.finance.resource.ViabilityState;
import org.innovateuk.ifs.workflow.WorkflowStateMachineListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;
import java.util.function.Predicate;

import static org.innovateuk.ifs.project.finance.resource.ViabilityEvent.*;
import static org.innovateuk.ifs.project.finance.resource.ViabilityState.*;

/**
 * Describes the workflow for the Viability Approval process.
 */
@Configuration
@EnableStateMachineFactory(name = "viabilityStateMachineFactory")
public class ViabilityWorkflow extends StateMachineConfigurerAdapter<ViabilityState, ViabilityEvent> {

    private static Predicate<StateContext<ViabilityState, ViabilityEvent>> isLoan() {
        return   context -> ((PartnerOrganisation) context.getMessageHeader("target"))
                        .getProject()
                        .getApplication()
                        .getCompetition()
                        .getFundingType() == FundingType.LOAN;
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<ViabilityState, ViabilityEvent> config) throws Exception {
        config.withConfiguration().listener(new WorkflowStateMachineListener<>());
    }

    @Override
    public void configure(StateMachineStateConfigurer<ViabilityState, ViabilityEvent> states) throws Exception {
        states.withStates()
                .initial(REVIEW)
                .states(EnumSet.of(REVIEW, NOT_APPLICABLE, APPROVED))
                .end(APPROVED)
                .end(COMPLETED_OFFLINE);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<ViabilityState, ViabilityEvent> transitions) throws Exception {
        transitions
                .withExternal()
                    .source(REVIEW)
                    .event(PROJECT_CREATED)
                    .target(REVIEW)
                    .guard(ctx -> isLoan().negate().test(ctx))
                    .and()
                .withExternal()
                    .source(REVIEW)
                    .event(PROJECT_CREATED)
                    .guard(ctx -> isLoan().test(ctx))
                    .target(COMPLETED_OFFLINE)
                    .and()
                .withExternal()
                    .source(REVIEW)
                    .event(VIABILITY_NOT_APPLICABLE)
                    .target(NOT_APPLICABLE)
                    .and()
                .withExternal()
                    .source(REVIEW)
                    .event(VIABILITY_APPROVED)
                    .target(APPROVED);
    }
}