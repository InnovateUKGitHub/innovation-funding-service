package org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration;

import org.innovateuk.ifs.project.finance.resource.EligibilityEvent;
import org.innovateuk.ifs.project.finance.resource.EligibilityState;
import org.innovateuk.ifs.project.financechecks.domain.EligibilityProcess;
import org.innovateuk.ifs.project.financechecks.domain.EligibilityResetOutcome;
import org.junit.Test;
import org.springframework.statemachine.StateContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.project.core.builder.PartnerOrganisationBuilder.newPartnerOrganisation;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MarkEligibilityAsResetActionTest {
    private MarkEligibilityAsResetAction action = new MarkEligibilityAsResetAction();

    @Test
    public void shouldDoNothingIfResetOutcomeNotPresent() {
        EligibilityProcess process = new EligibilityProcess(newProjectUser().build(), newPartnerOrganisation().build(), EligibilityState.REVIEW);
        StateContext<EligibilityState, EligibilityEvent> context = setupContext(process);

        action.doExecute(context);

        assertThat(process.getEligibilityResetOutcomes()).isEmpty();
    }

    @Test
    public void shouldAddOutcomeWhenPresent() {
        EligibilityProcess process = new EligibilityProcess(newProjectUser().build(), newPartnerOrganisation().build(), EligibilityState.REVIEW);
        EligibilityResetOutcome outcome = new EligibilityResetOutcome();
        StateContext<EligibilityState, EligibilityEvent> context = setupContext(process, outcome);

        action.doExecute(context);

        assertThat(process.getEligibilityResetOutcomes()).hasSize(1);
        assertThat(process.getEligibilityResetOutcomes()).containsExactly(outcome);
    }

    private StateContext<EligibilityState, EligibilityEvent> setupContext(EligibilityProcess process) {
        StateContext<EligibilityState, EligibilityEvent> context = mock(StateContext.class);
        when(context.getMessageHeader("process")).thenReturn(process);
        return context;
    }

    private StateContext<EligibilityState, EligibilityEvent> setupContext(EligibilityProcess process, EligibilityResetOutcome outcome) {
        StateContext<EligibilityState, EligibilityEvent> context = setupContext(process);
        when(context.getMessageHeader("reset")).thenReturn(outcome);
        return context;
    }
}
