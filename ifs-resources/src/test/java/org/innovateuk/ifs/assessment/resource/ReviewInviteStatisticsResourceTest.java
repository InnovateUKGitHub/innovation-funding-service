package org.innovateuk.ifs.assessment.resource;

import org.innovateuk.ifs.review.resource.ReviewInviteStatisticsResource;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ReviewInviteStatisticsResourceTest {

    private int totalInvites = 5;
    private int acceptedInvites = 2;
    private int declinedInvites = 1;
    private int expectedPendingInvites = 2;

    @Test
    public void invitesAddUp() throws Exception {
        ReviewInviteStatisticsResource statisticsResource = new ReviewInviteStatisticsResource();
        statisticsResource.setAccepted(acceptedInvites);
        statisticsResource.setInvited(totalInvites);
        statisticsResource.setDeclined(declinedInvites);
        assertEquals(statisticsResource.getPending(), expectedPendingInvites);
        assertEquals(totalInvites, (acceptedInvites + declinedInvites + expectedPendingInvites));
    }

}
