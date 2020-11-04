package org.innovateuk.ifs.management.supporters.populator;

import org.innovateuk.ifs.supporter.resource.ApplicationsForCofundingPageResource;
import org.innovateuk.ifs.supporter.service.SupporterAssignmentRestService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.supporters.viewmodel.ViewSupportersViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ViewSupporterViewModelPopulatorTest {

    @InjectMocks
    private ViewSupporterViewModelPopulator populator;

    @Mock
    private SupporterAssignmentRestService supporterAssignmentRestService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Test
    public void populate() {
        long competitionId = 5L;
        String competitionName = "my competition";
        String filter = "filter";
        int page = 1;

        CompetitionResource competition = CompetitionResourceBuilder.newCompetitionResource().withId(competitionId).withName(competitionName).build();
        Mockito.when(competitionRestService.getCompetitionById(competitionId)).thenReturn(RestResult.restSuccess(competition));

        ApplicationsForCofundingPageResource pageResource = new ApplicationsForCofundingPageResource();
        Mockito.when(supporterAssignmentRestService.findApplicationsNeedingSupporters(competitionId, filter, page - 1)).thenReturn(RestResult.restSuccess(pageResource));

        ViewSupportersViewModel result = populator.populateModel(competitionId, filter, page);

        assertEquals(competitionId, result.getCompetitionId());
        assertEquals(competitionName, result.getCompetitionName());
        verify(competitionRestService).getCompetitionById(competitionId);
        verify(supporterAssignmentRestService).findApplicationsNeedingSupporters(competitionId, filter, page - 1);
    }
}