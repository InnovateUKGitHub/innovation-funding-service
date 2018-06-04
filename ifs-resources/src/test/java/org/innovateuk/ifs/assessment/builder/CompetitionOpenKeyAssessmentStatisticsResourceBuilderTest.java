package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.assessment.resource.CompetitionOpenKeyAssessmentStatisticsResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.assessment.builder.CompetitionOpenKeyAssessmentStatisticsResourceBuilder.newCompetitionOpenKeyAssessmentStatisticsResource;
import static org.junit.Assert.assertEquals;

public class CompetitionOpenKeyAssessmentStatisticsResourceBuilderTest {

    @Test
    public void buildOne() {
        int expectedAssessorsInvited = 1;
        int expectedAssessorsAccepted = 2;

        CompetitionOpenKeyAssessmentStatisticsResource keyStatisticsResource =
                newCompetitionOpenKeyAssessmentStatisticsResource()
                        .withAssessorsInvited(expectedAssessorsInvited)
                        .withAssessorsAccepted(expectedAssessorsAccepted)
                        .build();

        assertEquals(expectedAssessorsInvited, keyStatisticsResource.getAssessorsInvited());
        assertEquals(expectedAssessorsAccepted, keyStatisticsResource.getAssessorsAccepted());
    }

    @Test
    public void buildMany() {
        Integer[] expectedAssessorsInviteds = {1, 11};
        Integer[] expectedAssessorsAccepteds = {2, 12};

        List<CompetitionOpenKeyAssessmentStatisticsResource> keyStatisticsResources =
                newCompetitionOpenKeyAssessmentStatisticsResource()
                        .withAssessorsInvited(expectedAssessorsInviteds)
                        .withAssessorsAccepted(expectedAssessorsAccepteds)
                        .build(2);

        for (int i = 0; i < keyStatisticsResources.size(); i++) {
            assertEquals((int) expectedAssessorsInviteds[i], keyStatisticsResources.get(i).getAssessorsInvited());
            assertEquals((int) expectedAssessorsAccepteds[i], keyStatisticsResources.get(i).getAssessorsAccepted());
        }
    }
}
