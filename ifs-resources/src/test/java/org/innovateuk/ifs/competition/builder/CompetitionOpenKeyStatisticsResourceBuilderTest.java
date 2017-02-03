package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.competition.resource.CompetitionOpenKeyStatisticsResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.competition.builder.CompetitionOpenKeyStatisticsResourceBuilder.newCompetitionOpenKeyStatisticsResource;
import static org.junit.Assert.assertEquals;

public class CompetitionOpenKeyStatisticsResourceBuilderTest {

    @Test
    public void buildOne() {

        long expectedAssessorsInvited = 1L;
        long expectedAssessorsAccepted = 2L;
        long expectedApplicationsPerAssessor = 3L;
        long expectedApplicationsStarted = 4L;
        long expectedApplicationsPastHalf = 5L;
        long expectedApplicationsSubmitted = 6L;

        CompetitionOpenKeyStatisticsResource keyStatisticsResource = newCompetitionOpenKeyStatisticsResource()
                .withAssessorsInvited(expectedAssessorsInvited)
                .withAssessorsAccepted(expectedAssessorsAccepted)
                .withApplicationsPerAssessor(expectedApplicationsPerAssessor)
                .withApplicationsStarted(expectedApplicationsStarted)
                .withApplicationsPastHalf(expectedApplicationsPastHalf)
                .withApplicationsSubmitted(expectedApplicationsSubmitted)
                .build();

        assertEquals(expectedAssessorsInvited, keyStatisticsResource.getAssessorsInvited());
        assertEquals(expectedAssessorsAccepted, keyStatisticsResource.getAssessorsAccepted());
        assertEquals(expectedApplicationsPerAssessor, keyStatisticsResource.getApplicationsPerAssessor());
        assertEquals(expectedApplicationsStarted, keyStatisticsResource.getApplicationsStarted());
        assertEquals(expectedApplicationsPastHalf, keyStatisticsResource.getApplicationsPastHalf());
        assertEquals(expectedApplicationsSubmitted, keyStatisticsResource.getApplicationsSubmitted());

    }

    @Test
    public void buildMany() {
        Long[] expectedAssessorsInviteds = {1L, 11L};
        Long[] expectedAssessorsAccepteds = {2L, 12L};
        Long[] expectedApplicationsPerAssessors = {3L, 13L};
        Long[] expectedApplicationsStarteds = {4L, 14L};
        Long[] expectedApplicationsPastHalfs = {5L, 15L};
        Long[] expectedApplicationsSubmitteds = {6L, 16L};

        List<CompetitionOpenKeyStatisticsResource> keyStatisticsResources = newCompetitionOpenKeyStatisticsResource()
                .withAssessorsInvited(expectedAssessorsInviteds)
                .withAssessorsAccepted(expectedAssessorsAccepteds)
                .withApplicationsPerAssessor(expectedApplicationsPerAssessors)
                .withApplicationsStarted(expectedApplicationsStarteds)
                .withApplicationsPastHalf(expectedApplicationsPastHalfs)
                .withApplicationsSubmitted(expectedApplicationsSubmitteds)
                .build(2);

        for (int i = 0; i < keyStatisticsResources.size(); i++) {
            assertEquals((long) expectedAssessorsInviteds[i], keyStatisticsResources.get(i).getAssessorsInvited());
            assertEquals((long) expectedAssessorsAccepteds[i], keyStatisticsResources.get(i).getAssessorsAccepted());
            assertEquals((long) expectedApplicationsPerAssessors[i], keyStatisticsResources.get(i).getApplicationsPerAssessor());
            assertEquals((long) expectedApplicationsStarteds[i], keyStatisticsResources.get(i).getApplicationsStarted());
            assertEquals((long) expectedApplicationsPastHalfs[i], keyStatisticsResources.get(i).getApplicationsPastHalf());
            assertEquals((long) expectedApplicationsSubmitteds[i], keyStatisticsResources.get(i).getApplicationsSubmitted());
        }

    }

}
