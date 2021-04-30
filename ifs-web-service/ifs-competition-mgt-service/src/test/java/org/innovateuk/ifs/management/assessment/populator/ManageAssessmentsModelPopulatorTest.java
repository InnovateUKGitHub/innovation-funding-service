package org.innovateuk.ifs.management.assessment.populator;

import org.innovateuk.ifs.assessment.resource.CompetitionInAssessmentKeyAssessmentStatisticsResource;
import org.innovateuk.ifs.assessment.service.AssessmentRestService;
import org.innovateuk.ifs.assessment.service.CompetitionKeyAssessmentStatisticsRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.MilestoneResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.competition.service.MilestoneRestService;
import org.innovateuk.ifs.management.assessment.viewmodel.ManageAssessmentsViewModel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.time.ZonedDateTime.now;
import static java.util.stream.Collectors.toList;
import static org.innovateuk.ifs.assessment.builder.CompetitionInAssessmentKeyAssessmentStatisticsResourceBuilder.newCompetitionInAssessmentKeyAssessmentStatisticsResource;
import static org.innovateuk.ifs.assessment.resource.AssessmentState.CREATED;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.builder.MilestoneResourceBuilder.newMilestoneResource;
import static org.innovateuk.ifs.competition.resource.MilestoneType.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class ManageAssessmentsModelPopulatorTest {

    @InjectMocks
    private ManageAssessmentsModelPopulator manageAssessmentsModelPopulator;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private CompetitionKeyAssessmentStatisticsRestService competitionKeyAssessmentStatisticsRestService;

    @Mock
    private MilestoneRestService milestoneRestService;

    @Mock
    private AssessmentRestService assessmentRestService;

    @Test
    public void testManageAssessmentsModelPopulator(){
        // Set up
        long assessmentPeriod1Id = 1;
        long assessmentPeriod2Id = 2;
        long assessmentPeriod3Id = 3;
        long assessmentPeriod4Id = 4;
        List<MilestoneResource> milestones =
        newArrayList(newMilestoneResource()
                        .withAssessmentPeriod(assessmentPeriod1Id)
                        .withType(ASSESSORS_NOTIFIED, ASSESSOR_BRIEFING, ASSESSOR_ACCEPTS, ASSESSOR_DEADLINE, ASSESSMENT_CLOSED) // Have been notified and closed
                        .withDate(now().minusDays(21), now().minusDays(20), now().minusDays(19), now().minusDays(18), now().minusDays(17))
                        .build(5),
                newMilestoneResource()
                        .withAssessmentPeriod(assessmentPeriod2Id)
                        .withType(ASSESSORS_NOTIFIED, ASSESSOR_BRIEFING, ASSESSOR_ACCEPTS, ASSESSOR_DEADLINE) // Have been notified once and there is not notification
                        .withDate(now().minusDays(16), now().minusDays(15), now().minusDays(14))
                        .build(4),
                newMilestoneResource()
                        .withAssessmentPeriod(assessmentPeriod3Id)
                        .withType(ASSESSOR_BRIEFING, ASSESSOR_ACCEPTS, ASSESSOR_DEADLINE) // No notifications
                        .withDate(now().minusDays(13), now().minusDays(12), now().minusDays(11))
                        .build(3),
                newMilestoneResource()
                        .withAssessmentPeriod(assessmentPeriod4Id)
                        .withType(ASSESSOR_BRIEFING, ASSESSOR_ACCEPTS, ASSESSOR_DEADLINE) // Not shown on the first page
                        .withDate(now().minusDays(10), now().minusDays(9), now().minusDays(8))
                        .build(3))
                        .stream().flatMap(List::stream).collect(toList());
        CompetitionResource competition = newCompetitionResource().build();
        CompetitionInAssessmentKeyAssessmentStatisticsResource competitionInAssessmentKeyAssessmentStatisticsResource = newCompetitionInAssessmentKeyAssessmentStatisticsResource().build();
        when(competitionRestService.getCompetitionById(competition.getId())).thenReturn(restSuccess(competition));
        when(competitionKeyAssessmentStatisticsRestService.getInAssessmentKeyStatisticsByCompetition(competition.getId())).thenReturn(restSuccess(competitionInAssessmentKeyAssessmentStatisticsResource));
        when(milestoneRestService.getAllMilestonesByCompetitionId(competition.getId())).thenReturn(restSuccess(milestones));
        when(assessmentRestService.countByStateAndAssessmentPeriod(CREATED, assessmentPeriod1Id)).thenReturn(restSuccess(0l)); // no notification
        when(assessmentRestService.countByStateAndAssessmentPeriod(CREATED, assessmentPeriod2Id)).thenReturn(restSuccess(0l)); // no notification
        when(assessmentRestService.countByStateAndAssessmentPeriod(CREATED, assessmentPeriod3Id)).thenReturn(restSuccess(1l)); // one outstanding  notification
        when(assessmentRestService.countByStateAndAssessmentPeriod(CREATED, assessmentPeriod4Id)).thenReturn(restSuccess(0l)); // no notification
        // Method under test
        ManageAssessmentsViewModel manageAssessmentsViewModel = manageAssessmentsModelPopulator.populateModel(competition.getId(), 0, 3); // First page
        // Assertion
        assertEquals((long)competition.getId(), manageAssessmentsViewModel.getCompetitionId());
        assertEquals(3, manageAssessmentsViewModel.getAssessmentPeriods().size()); // First page
        assertEquals(assessmentPeriod1Id, (long)manageAssessmentsViewModel.getAssessmentPeriods().get(0).getAssessmentPeriodId());
        assertEquals(assessmentPeriod2Id, (long)manageAssessmentsViewModel.getAssessmentPeriods().get(1).getAssessmentPeriodId());
        assertEquals(assessmentPeriod3Id, (long)manageAssessmentsViewModel.getAssessmentPeriods().get(2).getAssessmentPeriodId());
        assertEquals(false, manageAssessmentsViewModel.getAssessmentPeriods().get(0).canCloseAssessment()); // Already closed
        assertEquals(true, manageAssessmentsViewModel.getAssessmentPeriods().get(1).canCloseAssessment()); // Closable
        assertEquals(false, manageAssessmentsViewModel.getAssessmentPeriods().get(2).canCloseAssessment()); // An outstanding notification
        assertEquals(true, manageAssessmentsViewModel.getAssessmentPeriods().get(2).canNotifyAssessors()); // An outstanding notification
    }
}
