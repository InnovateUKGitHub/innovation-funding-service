package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.competition.resource.CompetitionClosedKeyApplicationStatisticsResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.competition.builder.CompetitionClosedKeyApplicationStatisticsResourceBuilder.newCompetitionClosedKeyApplicationStatisticsResource;
import static org.junit.Assert.assertEquals;

public class CompetitionClosedKeyApplicationStatisticsResourceBuilderTest {

    @Test
    public void buildOne() {
        int expectedApplicationsPerAssessor = 3;
        int expectedApplicationsRequiringAssessors = 4;
        int expectedAssignmentCount = 6;

        CompetitionClosedKeyApplicationStatisticsResource keyStatisticsResource =
                newCompetitionClosedKeyApplicationStatisticsResource()
                        .withApplicationsPerAssessor(expectedApplicationsPerAssessor)
                        .withApplicationsRequiringAssessors(expectedApplicationsRequiringAssessors)
                        .withAssignmentCount(expectedAssignmentCount)
                        .build();

        assertEquals(expectedApplicationsPerAssessor, keyStatisticsResource.getApplicationsPerAssessor());
        assertEquals(expectedApplicationsRequiringAssessors, keyStatisticsResource.getApplicationsRequiringAssessors());
        assertEquals(expectedAssignmentCount, keyStatisticsResource.getAssignmentCount());
    }

    @Test
    public void buildMany() {
        Integer[] expectedApplicationsPerAssessors = {3, 13};
        Integer[] expectedApplicationsRequiringAssessorss = {4, 14};
        Integer[] expectedAssignmentCounts = {6, 16};

        List<CompetitionClosedKeyApplicationStatisticsResource> keyStatisticsResources =
                newCompetitionClosedKeyApplicationStatisticsResource()
                        .withApplicationsPerAssessor(expectedApplicationsPerAssessors)
                        .withApplicationsRequiringAssessors(expectedApplicationsRequiringAssessorss)
                        .withAssignmentCount(expectedAssignmentCounts)
                        .build(2);

        for (int i = 0; i < keyStatisticsResources.size(); i++) {
            assertEquals((int) expectedApplicationsPerAssessors[i], keyStatisticsResources.get(i)
                    .getApplicationsPerAssessor());
            assertEquals((int) expectedApplicationsRequiringAssessorss[i], keyStatisticsResources.get(i)
                    .getApplicationsRequiringAssessors());
            assertEquals((int) expectedAssignmentCounts[i], keyStatisticsResources.get(i).getAssignmentCount());
        }
    }

}
