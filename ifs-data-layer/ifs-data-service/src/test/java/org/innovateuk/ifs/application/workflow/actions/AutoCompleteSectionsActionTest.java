package org.innovateuk.ifs.application.workflow.actions;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.application.resource.ApplicationEvent;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.transactional.SectionStatusService;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.form.transactional.SectionService;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.statemachine.StateContext;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionBuilder.newCompetition;
import static org.innovateuk.ifs.competition.builder.CompetitionTypeBuilder.newCompetitionType;
import static org.innovateuk.ifs.form.builder.SectionResourceBuilder.newSectionResource;
import static org.innovateuk.ifs.form.resource.SectionType.TERMS_AND_CONDITIONS;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AutoCompleteSectionsActionTest {

    @Mock
    private SectionService sectionService;

    @Mock
    private SectionStatusService sectionStatusService;

    @InjectMocks
    private AutoCompleteSectionsAction autoCompleteSectionsAction;

    @Test
    public void doExecute() {
        SectionResource termsSection = newSectionResource().build();
        Application application = newApplication()
                .withCompetition(newCompetition()
                        .withCompetitionType(newCompetitionType().withName("Expression of interest").build())
                        .build())
                .withProcessRole(newProcessRole().withRole(Role.LEADAPPLICANT).build())
                .build();
        StateContext<ApplicationState, ApplicationEvent> stateContext = mock(StateContext.class);

        when(sectionService.getSectionsByCompetitionIdAndType(application.getCompetition().getId(), TERMS_AND_CONDITIONS))
                .thenReturn(serviceSuccess(singletonList(termsSection)));

        autoCompleteSectionsAction.doExecute(application, stateContext);

        InOrder inOrder = inOrder(sectionService, sectionStatusService);
        inOrder.verify(sectionService).getSectionsByCompetitionIdAndType(application.getCompetition().getId(), TERMS_AND_CONDITIONS);
        inOrder.verify(sectionStatusService).markSectionAsComplete(termsSection.getId(), application.getId(), application.getLeadApplicantProcessRole().getId());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void doExecute_nonEoiCompetition() {
        SectionResource termsSection = newSectionResource().build();
        Application application = newApplication()
                .withCompetition(newCompetition()
                        .withCompetitionType(newCompetitionType().withName("Generic").build())
                        .build())
                .withProcessRole(newProcessRole().withRole(Role.LEADAPPLICANT).build())
                .build();
        StateContext<ApplicationState, ApplicationEvent> stateContext = mock(StateContext.class);

        autoCompleteSectionsAction.doExecute(application, stateContext);

        verifyZeroInteractions(sectionService, sectionStatusService);
    }
}