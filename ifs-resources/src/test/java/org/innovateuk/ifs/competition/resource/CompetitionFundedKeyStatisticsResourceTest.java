package org.innovateuk.ifs.competition.resource;

import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.competition.builder.CompetitionFundedKeyStatisticsResourceBuilder.newCompetitionFundedKeyStatisticsResource;
import static org.junit.Assert.assertEquals;

public class CompetitionFundedKeyStatisticsResourceTest {

    @Test
    public void isCanManageFundingNotifications() {
        List<CompetitionFundedKeyStatisticsResource> resources =
                newCompetitionFundedKeyStatisticsResource()
                        .withApplicationsFunded(0, 1, 0, 0, 1, 0, 1, 1)
                        .withApplicationsNotFunded(0, 0, 1, 0, 1, 1, 0, 1)
                        .withApplicationsOnHold(0, 0, 0, 1, 0, 1, 0, 1)
                        .build(8);
        boolean[] expectedCanManageFunding = {false, true, true, true, true, true, true, true};
        for (int i = 0; i < 8; i++) {
            assertEquals(expectedCanManageFunding[i], resources.get(i).isCanManageFundingNotifications());
        }
    }

    @Test
    public void isCanReleaseFeedback() {
        List<CompetitionFundedKeyStatisticsResource> resources =
                newCompetitionFundedKeyStatisticsResource()
                .withApplicationsAwaitingDecision(1,1,0,0)
                .withApplicationsSubmitted(1,1,1,1)
                .withApplicationsNotifiedOfDecision(0,1,0,1)
                .build(4);
        boolean[] expectedCanReleaseFeedback = {false, false, false, true};
        for (int i = 0; i < 4; i++) {
            assertEquals(expectedCanReleaseFeedback[i], resources.get(i).isCanReleaseFeedback());
        }
    }
}
