package org.innovateuk.ifs.management.model;

import org.assertj.core.util.Lists;
import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.assessment.resource.CompetitionInAssessmentKeyAssessmentStatisticsResource;
import org.innovateuk.ifs.assessment.service.CompetitionKeyAssessmentStatisticsRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.assessment.populator.ManageAssessmentsModelPopulator;
import org.innovateuk.ifs.management.assessment.viewmodel.ManageAssessmentsViewModel;
import org.innovateuk.ifs.management.assessmentperiod.form.AssessmentPeriodForm;
import org.innovateuk.ifs.management.assessmentperiod.form.ManageAssessmentPeriodsForm;
import org.innovateuk.ifs.management.assessmentperiod.populator.AssessmentPeriodFormPopulator;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import java.util.List;

import static org.innovateuk.ifs.assessment.builder.CompetitionInAssessmentKeyAssessmentStatisticsResourceBuilder.newCompetitionInAssessmentKeyAssessmentStatisticsResource;
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

    @Test
    public void populateModel() throws Exception {
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

        ManageAssessmentPeriodsForm form = new ManageAssessmentPeriodsForm();
        AssessmentPeriodForm period = new AssessmentPeriodForm();
        List<AssessmentPeriodForm> periods = Lists.newArrayList(period);
        form.setAssessmentPeriods(periods);
        when(competitionRestService.getCompetitionById(expectedCompetitionId)).thenReturn(restSuccess(competitionResource));
        when(competitionKeyAssessmentStatisticsRestService.getInAssessmentKeyStatisticsByCompetition(expectedCompetitionId)).thenReturn(restSuccess(statisticsResource));
        when(assessmentPeriodFormPopulator.populate(expectedCompetitionId)).thenReturn(form);
        ManageAssessmentsViewModel expectedModel = new ManageAssessmentsViewModel(competitionResource, statisticsResource, periods, assessmentPeriodPagination);

        ManageAssessmentsViewModel actualModel = manageAssessmentsModelPopulator.populateModel(expectedCompetitionId);

        assertEquals(expectedModel, actualModel);
    }
}