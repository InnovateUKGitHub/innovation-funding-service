package org.innovateuk.ifs.application.populator;

import org.innovateuk.ifs.application.readonly.populator.ApplicationReadOnlyViewModelPopulator;
import org.innovateuk.ifs.application.readonly.viewmodel.ApplicationReadOnlyViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.user.builder.UserResourceBuilder;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.Model;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.application.readonly.ApplicationReadOnlySettings.defaultSettings;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
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

    @Test
    public void testPrint() {
        Model model = mock(Model.class);
        UserResource user = UserResourceBuilder.newUserResource().build();
        long applicationId = 1L;
        ApplicationReadOnlyViewModel viewModel = mock(ApplicationReadOnlyViewModel.class);
        CompetitionResource competition = newCompetitionResource().build();

        ApplicationResource application = newApplicationResource()
                .withId(applicationId)
                .withCompetition(competition.getId())
                .build();

        when(applicationRestService.getApplicationById(applicationId)).thenReturn(restSuccess(application));
        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));

        when(applicationReadOnlyViewModelPopulator.populate(applicationId, user, defaultSettings().setIncludeAllAssessorFeedback(true))).thenReturn(viewModel);

        applicationPrintPopulator.print(applicationId, model, user);

        verify(model).addAttribute("model", viewModel);
    }
}
