package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.assessment.resource.CompetitionClosedKeyAssessmentStatisticsResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.assessment.builder.CompetitionClosedKeyAssessmentStatisticsResourceBuilder.newCompetitionClosedKeyAssessmentStatisticsResource;
import static org.junit.Assert.assertEquals;

public class CompetitionClosedKeyAssessmentStatisticsResourceBuilderTest {

    @Test
    public void buildOne() {
        int expectedAssessorsInvited = 1;
        int expectedAssessorsAccepted = 2;
        int expectedAssessorsWithoutApplications = 5;

        CompetitionClosedKeyAssessmentStatisticsResource keyStatisticsResource =
                newCompetitionClosedKeyAssessmentStatisticsResource()
                        .withAssessorsInvited(expectedAssessorsInvited)
                        .withAssessorsAccepted(expectedAssessorsAccepted)
                        .withAssessorsWithoutApplications(expectedAssessorsWithoutApplications)
                        .build();

        assertEquals(expectedAssessorsInvited, keyStatisticsResource.getAssessorsInvited());
        assertEquals(expectedAssessorsAccepted, keyStatisticsResource.getAssessorsAccepted());
        assertEquals(expectedAssessorsWithoutApplications, keyStatisticsResource.getAssessorsWithoutApplications());
    }

    @Test
    public void buildMany() {
        Integer[] expectedAssessorsInviteds = {1, 11};
        Integer[] expectedAssessorsAccepteds = {2, 12};
        Integer[] expectedAssessorsWithoutApplicationss = {5, 15};

        List<CompetitionClosedKeyAssessmentStatisticsResource> keyStatisticsResources =
                newCompetitionClosedKeyAssessmentStatisticsResource()
                        .withAssessorsInvited(expectedAssessorsInviteds)
                        .withAssessorsAccepted(expectedAssessorsAccepteds)
                        .withAssessorsWithoutApplications(expectedAssessorsWithoutApplicationss)
                        .build(2);

        for (int i = 0; i < keyStatisticsResources.size(); i++) {
            assertEquals((int) expectedAssessorsInviteds[i], keyStatisticsResources.get(i).getAssessorsInvited());
            assertEquals((int) expectedAssessorsAccepteds[i], keyStatisticsResources.get(i).getAssessorsAccepted());
            assertEquals((int) expectedAssessorsWithoutApplicationss[i], keyStatisticsResources.get(i)
                    .getAssessorsWithoutApplications());
        }
    }
}
