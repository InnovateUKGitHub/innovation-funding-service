package org.innovateuk.ifs.management.supporters.populator;

import org.innovateuk.ifs.supporter.resource.ApplicationsForCofundingPageResource;
import org.innovateuk.ifs.supporter.service.SupporterAssignmentRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.supporters.viewmodel.AllocateSupportersViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class AllocateSupportersViewModelPopulatorTest {

    @InjectMocks
    private AllocateSupportersViewModelPopulator populator;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private SupporterAssignmentRestService supporterAssignmentRestService;

    @Test
    public void populate() {
        // given
        long competitionId = 5L;
        String competitionName = "my comp";
        String filter = "w";
        int page = 1;

        CompetitionResource competition = newCompetitionResource().withId(competitionId).withName(competitionName).build();
        given(competitionRestService.getCompetitionById(competitionId)).willReturn(restSuccess(competition));

        ApplicationsForCofundingPageResource applicationsNeedingSupporters = new ApplicationsForCofundingPageResource();
        given(supporterAssignmentRestService.findApplicationsNeedingSupporters(competitionId, filter, 0)).willReturn(restSuccess(applicationsNeedingSupporters));

        // when
        AllocateSupportersViewModel result = populator.populateModel(competitionId, filter, page);

        // then
        assertThat(result.getApplicationsPage()).isEqualTo(applicationsNeedingSupporters);
        assertThat(result.getCompetitionId()).isEqualTo(competitionId);
        assertThat(result.getCompetitionName()).isEqualTo(competitionName);
        assertThat(result.getFilter()).isEqualTo(filter);
    }

}
