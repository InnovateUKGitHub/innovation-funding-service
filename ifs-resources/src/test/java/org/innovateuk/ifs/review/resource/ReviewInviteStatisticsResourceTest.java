package org.innovateuk.ifs.review.resource;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ReviewInviteStatisticsResourceTest {

    private int totalInvites = 5;
    private int acceptedInvites = 2;
    private int declinedInvites = 1;
    private int expectedPendingInvites = 2;

    @Test
    public void invitesAddUp() {
        ReviewInviteStatisticsResource statisticsResource = new ReviewInviteStatisticsResource(totalInvites, acceptedInvites, declinedInvites);
        assertEquals(statisticsResource.getPending(), expectedPendingInvites);
        assertEquals(totalInvites, (acceptedInvites + declinedInvites + expectedPendingInvites));
    }
}
