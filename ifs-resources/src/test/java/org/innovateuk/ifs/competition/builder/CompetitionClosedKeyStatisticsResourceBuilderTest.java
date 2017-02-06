package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.competition.resource.CompetitionClosedKeyStatisticsResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.competition.builder.CompetitionClosedKeyStatisticsResourceBuilder.newCompetitionClosedKeyStatisticsResource;
import static org.junit.Assert.assertEquals;

public class CompetitionClosedKeyStatisticsResourceBuilderTest {

    @Test
    public void buildOne() {

        long expectedAssessorsInvited = 1L;
        long expectedAssessorsAccepted = 2L;
        long expectedApplicationsPerAssessor = 3L;
        long expectedApplicationsRequiringAssessors = 4L;
        long expectedAssessorsWithoutApplications = 5L;
        long expectedAssignmentCount = 6L;

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
        Long[] expectedAssessorsInviteds = {1L, 11L};
        Long[] expectedAssessorsAccepteds = {2L, 12L};
        Long[] expectedApplicationsPerAssessors = {3L, 13L};
        Long[] expectedApplicationsRequiringAssessorss = {4L, 14L};
        Long[] expectedAssessorsWithoutApplicationss = {5L, 15L};
        Long[] expectedAssignmentCounts = {6L, 16L};

        List<CompetitionClosedKeyStatisticsResource> keyStatisticsResources = newCompetitionClosedKeyStatisticsResource()
                .withAssessorsInvited(expectedAssessorsInviteds)
                .withAssessorsAccepted(expectedAssessorsAccepteds)
                .withApplicationsPerAssessor(expectedApplicationsPerAssessors)
                .withApplicationsRequiringAssessors(expectedApplicationsRequiringAssessorss)
                .withAssessorsWithoutApplications(expectedAssessorsWithoutApplicationss)
                .withAssignmentCount(expectedAssignmentCounts)
                .build(2);

        for (int i = 0; i < keyStatisticsResources.size(); i++) {
            assertEquals((long) expectedAssessorsInviteds[i], keyStatisticsResources.get(i).getAssessorsInvited());
            assertEquals((long) expectedAssessorsAccepteds[i], keyStatisticsResources.get(i).getAssessorsAccepted());
            assertEquals((long) expectedApplicationsPerAssessors[i], keyStatisticsResources.get(i).getApplicationsPerAssessor());
            assertEquals((long) expectedApplicationsRequiringAssessorss[i], keyStatisticsResources.get(i).getApplicationsRequiringAssessors());
            assertEquals((long) expectedAssessorsWithoutApplicationss[i], keyStatisticsResources.get(i).getAssessorsWithoutApplications());
            assertEquals((long) expectedAssignmentCounts[i], keyStatisticsResources.get(i).getAssignmentCount());
        }

    }

}
