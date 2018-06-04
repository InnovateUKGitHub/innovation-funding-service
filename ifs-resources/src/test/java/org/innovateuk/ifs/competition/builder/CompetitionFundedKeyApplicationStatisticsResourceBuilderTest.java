package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.competition.resource.CompetitionFundedKeyApplicationStatisticsResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.competition.builder.CompetitionFundedKeyApplicationStatisticsResourceBuilder.newCompetitionFundedKeyApplicationStatisticsResource;
import static org.junit.Assert.assertEquals;

public class CompetitionFundedKeyApplicationStatisticsResourceBuilderTest {

    @Test
    public void buildOne() {
        int expectedApplicationsSubmitted = 1;
        int expectedApplicationsFunded = 2;
        int expectedApplicationsNotFunded = 3;
        int expectedApplicationsOnHold = 4;
        int expectedApplicationsNotifiedOfDecision = 5;
        int expectedApplicationsAwaitingDecision = 6;

        CompetitionFundedKeyApplicationStatisticsResource keyStatisticsResource = newCompetitionFundedKeyApplicationStatisticsResource()
                .withApplicationsSubmitted(expectedApplicationsSubmitted)
                .withApplicationsFunded(expectedApplicationsFunded)
                .withApplicationsNotFunded(expectedApplicationsNotFunded)
                .withApplicationsOnHold(expectedApplicationsOnHold)
                .withApplicationsNotifiedOfDecision(expectedApplicationsNotifiedOfDecision)
                .withApplicationsAwaitingDecision(expectedApplicationsAwaitingDecision)
                .build();

        assertEquals(expectedApplicationsSubmitted, keyStatisticsResource.getApplicationsSubmitted());
        assertEquals(expectedApplicationsFunded, keyStatisticsResource.getApplicationsFunded());
        assertEquals(expectedApplicationsNotFunded, keyStatisticsResource.getApplicationsNotFunded());
        assertEquals(expectedApplicationsOnHold, keyStatisticsResource.getApplicationsOnHold());
        assertEquals(expectedApplicationsNotifiedOfDecision, keyStatisticsResource.getApplicationsNotifiedOfDecision());
        assertEquals(expectedApplicationsAwaitingDecision, keyStatisticsResource.getApplicationsAwaitingDecision());
    }

    @Test
    public void buildMany() {
        Integer[] expectedApplicationsSubmitted = {1, 1};
        Integer[] expectedApplicationsFunded = {2, 1};
        Integer[] expectedApplicationsNotFunded = {3, 1};
        Integer[] expectedApplicationsOnHold = {4, 1};
        Integer[] expectedApplicationsNotifiedOfDecision = {5, 1};
        Integer[] expectedApplicationsAwaitingDecision = {6, 1};

        List<CompetitionFundedKeyApplicationStatisticsResource> keyStatisticsResources = newCompetitionFundedKeyApplicationStatisticsResource()
                .withApplicationsSubmitted(expectedApplicationsSubmitted)
                .withApplicationsFunded(expectedApplicationsFunded)
                .withApplicationsNotFunded(expectedApplicationsNotFunded)
                .withApplicationsOnHold(expectedApplicationsOnHold)
                .withApplicationsNotifiedOfDecision(expectedApplicationsNotifiedOfDecision)
                .withApplicationsAwaitingDecision(expectedApplicationsAwaitingDecision)
                .build(2);

        for (int i = 0; i < keyStatisticsResources.size(); i++) {
            assertEquals((int) expectedApplicationsSubmitted[i], keyStatisticsResources.get(i).getApplicationsSubmitted());
            assertEquals((int) expectedApplicationsFunded[i], keyStatisticsResources.get(i).getApplicationsFunded());
            assertEquals((int) expectedApplicationsNotFunded[i], keyStatisticsResources.get(i).getApplicationsNotFunded());
            assertEquals((int) expectedApplicationsOnHold[i], keyStatisticsResources.get(i).getApplicationsOnHold());
            assertEquals((int) expectedApplicationsNotifiedOfDecision[i], keyStatisticsResources.get(i).getApplicationsNotifiedOfDecision());
            assertEquals((int) expectedApplicationsAwaitingDecision[i], keyStatisticsResources.get(i).getApplicationsAwaitingDecision());
        }
    }
}
