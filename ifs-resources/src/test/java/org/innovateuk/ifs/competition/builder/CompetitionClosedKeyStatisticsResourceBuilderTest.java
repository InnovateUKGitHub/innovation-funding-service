package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.competition.resource.CompetitionClosedKeyStatisticsResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.competition.builder.CompetitionClosedKeyStatisticsResourceBuilder.newCompetitionClosedKeyStatisticsResource;
import static org.junit.Assert.assertEquals;

public class CompetitionClosedKeyStatisticsResourceBuilderTest {

    @Test
    public void buildOne() {

        int expectedAssessorsInvited = 1;
        int expectedAssessorsAccepted = 2;
        int expectedApplicationsPerAssessor = 3;
        int expectedApplicationsRequiringAssessors = 4;
        int expectedAssessorsWithoutApplications = 5;
        int expectedAssignmentCount = 6;

        CompetitionClosedKeyStatisticsResource keyStatisticsResource = newCompetitionClosedKeyStatisticsResource()
                .withAssessorsInvited(expectedAssessorsInvited)
                .withAssessorsAccepted(expectedAssessorsAccepted)
                .withApplicationsPerAssessor(expectedApplicationsPerAssessor)
                .withApplicationsRequiringAssessors(expectedApplicationsRequiringAssessors)
                .withAssessorsWithoutApplications(expectedAssessorsWithoutApplications)
                .withAssignmentCount(expectedAssignmentCount)
                .build();

        assertEquals(expectedAssessorsInvited, keyStatisticsResource.getAssessorsInvited());
        assertEquals(expectedAssessorsAccepted, keyStatisticsResource.getAssessorsAccepted());
        assertEquals(expectedApplicationsPerAssessor, keyStatisticsResource.getApplicationsPerAssessor());
        assertEquals(expectedApplicationsRequiringAssessors, keyStatisticsResource.getApplicationsRequiringAssessors());
        assertEquals(expectedAssessorsWithoutApplications, keyStatisticsResource.getAssessorsWithoutApplications());
        assertEquals(expectedAssignmentCount, keyStatisticsResource.getAssignmentCount());
    }

    @Test
    public void buildMany() {
        Integer[] expectedAssessorsInviteds = {1, 11};
        Integer[] expectedAssessorsAccepteds = {2, 12};
        Integer[] expectedApplicationsPerAssessors = {3, 13};
        Integer[] expectedApplicationsRequiringAssessorss = {4, 14};
        Integer[] expectedAssessorsWithoutApplicationss = {5, 15};
        Integer[] expectedAssignmentCounts = {6, 16};

        List<CompetitionClosedKeyStatisticsResource> keyStatisticsResources = newCompetitionClosedKeyStatisticsResource()
                .withAssessorsInvited(expectedAssessorsInviteds)
                .withAssessorsAccepted(expectedAssessorsAccepteds)
                .withApplicationsPerAssessor(expectedApplicationsPerAssessors)
                .withApplicationsRequiringAssessors(expectedApplicationsRequiringAssessorss)
                .withAssessorsWithoutApplications(expectedAssessorsWithoutApplicationss)
                .withAssignmentCount(expectedAssignmentCounts)
                .build(2);

        for (int i = 0; i < keyStatisticsResources.size(); i++) {
            assertEquals((int) expectedAssessorsInviteds[i], keyStatisticsResources.get(i).getAssessorsInvited());
            assertEquals((int) expectedAssessorsAccepteds[i], keyStatisticsResources.get(i).getAssessorsAccepted());
            assertEquals((int) expectedApplicationsPerAssessors[i], keyStatisticsResources.get(i).getApplicationsPerAssessor());
            assertEquals((int) expectedApplicationsRequiringAssessorss[i], keyStatisticsResources.get(i).getApplicationsRequiringAssessors());
            assertEquals((int) expectedAssessorsWithoutApplicationss[i], keyStatisticsResources.get(i).getAssessorsWithoutApplications());
            assertEquals((int) expectedAssignmentCounts[i], keyStatisticsResources.get(i).getAssignmentCount());
        }

    }

}
