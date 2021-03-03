package org.innovateuk.ifs.application.workflow.actions;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.resource.ApplicationEvent;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.transactional.AutoCompleteSectionsUtil;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.user.resource.ProcessRoleType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.statemachine.StateContext;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.CompetitionTypeBuilder.newCompetitionType;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AutoCompleteSectionsActionTest {

    @Mock
    private AutoCompleteSectionsUtil autoCompleteSectionsUtil;

    @InjectMocks
    private AutoCompleteSectionsAction autoCompleteSectionsAction;

    @Test
    public void doExecute() {
        ProcessRole processRole = newProcessRole().withRole(ProcessRoleType.LEADAPPLICANT).withOrganisationId(1L).build();
        Application application = newApplication()
                .withCompetition(newCompetition()
                        .withCompetitionType(newCompetitionType().withName("Expression of interest").build())
                        .build())
                .withProcessRole(processRole)
                .build();
        StateContext<ApplicationState, ApplicationEvent> stateContext = mock(StateContext.class);

        autoCompleteSectionsAction.doExecute(application, stateContext);

        verify(autoCompleteSectionsUtil).intitialiseCompleteSectionsForOrganisation(application, processRole.getOrganisationId(), processRole.getId());
    }
}