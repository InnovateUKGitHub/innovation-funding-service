package org.innovateuk.ifs.project.financechecks.workflow.financechecks.configuration;

import org.innovateuk.ifs.project.finance.resource.ViabilityEvent;
import org.innovateuk.ifs.project.finance.resource.ViabilityState;
import org.innovateuk.ifs.project.financechecks.domain.ViabilityProcess;
import org.innovateuk.ifs.project.financechecks.domain.ViabilityResetOutcome;
import org.junit.Test;
import org.springframework.statemachine.StateContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.project.core.builder.PartnerOrganisationBuilder.newPartnerOrganisation;
import static org.innovateuk.ifs.project.core.builder.ProjectUserBuilder.newProjectUser;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MarkViabilityAsResetActionTest {
    private MarkViabilityAsResetAction action = new MarkViabilityAsResetAction();

    @Test
    public void shouldDoNothingIfResetOutcomeNotPresent() {
        ViabilityProcess process = new ViabilityProcess(newProjectUser().build(), newPartnerOrganisation().build(), ViabilityState.REVIEW);
        StateContext<ViabilityState, ViabilityEvent> context = setupContext(process);

        action.doExecute(context);

        assertThat(process.getViabilityResetOutcomes()).isEmpty();
    }

    @Test
    public void shouldAddOutcomeWhenPresent() {
        ViabilityProcess process = new ViabilityProcess(newProjectUser().build(), newPartnerOrganisation().build(), ViabilityState.REVIEW);
        ViabilityResetOutcome outcome = new ViabilityResetOutcome();
        StateContext<ViabilityState, ViabilityEvent> context = setupContext(process, outcome);

        action.doExecute(context);

        assertThat(process.getViabilityResetOutcomes()).hasSize(1);
        assertThat(process.getViabilityResetOutcomes()).containsExactly(outcome);
    }

    private StateContext<ViabilityState, ViabilityEvent> setupContext(ViabilityProcess process) {
        StateContext<ViabilityState, ViabilityEvent> context = mock(StateContext.class);
        when(context.getMessageHeader("process")).thenReturn(process);
        return context;
    }

    private StateContext<ViabilityState, ViabilityEvent> setupContext(ViabilityProcess process, ViabilityResetOutcome outcome) {
        StateContext<ViabilityState, ViabilityEvent> context = setupContext(process);
        when(context.getMessageHeader("reset")).thenReturn(outcome);
        return context;
    }
}
