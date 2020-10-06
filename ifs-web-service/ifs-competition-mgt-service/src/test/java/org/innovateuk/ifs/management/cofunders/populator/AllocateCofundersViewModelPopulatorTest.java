package org.innovateuk.ifs.management.cofunders.populator;

import org.innovateuk.ifs.cofunder.resource.ApplicationsForCofundingPageResource;
import org.innovateuk.ifs.cofunder.service.CofunderAssignmentRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.cofunders.viewmodel.AllocateCofundersViewModel;
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
public class AllocateCofundersViewModelPopulatorTest {

    @InjectMocks
    private AllocateCofundersViewModelPopulator populator;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private CofunderAssignmentRestService cofunderAssignmentRestService;

    @Test
    public void populate() {
        // given
        long competitionId = 5L;
        String competitionName = "my comp";
        String filter = "w";
        int page = 1;

        CompetitionResource competition = newCompetitionResource().withId(competitionId).withName(competitionName).build();
        given(competitionRestService.getCompetitionById(competitionId)).willReturn(restSuccess(competition));

        ApplicationsForCofundingPageResource applicationsNeedingCofunders = new ApplicationsForCofundingPageResource();
        given(cofunderAssignmentRestService.findApplicationsNeedingCofunders(competitionId, filter, 0)).willReturn(restSuccess(applicationsNeedingCofunders));

        // when
        AllocateCofundersViewModel result = populator.populateModel(competitionId, filter, page);

        // then
        assertThat(result.getApplicationsPage()).isEqualTo(applicationsNeedingCofunders);
        assertThat(result.getCompetitionId()).isEqualTo(competitionId);
        assertThat(result.getCompetitionName()).isEqualTo(competitionName);
        assertThat(result.getFilter()).isEqualTo(filter);
    }

}
