package org.innovateuk.ifs.assessment.resource;

import org.innovateuk.ifs.assessment.panel.resource.AssessmentPanelInviteStatisticsResource;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AssessmentPanelInviteStatisticsResourceTest {

    private int totalInvites = 5;
    private int acceptedInvites = 2;
    private int declinedInvites = 1;
    private int expectedPendingInvites = 2;

    @Test
    public void invitesAddUp() throws Exception {
        AssessmentPanelInviteStatisticsResource statisticsResource = new AssessmentPanelInviteStatisticsResource();
        statisticsResource.setAccepted(acceptedInvites);
        statisticsResource.setInvited(totalInvites);
        statisticsResource.setDeclined(declinedInvites);
        assertEquals(statisticsResource.getPending(), expectedPendingInvites);
        assertEquals(totalInvites, (acceptedInvites + declinedInvites + expectedPendingInvites));
    }

}
