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
import org.innovateuk.ifs.management.assessmentperiod.model.AssessmentPeriodViewModel;
import org.innovateuk.ifs.management.assessmentperiod.populator.AssessmentPeriodFormPopulator;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.assessment.builder.CompetitionInAssessmentKeyAssessmentStatisticsResourceBuilder.newCompetitionInAssessmentKeyAssessmentStatisticsResource;
import static org.innovateuk.ifs.commons.resource.PageResource.fromList;
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
        final long expectedCompetitionId = 13;
        final String expectedCompetitionName = "Test Competition";
        final CompetitionStatus expectedCompetitionStatus = CompetitionStatus.IN_ASSESSMENT;
        final int expectedAssignmentCount = 2;
        final int expectedAssignmentsWaiting = 3;
        final int expectedAssignmentsAccepted = 5;
        final int expectedAssessmentsStarted = 7;
        final int expectedAssessmentsSubmitted = 11;

        CompetitionResource competitionResource = newCompetitionResource()
                .withId(expectedCompetitionId)
                .withName(expectedCompetitionName)
                .withCompetitionStatus(expectedCompetitionStatus)
                .build();
        CompetitionInAssessmentKeyAssessmentStatisticsResource statisticsResource = newCompetitionInAssessmentKeyAssessmentStatisticsResource()
                .withAssignmentCount(expectedAssignmentCount)
                .withAssignmentsWaiting(expectedAssignmentsWaiting)
                .withAssignmentsAccepted(expectedAssignmentsAccepted)
                .withAssessmentsStarted(expectedAssessmentsStarted)
                .withAssessmentsSubmitted(expectedAssessmentsSubmitted)
                .build();

        when(competitionRestService.getCompetitionById(expectedCompetitionId)).thenReturn(restSuccess(competitionResource));
        when(competitionKeyAssessmentStatisticsRestService.getInAssessmentKeyStatisticsByCompetition(expectedCompetitionId)).thenReturn(restSuccess(statisticsResource));
        when(milestoneRestService.getAllMilestonesByCompetitionId(competitionResource.getId())).thenReturn(restSuccess(emptyList()));
        ManageAssessmentsViewModel expectedModel = new ManageAssessmentsViewModel(competitionResource, statisticsResource, fromList(emptyList(), 0, 2));

        ManageAssessmentsViewModel actualModel = manageAssessmentsModelPopulator.populateModel(expectedCompetitionId, 0, 2);

        assertEquals(expectedModel, actualModel);
    }


    public void populateModelAlwayOpen() {
        // TODO qqRP
    }
}