package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.assessment.resource.CompetitionInAssessmentKeyAssessmentStatisticsResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.assessment.builder.CompetitionInAssessmentKeyAssessmentStatisticsResourceBuilder.newCompetitionInAssessmentKeyAssessmentStatisticsResource;
import static org.junit.Assert.assertEquals;

public class CompetitionInAssessmentKeyAssessmentStatisticsResourceBuilderTest {

    @Test
    public void buildOne() {
        int expectedAssignmentCount = 1;
        int expectedAssignmentsWaiting = 2;
        int expectedAssignmentsAccepted = 3;
        int expectedAssessmentsStarted = 4;
        int expectedAssessmentsSubmitted = 5;

        CompetitionInAssessmentKeyAssessmentStatisticsResource keyStatisticsResource =
                newCompetitionInAssessmentKeyAssessmentStatisticsResource()
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
        Integer[] expectedAssignmentCounts = {1, 1};
        Integer[] expectedAssignmentsWaitings = {2, 1};
        Integer[] expectedAssignmentsAccepteds = {3, 1};
        Integer[] expectedAssessmentsStarteds = {4, 1};
        Integer[] expectedAssessmentsSubmitteds = {5, 1};

        List<CompetitionInAssessmentKeyAssessmentStatisticsResource> keyStatisticsResources =
                newCompetitionInAssessmentKeyAssessmentStatisticsResource()
                .withAssignmentCount(expectedAssignmentCounts)
                .withAssignmentsWaiting(expectedAssignmentsWaitings)
                .withAssignmentsAccepted(expectedAssignmentsAccepteds)
                .withAssessmentsStarted(expectedAssessmentsStarteds)
                .withAssessmentsSubmitted(expectedAssessmentsSubmitteds)
                .build(2);

        for (int i = 0; i < keyStatisticsResources.size(); i++) {
            assertEquals((int) expectedAssignmentCounts[i], keyStatisticsResources.get(i).getAssignmentCount());
            assertEquals((int) expectedAssignmentsWaitings[i], keyStatisticsResources.get(i).getAssignmentsWaiting());
            assertEquals((int) expectedAssignmentsAccepteds[i], keyStatisticsResources.get(i).getAssignmentsAccepted());
            assertEquals((int) expectedAssessmentsStarteds[i], keyStatisticsResources.get(i).getAssessmentsStarted());
            assertEquals((int) expectedAssessmentsSubmitteds[i], keyStatisticsResources.get(i)
                    .getAssessmentsSubmitted());
        }
    }
}
