package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.assessment.resource.dashboard.ApplicationAssessmentResource;
import org.innovateuk.ifs.assessment.resource.dashboard.AssessorCompetitionDashboardResource;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.assessment.builder.ApplicationAssessmentResourceBuilder.newApplicationAssessmentResource;
import static org.innovateuk.ifs.assessment.builder.AssessorCompetitionDashboardResourceBuilder.newAssessorCompetitionDashboardResource;
import static org.junit.Assert.assertEquals;

public class AssessorCompetitionDashboardResourceBuilderTest {

    @Test
    public void buildOne() {
        long expectedCompetitionId = 1L;
        String expectedCompetitionName = "Competition Name";
        String expectedInnovationLead = "Innovation Lead";
        ZonedDateTime expectedAssessorAcceptDate = ZonedDateTime.now().minusDays(2);
        ZonedDateTime expectedAssessorDeadlineDate = ZonedDateTime.now().plusDays(2);

        List<ApplicationAssessmentResource> expectedAssessmentId = singletonList(newApplicationAssessmentResource()
                .withAssessmentId(1L)
                .build());

        AssessorCompetitionDashboardResource resource = newAssessorCompetitionDashboardResource()
                .withCompetitionId(expectedCompetitionId)
                .withCompetitionName(expectedCompetitionName)
                .withInnovationLead(expectedInnovationLead)
                .withAssessorAcceptDate(expectedAssessorAcceptDate)
                .withAssessorDeadlineDate(expectedAssessorDeadlineDate)
                .withApplicationAssessments(expectedAssessmentId)
                .build();

        assertEquals(expectedCompetitionId, resource.getCompetitionId());
        assertEquals(expectedCompetitionName, resource.getCompetitionName());
        assertEquals(expectedInnovationLead, resource.getInnovationLead());
        assertEquals(expectedAssessorAcceptDate, resource.getAssessorAcceptDate());
        assertEquals(expectedAssessorDeadlineDate, resource.getAssessorDeadlineDate());
        assertEquals(expectedAssessmentId, resource.getApplicationAssessments());
    }

    @Test
    public void buildMany() {
        Long[] expectedCompetitionIds = {1L, 2L};
        String[] expectedCompetitionNames = {"Competition 1", "Competition 2"};
        String[] expectedInnovationLeads = {"Innovation Lead 1", "Innovation Lead 2"};
        ZonedDateTime[] expectedAssessorAcceptDates = {ZonedDateTime.now().minusDays(2), ZonedDateTime.now().minusDays(3)};
        ZonedDateTime[] expectedAssessorDeadlineDates = {ZonedDateTime.now().plusDays(2), ZonedDateTime.now().plusDays(3)};
        List<ApplicationAssessmentResource> expectedAssessmentIds = newApplicationAssessmentResource()
                .withAssessmentId(1L, 2L)
                .build(2);


        List<AssessorCompetitionDashboardResource> resource = newAssessorCompetitionDashboardResource()
                .withCompetitionId(expectedCompetitionIds)
                .withCompetitionName(expectedCompetitionNames)
                .withInnovationLead(expectedInnovationLeads)
                .withAssessorAcceptDate(expectedAssessorAcceptDates)
                .withAssessorDeadlineDate(expectedAssessorDeadlineDates)
                .withApplicationAssessments(expectedAssessmentIds)
                .build(2);

        assertEquals(expectedCompetitionIds[0].longValue(), resource.get(0).getCompetitionId());
        assertEquals(expectedCompetitionNames[0], resource.get(0).getCompetitionName());
        assertEquals(expectedInnovationLeads[0], resource.get(0).getInnovationLead());
        assertEquals(expectedAssessorAcceptDates[0], resource.get(0).getAssessorAcceptDate());
        assertEquals(expectedAssessorDeadlineDates[0], resource.get(0).getAssessorDeadlineDate());
        assertEquals(expectedAssessmentIds, resource.get(0).getApplicationAssessments());
    }
}