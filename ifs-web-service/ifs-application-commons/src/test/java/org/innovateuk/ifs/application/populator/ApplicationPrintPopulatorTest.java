package org.innovateuk.ifs.application.populator;

import org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings;
import org.innovateuk.ifs.application.readonly.populator.ApplicationReadOnlyViewModelPopulator;
import org.innovateuk.ifs.application.readonly.viewmodel.ApplicationReadOnlyViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.interview.service.InterviewAssignmentRestService;
import org.innovateuk.ifs.user.builder.UserResourceBuilder;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.omg.CORBA.PRIVATE_MEMBER;
import org.springframework.ui.Model;

import java.util.Arrays;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings.defaultSettings;
import static org.innovateuk.ifs.commons.rest.RestResult.fromException;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ApplicationPrintPopulatorTest {

    @InjectMocks
    private ApplicationPrintPopulator applicationPrintPopulator;

    @Mock
    private ApplicationReadOnlyViewModelPopulator applicationReadOnlyViewModelPopulator;

    @Mock
    private ApplicationRestService applicationRestService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private InterviewAssignmentRestService interviewAssignmentRestService;

    @Captor
    private ArgumentCaptor<ApplicationReadOnlySettings> settingsArgumentCaptor = ArgumentCaptor.forClass(ApplicationReadOnlySettings.class);

    @Test
    public void testPrint() {
        Model model = mock(Model.class);
        UserResource user = UserResourceBuilder.newUserResource()
                .withRoleGlobal(Role.ASSESSOR)
                .build();
        long applicationId = 1L;
        ApplicationReadOnlyViewModel viewModel = mock(ApplicationReadOnlyViewModel.class);
        CompetitionResource competition = newCompetitionResource()
                .withFundingType(FundingType.GRANT)
                .withCompetitionStatus(CompetitionStatus.PREVIOUS)
                .build();

        ApplicationResource application = newApplicationResource()
                .withId(applicationId)
                .withCompetition(competition.getId())
                .withApplicationState(ApplicationState.SUBMITTED)
                .build();

        when(applicationRestService.getApplicationById(applicationId)).thenReturn(restSuccess(application));
        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(interviewAssignmentRestService.isAssignedToInterview(applicationId)).thenReturn(restSuccess(false));
        when(applicationReadOnlyViewModelPopulator.populate(eq(applicationId), eq(user), any(ApplicationReadOnlySettings.class)))
                .thenReturn(viewModel);

        applicationPrintPopulator.print(applicationId, model, user);

        verify(model).addAttribute("model", viewModel);
        verify(applicationReadOnlyViewModelPopulator).populate(eq(applicationId), eq(user), settingsArgumentCaptor.capture());

        ApplicationReadOnlySettings settings = settingsArgumentCaptor.getValue();
        assertNotNull(settings);
        assertTrue(settings.isIncludeAllAssessorFeedback());
        assertFalse(settings.isIncludeAllSupporterFeedback());
    }

    @Test
    public void testPrintKta() {
        Model model = mock(Model.class);
        UserResource user = UserResourceBuilder.newUserResource()
                .withRolesGlobal(Arrays.asList(Role.KNOWLEDGE_TRANSFER_ADVISER, Role.ASSESSOR))
                .build();
        long applicationId = 1L;
        ApplicationReadOnlyViewModel viewModel = mock(ApplicationReadOnlyViewModel.class);
        CompetitionResource competition = newCompetitionResource()
                .withFundingType(FundingType.KTP)
                .withCompetitionStatus(CompetitionStatus.PROJECT_SETUP)
                .build();

        ApplicationResource application = newApplicationResource()
                .withId(applicationId)
                .withCompetition(competition.getId())
                .withApplicationState(ApplicationState.SUBMITTED)
                .build();

        when(applicationRestService.getApplicationById(applicationId)).thenReturn(restSuccess(application));
        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(interviewAssignmentRestService.isAssignedToInterview(applicationId)).thenReturn(restSuccess(false));
        when(applicationReadOnlyViewModelPopulator.populate(eq(applicationId), eq(user), any(ApplicationReadOnlySettings.class)))
                .thenReturn(viewModel);

        applicationPrintPopulator.print(applicationId, model, user);

        verify(model).addAttribute("model", viewModel);
        verify(applicationReadOnlyViewModelPopulator).populate(eq(applicationId), eq(user), settingsArgumentCaptor.capture());

        ApplicationReadOnlySettings settings = settingsArgumentCaptor.getValue();
        assertNotNull(settings);
        assertTrue(settings.isIncludeAllAssessorFeedback());
        assertTrue(settings.isIncludeAllSupporterFeedback());
    }
}
