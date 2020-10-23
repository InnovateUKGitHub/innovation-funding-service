package org.innovateuk.ifs.management.supporters.populator;

import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.supporters.viewmodel.ManageSupportersViewModel;
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
public class ManageSupportersViewModelPopulatorTest {

    @InjectMocks
    private ManageSupportersViewModelPopulator populator;

    @Mock
    private CompetitionRestService competitionRestService;

    @Test
    public void populate() {
        // given
        long competitionId = 5L;
        String competitionName = "my comp";

        CompetitionResource competition = newCompetitionResource().withId(competitionId).withName(competitionName).build();
        given(competitionRestService.getCompetitionById(competitionId)).willReturn(restSuccess(competition));

        // when
        ManageSupportersViewModel result = populator.populateModel(competitionId);

        // then
        assertThat(result.getCompetitionId()).isEqualTo(competitionId);
        assertThat(result.getCompetitionName()).isEqualTo(competitionName);
    }

}
