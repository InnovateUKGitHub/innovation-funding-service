package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.competition.resource.CompetitionOpenKeyStatisticsResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.competition.builder.CompetitionOpenKeyStatisticsResourceBuilder.newCompetitionOpenKeyStatisticsResource;
import static org.junit.Assert.assertEquals;

public class CompetitionOpenKeyStatisticsResourceBuilderTest {

    @Test
    public void buildOne() {

        int expectedAssessorsInvited = 1;
        int expectedAssessorsAccepted = 2;
        int expectedApplicationsPerAssessor = 3;
        int expectedApplicationsStarted = 4;
        int expectedApplicationsPastHalf = 5;
        int expectedApplicationsSubmitted = 6;

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
        Integer[] expectedAssessorsInviteds = {1, 11};
        Integer[] expectedAssessorsAccepteds = {2, 12};
        Integer[] expectedApplicationsPerAssessors = {3, 13};
        Integer[] expectedApplicationsStarteds = {4, 14};
        Integer[] expectedApplicationsPastHalfs = {5, 15};
        Integer[] expectedApplicationsSubmitteds = {6, 16};

        List<CompetitionOpenKeyStatisticsResource> keyStatisticsResources = newCompetitionOpenKeyStatisticsResource()
                .withAssessorsInvited(expectedAssessorsInviteds)
                .withAssessorsAccepted(expectedAssessorsAccepteds)
                .withApplicationsPerAssessor(expectedApplicationsPerAssessors)
                .withApplicationsStarted(expectedApplicationsStarteds)
                .withApplicationsPastHalf(expectedApplicationsPastHalfs)
                .withApplicationsSubmitted(expectedApplicationsSubmitteds)
                .build(2);

        for (int i = 0; i < keyStatisticsResources.size(); i++) {
            assertEquals((int) expectedAssessorsInviteds[i], keyStatisticsResources.get(i).getAssessorsInvited());
            assertEquals((int) expectedAssessorsAccepteds[i], keyStatisticsResources.get(i).getAssessorsAccepted());
            assertEquals((int) expectedApplicationsPerAssessors[i], keyStatisticsResources.get(i).getApplicationsPerAssessor());
            assertEquals((int) expectedApplicationsStarteds[i], keyStatisticsResources.get(i).getApplicationsStarted());
            assertEquals((int) expectedApplicationsPastHalfs[i], keyStatisticsResources.get(i).getApplicationsPastHalf());
            assertEquals((int) expectedApplicationsSubmitteds[i], keyStatisticsResources.get(i).getApplicationsSubmitted());
        }

    }

}
