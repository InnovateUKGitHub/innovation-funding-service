package org.innovateuk.ifs.management.model;

import org.assertj.core.util.Lists;
import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.assessment.resource.CompetitionInAssessmentKeyAssessmentStatisticsResource;
import org.innovateuk.ifs.assessment.service.CompetitionKeyAssessmentStatisticsRestService;
import org.innovateuk.ifs.commons.resource.PageResource;
import org.innovateuk.ifs.competition.resource.AssessmentPeriodResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.service.AssessmentPeriodRestService;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.assessment.populator.ManageAssessmentsModelPopulator;
import org.innovateuk.ifs.management.assessment.viewmodel.ManageAssessmentsViewModel;
import org.innovateuk.ifs.management.assessmentperiod.form.AssessmentPeriodForm;
import org.innovateuk.ifs.management.assessmentperiod.form.ManageAssessmentPeriodsForm;
import org.innovateuk.ifs.management.assessmentperiod.populator.AssessmentPeriodFormPopulator;
import org.innovateuk.ifs.pagination.PaginationViewModel;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import java.util.Collections;
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

    @Mock
    private AssessmentPeriodRestService assessmentPeriodRestService;

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
        PageResource<AssessmentPeriodResource> periodPages = new PageResource<>(1, 1, Collections.emptyList(), 1, 1);
        when(assessmentPeriodRestService.getAssessmentPeriodByCompetitionId(expectedCompetitionId, 1, 3)).thenReturn(restSuccess(periodPages));
        when(assessmentPeriodFormPopulator.populate(expectedCompetitionId, periodPages)).thenReturn(form);
        ManageAssessmentsViewModel expectedModel = new ManageAssessmentsViewModel(competitionResource, statisticsResource, periods, new PaginationViewModel(periodPages));

        ManageAssessmentsViewModel actualModel = manageAssessmentsModelPopulator.populateModel(expectedCompetitionId, 1);

        assertEquals(expectedModel, actualModel);
    }
}