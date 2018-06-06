package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.assessment.resource.CompetitionReadyToOpenKeyAssessmentStatisticsResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.assessment.builder.CompetitionReadyToOpenKeyAssessmentStatisticsResourceBuilder.newCompetitionReadyToOpenKeyAssessmentStatisticsResource;
import static org.junit.Assert.assertEquals;

public class CompetitionReadyToOpenKeyAssessmentStatisticsResourceBuilderTest {

    @Test
    public void buildOne() {
        int expectedAssessorsInvited = 1;
        int expectedAssessorsAccepted = 2;

        CompetitionReadyToOpenKeyAssessmentStatisticsResource keyStatisticsResource =
                newCompetitionReadyToOpenKeyAssessmentStatisticsResource()
                        .withAssessorsInvited(expectedAssessorsInvited)
                        .withAssessorsAccepted(expectedAssessorsAccepted)
                        .build();

        assertEquals(expectedAssessorsInvited, keyStatisticsResource.getAssessorsInvited());
        assertEquals(expectedAssessorsAccepted, keyStatisticsResource.getAssessorsAccepted());
    }

    @Test
    public void buildMany() {
        Integer[] expectedAssessorsInviteds = {1, 3};
        Integer[] expectedAssessorsAccepteds = {2, 4};

        List<CompetitionReadyToOpenKeyAssessmentStatisticsResource> keyStatisticsResources =
                newCompetitionReadyToOpenKeyAssessmentStatisticsResource()
                        .withAssessorsInvited(expectedAssessorsInviteds)
                        .withAssessorsAccepted(expectedAssessorsAccepteds)
                        .build(2);

        for (int i = 0; i < keyStatisticsResources.size(); i++) {
            assertEquals((long) expectedAssessorsInviteds[i], keyStatisticsResources.get(i).getAssessorsInvited());
            assertEquals((long) expectedAssessorsAccepteds[i], keyStatisticsResources.get(i).getAssessorsAccepted());
        }
    }
}
