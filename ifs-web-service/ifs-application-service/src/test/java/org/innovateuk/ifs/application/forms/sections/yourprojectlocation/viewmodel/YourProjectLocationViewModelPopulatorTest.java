package org.innovateuk.ifs.application.forms.sections.yourprojectlocation.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.application.service.SectionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static com.google.common.primitives.Longs.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class YourProjectLocationViewModelPopulatorTest {

    @InjectMocks
    private YourProjectLocationViewModelPopulator populator;

    @Mock
    private ApplicationRestService applicationRestService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private SectionService sectionService;

    @Test
    public void populate() {

        long organisationId = 123L;
        long sectionId = 789L;
        boolean internalUser = false;
        List<Long> sectionsMarkedAsComplete = asList(111L, sectionId, 333L);
        ApplicationState applicationState = ApplicationState.OPEN;
        CompetitionStatus competitionState = CompetitionStatus.OPEN;

        boolean expectedComplete = true;
        boolean expectedOpen = true;
        boolean expectedReadonly = true;

        assertViewModelPopulatedOk(
                organisationId,
                sectionId,
                internalUser,
                sectionsMarkedAsComplete,
                applicationState,
                competitionState,
                expectedComplete,
                expectedOpen,
                expectedReadonly);
    }

    @Test
    public void populateCompetitionClosed() {

        long organisationId = 123L;
        long sectionId = 789L;
        boolean internalUser = false;
        List<Long> sectionsMarkedAsComplete = asList(111L, sectionId, 333L);
        ApplicationState applicationState = ApplicationState.OPEN;
        CompetitionStatus competitionState = CompetitionStatus.CLOSED;

        boolean expectedComplete = true;
        boolean expectedOpen = false;
        boolean expectedReadonly = true;

        assertViewModelPopulatedOk(
                organisationId,
                sectionId,
                internalUser,
                sectionsMarkedAsComplete,
                applicationState,
                competitionState,
                expectedComplete,
                expectedOpen,
                expectedReadonly);
    }

    private void assertViewModelPopulatedOk(
            long organisationId,
            long sectionId,
            boolean internalUser,
            List<Long> sectionsMarkedAsComplete,
            ApplicationState applicationState,
            CompetitionStatus competitionStatus,
            boolean expectedComplete,
            boolean expectedOpen,
            boolean expectedReadonly) {

        CompetitionResource competition = newCompetitionResource().
                withCompetitionStatus(competitionStatus).
                build();

        ApplicationResource application = newApplicationResource().
                withCompetition(competition.getId()).
                withName("Lovely application").
                withApplicationState(applicationState).
                build();

        when(applicationRestService.getApplicationById(application.getId())).thenReturn(restSuccess(application));
        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(sectionService.getCompleted(application.getId(), organisationId)).thenReturn(sectionsMarkedAsComplete);

        YourProjectLocationViewModel viewModel = populator.populate(organisationId, application.getId(), sectionId, internalUser);

        assertThat(viewModel.getApplicationId()).isEqualTo(application.getId());
        assertThat(viewModel.getApplicationName()).isEqualTo(application.getName());
        assertThat(viewModel.getFinancesUrl()).isEqualTo("/application/" + application.getId() + "/form/FINANCE");
        assertThat(viewModel.getSectionId()).isEqualTo(sectionId);
        assertThat(viewModel.isComplete()).isEqualTo(expectedComplete);
        assertThat(viewModel.isOpen()).isEqualTo(expectedOpen);
        assertThat(viewModel.isReadOnly()).isEqualTo(expectedReadonly);
    }
}
