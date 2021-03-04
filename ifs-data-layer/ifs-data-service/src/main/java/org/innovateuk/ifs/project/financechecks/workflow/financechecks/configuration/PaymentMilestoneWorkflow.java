package org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration;

import org.innovateuk.ifs.project.finance.resource.PaymentMilestoneEvent;
import org.innovateuk.ifs.project.finance.resource.PaymentMilestoneState;
import org.innovateuk.ifs.workflow.WorkflowStateMachineListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

import static org.innovateuk.ifs.project.finance.resource.PaymentMilestoneEvent.*;
import static org.innovateuk.ifs.project.finance.resource.PaymentMilestoneState.*;

@Configuration
@EnableStateMachineFactory(name = "paymentMilestoneStateMachineFactory")
public class PaymentMilestoneWorkflow extends StateMachineConfigurerAdapter<PaymentMilestoneState, PaymentMilestoneEvent> {

    @Autowired
    private PaymentMilestoneApprovedGuard paymentMilestoneApprovedGuard;

    @Autowired
    private MarkPaymentMilestoneAsResetAction markPaymentMilestoneAsResetAction;

    @Override
    public void configure(StateMachineConfigurationConfigurer<PaymentMilestoneState, PaymentMilestoneEvent> config) throws Exception {
        config.withConfiguration().listener(new WorkflowStateMachineListener<>());

    }

    @Override
    public void configure(StateMachineStateConfigurer<PaymentMilestoneState, PaymentMilestoneEvent> states) throws Exception {
        states.withStates()
                .initial(REVIEW)
                .states(EnumSet.of(REVIEW, APPROVED));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<PaymentMilestoneState, PaymentMilestoneEvent> transitions) throws Exception {
        transitions
                .withExternal()
                .source(REVIEW)
                .event(PROJECT_CREATED)
                .target(REVIEW)
                .and()
                .withExternal()
                .source(REVIEW)
                .event(PAYMENT_MILESTONE_APPROVED)
                .guard(paymentMilestoneApprovedGuard)
                .target(APPROVED)
                .and()
                .withExternal()
                .source(APPROVED)
                .event(PAYMENT_MILESTONE_RESET)
                .action(markPaymentMilestoneAsResetAction)
                .target(REVIEW);
    }
}
