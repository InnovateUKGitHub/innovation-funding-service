package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.competition.resource.CompetitionInAssessmentKeyStatisticsResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.competition.builder.CompetitionInAssessmentKeyStatisticsResourceBuilder.newCompetitionInAssessmentKeyStatisticsResource;
import static org.junit.Assert.assertEquals;

public class CompetitionInAssessmentKeyStatisticsResourceBuilderTest {

    @Test
    public void buildOne() {
        long expectedAssignmentCount = 1L;
        long expectedAssignmentsWaiting = 2L;
        long expectedAssignmentsAccepted = 3L;
        long expectedAssessmentsStarted = 4L;
        long expectedAssessmentsSubmitted = 5L;

        CompetitionInAssessmentKeyStatisticsResource keyStatisticsResource = newCompetitionInAssessmentKeyStatisticsResource()
                .withAssignmentCount(expectedAssignmentCount)
                .withAssignmentsWaiting(expectedAssignmentsWaiting)
                .withAssignmentsAccepted(expectedAssignmentsAccepted)
                .withAssessmentsStarted(expectedAssessmentsStarted)
                .withAssessmentsSubmitted(expectedAssessmentsSubmitted)
                .build();

        assertEquals(expectedAssignmentCount, keyStatisticsResource.getAssignmentCount());
        assertEquals(expectedAssignmentsWaiting, keyStatisticsResource.getAssignmentsWaiting());
        assertEquals(expectedAssignmentsAccepted, keyStatisticsResource.getAssignmentsAccepted());
        assertEquals(expectedAssessmentsStarted, keyStatisticsResource.getAssessmentsStarted());
        assertEquals(expectedAssessmentsSubmitted, keyStatisticsResource.getAssessmentsSubmitted());
    }

    @Test
    public void buildMany() {
        Long[] expectedAssignmentCounts = {1L, 1L};
        Long[] expectedAssignmentsWaitings = {2L, 1L};
        Long[] expectedAssignmentsAccepteds = {3L, 1L};
        Long[] expectedAssessmentsStarteds = {4L, 1L};
        Long[] expectedAssessmentsSubmitteds = {5L, 1L};

        List<CompetitionInAssessmentKeyStatisticsResource> keyStatisticsResources = newCompetitionInAssessmentKeyStatisticsResource()
                .withAssignmentCount(expectedAssignmentCounts)
                .withAssignmentsWaiting(expectedAssignmentsWaitings)
                .withAssignmentsAccepted(expectedAssignmentsAccepteds)
                .withAssessmentsStarted(expectedAssessmentsStarteds)
                .withAssessmentsSubmitted(expectedAssessmentsSubmitteds)
                .build(2);

        for (int i = 0; i < keyStatisticsResources.size(); i++) {
            assertEquals((long) expectedAssignmentCounts[i], keyStatisticsResources.get(i).getAssignmentCount());
            assertEquals((long) expectedAssignmentsWaitings[i], keyStatisticsResources.get(i).getAssignmentsWaiting());
            assertEquals((long) expectedAssignmentsAccepteds[i], keyStatisticsResources.get(i).getAssignmentsAccepted());
            assertEquals((long) expectedAssessmentsStarteds[i], keyStatisticsResources.get(i).getAssessmentsStarted());
            assertEquals((long) expectedAssessmentsSubmitteds[i], keyStatisticsResources.get(i).getAssessmentsSubmitted());
        }
    }

}
