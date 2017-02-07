package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.competition.resource.CompetitionReadyToOpenKeyStatisticsResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.competition.builder.CompetitionReadyToOpenKeyStatisticsResourceBuilder.newCompetitionReadyToOpenKeyStatisticsResource;
import static org.junit.Assert.assertEquals;

public class CompetitionReadyToOpenKeyStatisticsResourceBuilderTest {

    @Test
    public void buildOne() {

        long expectedAssessorsInvited = 1L;
        long expectedAssessorsAccepted = 2L;

        CompetitionReadyToOpenKeyStatisticsResource keyStatisticsResource = newCompetitionReadyToOpenKeyStatisticsResource()
                .withAssessorsInvited(expectedAssessorsInvited)
                .withAssessorsAccepted(expectedAssessorsAccepted)
                .build();

        assertEquals(expectedAssessorsInvited, keyStatisticsResource.getAssessorsInvited());
        assertEquals(expectedAssessorsAccepted, keyStatisticsResource.getAssessorsAccepted());
    }

    @Test
    public void buildMany() {
        Long[] expectedAssessorsInviteds = {1L, 3L};
        Long[] expectedAssessorsAccepteds = {2L, 4L};

        List<CompetitionReadyToOpenKeyStatisticsResource> keyStatisticsResources = newCompetitionReadyToOpenKeyStatisticsResource()
                .withAssessorsInvited(expectedAssessorsInviteds)
                .withAssessorsAccepted(expectedAssessorsAccepteds)
                .build(2);

        for (int i = 0; i < keyStatisticsResources.size(); i++) {
            assertEquals((long) expectedAssessorsInviteds[i], keyStatisticsResources.get(i).getAssessorsInvited());
            assertEquals((long) expectedAssessorsAccepteds[i], keyStatisticsResources.get(i).getAssessorsAccepted());
        }

    }

}
