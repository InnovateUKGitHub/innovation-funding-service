package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.assessment.resource.CompetitionInAssessmentKeyAssessmentStatisticsResource;
import org.innovateuk.ifs.assessment.service.CompetitionKeyAssessmentStatisticsRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competition.service.MilestoneRestService;
import org.innovateuk.ifs.management.assessment.populator.ManageAssessmentsModelPopulator;
import org.innovateuk.ifs.management.assessment.viewmodel.ManageAssessmentsViewModel;
import org.innovateuk.ifs.management.assessmentperiod.populator.AssessmentPeriodFormPopulator;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.assessment.builder.CompetitionInAssessmentKeyAssessmentStatisticsResourceBuilder.newCompetitionInAssessmentKeyAssessmentStatisticsResource;
import static org.innovateuk.ifs.commons.resource.PageResource.fromListZeroBased;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class ManageAssessmentsModelPopulatorTest extends BaseUnitTest {

    @InjectMocks
    @Spy
    private ManageAssessmentsModelPopulator manageAssessmentsModelPopulator;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private CompetitionKeyAssessmentStatisticsRestService competitionKeyAssessmentStatisticsRestService;

    @Mock
    private AssessmentPeriodFormPopulator assessmentPeriodFormPopulator;


    @Mock
    private MilestoneRestService milestoneRestService;

    @Test
    public void populateModel() {

        final String expectedCompetitionName = "Test Competition";
        final CompetitionStatus expectedCompetitionStatus = CompetitionStatus.IN_ASSESSMENT;

        CompetitionResource competitionResource = newCompetitionResource()
                .withName(expectedCompetitionName)
                .withCompetitionStatus(expectedCompetitionStatus)
                .build();
        CompetitionInAssessmentKeyAssessmentStatisticsResource statisticsResource = newCompetitionInAssessmentKeyAssessmentStatisticsResource()
                .withAssignmentCount(2)
                .withAssignmentsWaiting(3)
                .withAssignmentsAccepted(5)
                .withAssessmentsStarted(7)
                .withAssessmentsSubmitted(11)
                .build();

        when(competitionRestService.getCompetitionById(competitionResource.getId())).thenReturn(restSuccess(competitionResource));
        when(competitionKeyAssessmentStatisticsRestService.getInAssessmentKeyStatisticsByCompetition(competitionResource.getId())).thenReturn(restSuccess(statisticsResource));
        when(milestoneRestService.getAllMilestonesByCompetitionId(competitionResource.getId())).thenReturn(restSuccess(emptyList()));
        ManageAssessmentsViewModel expectedModel = new ManageAssessmentsViewModel(competitionResource, statisticsResource, fromListZeroBased(emptyList(), 0, 2));


        ManageAssessmentsViewModel actualModel = manageAssessmentsModelPopulator.populateModel(competitionResource.getId(), 0, 2);

        assertEquals(expectedModel, actualModel);
    }
}