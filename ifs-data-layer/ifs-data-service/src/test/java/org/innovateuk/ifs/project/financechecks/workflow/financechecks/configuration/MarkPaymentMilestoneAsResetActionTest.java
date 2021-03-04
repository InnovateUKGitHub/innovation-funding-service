package org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration;

import org.innovateuk.ifs.project.finance.resource.PaymentMilestoneEvent;
import org.innovateuk.ifs.project.finance.resource.PaymentMilestoneState;
import org.innovateuk.ifs.project.financechecks.domain.PaymentMilestoneProcess;
import org.innovateuk.ifs.project.financechecks.domain.PaymentMilestoneResetOutcome;
import org.junit.Test;
import org.springframework.statemachine.StateContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.project.core.builder.PartnerOrganisationBuilder.newPartnerOrganisation;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MarkPaymentMilestoneAsResetActionTest {
    private MarkPaymentMilestoneAsResetAction action = new MarkPaymentMilestoneAsResetAction();

    @Test
    public void shouldDoNothingIfResetOutcomeNotPresent() {
        PaymentMilestoneProcess process = new PaymentMilestoneProcess(newProjectUser().build(), newPartnerOrganisation().build(), PaymentMilestoneState.REVIEW);
        StateContext<PaymentMilestoneState, PaymentMilestoneEvent> context = setupContext(process);

        action.doExecute(context);

        assertThat(process.getPaymentMilestoneResetOutcomes()).isEmpty();
    }

    @Test
    public void shouldAddOutcomeWhenPresent() {
        PaymentMilestoneProcess process = new PaymentMilestoneProcess(newProjectUser().build(), newPartnerOrganisation().build(), PaymentMilestoneState.REVIEW);
        PaymentMilestoneResetOutcome outcome = new PaymentMilestoneResetOutcome();
        StateContext<PaymentMilestoneState, PaymentMilestoneEvent> context = setupContext(process, outcome);

        action.doExecute(context);

        assertThat(process.getPaymentMilestoneResetOutcomes()).hasSize(1);
        assertThat(process.getPaymentMilestoneResetOutcomes()).containsExactly(outcome);
    }

    private StateContext<PaymentMilestoneState, PaymentMilestoneEvent> setupContext(PaymentMilestoneProcess process) {
        StateContext<PaymentMilestoneState, PaymentMilestoneEvent> context = mock(StateContext.class);
        when(context.getMessageHeader("process")).thenReturn(process);
        return context;
    }

    private StateContext<PaymentMilestoneState, PaymentMilestoneEvent> setupContext(PaymentMilestoneProcess process, PaymentMilestoneResetOutcome outcome) {
        StateContext<PaymentMilestoneState, PaymentMilestoneEvent> context = setupContext(process);
        when(context.getMessageHeader("reset")).thenReturn(outcome);
        return context;
    }
}
