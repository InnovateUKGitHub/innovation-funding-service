package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.competition.resource.CompetitionOpenKeyApplicationStatisticsResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.competition.builder.CompetitionOpenKeyApplicationStatisticsResourceBuilder.newCompetitionOpenKeyApplicationStatisticsResource;
import static org.junit.Assert.assertEquals;

public class CompetitionOpenKeyApplicationStatisticsResourceBuilderTest {

    @Test
    public void buildOne() {
        int expectedApplicationsPerAssessor = 3;
        int expectedApplicationsStarted = 4;
        int expectedApplicationsPastHalf = 5;
        int expectedApplicationsSubmitted = 6;

        CompetitionOpenKeyApplicationStatisticsResource keyStatisticsResource =
                newCompetitionOpenKeyApplicationStatisticsResource()
                        .withApplicationsPerAssessor(expectedApplicationsPerAssessor)
                        .withApplicationsStarted(expectedApplicationsStarted)
                        .withApplicationsPastHalf(expectedApplicationsPastHalf)
                        .withApplicationsSubmitted(expectedApplicationsSubmitted)
                        .build();

        assertEquals(expectedApplicationsPerAssessor, keyStatisticsResource.getApplicationsPerAssessor());
        assertEquals(expectedApplicationsStarted, keyStatisticsResource.getApplicationsStarted());
        assertEquals(expectedApplicationsPastHalf, keyStatisticsResource.getApplicationsPastHalf());
        assertEquals(expectedApplicationsSubmitted, keyStatisticsResource.getApplicationsSubmitted());
    }

    @Test
    public void buildMany() {
        Integer[] expectedApplicationsPerAssessors = {3, 13};
        Integer[] expectedApplicationsStarteds = {4, 14};
        Integer[] expectedApplicationsPastHalfs = {5, 15};
        Integer[] expectedApplicationsSubmitteds = {6, 16};

        List<CompetitionOpenKeyApplicationStatisticsResource> keyStatisticsResources =
                newCompetitionOpenKeyApplicationStatisticsResource()
                        .withApplicationsPerAssessor(expectedApplicationsPerAssessors)
                        .withApplicationsStarted(expectedApplicationsStarteds)
                        .withApplicationsPastHalf(expectedApplicationsPastHalfs)
                        .withApplicationsSubmitted(expectedApplicationsSubmitteds)
                        .build(2);

        for (int i = 0; i < keyStatisticsResources.size(); i++) {
            assertEquals((int) expectedApplicationsPerAssessors[i], keyStatisticsResources.get(i)
                    .getApplicationsPerAssessor());
            assertEquals((int) expectedApplicationsStarteds[i], keyStatisticsResources.get(i).getApplicationsStarted());
            assertEquals((int) expectedApplicationsPastHalfs[i], keyStatisticsResources.get(i)
                    .getApplicationsPastHalf());
            assertEquals((int) expectedApplicationsSubmitteds[i], keyStatisticsResources.get(i)
                    .getApplicationsSubmitted());
        }
    }
}
