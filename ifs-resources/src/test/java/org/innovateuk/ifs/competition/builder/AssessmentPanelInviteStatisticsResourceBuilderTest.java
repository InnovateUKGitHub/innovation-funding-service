package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.assessment.panel.resource.AssessmentPanelInviteStatisticsResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.competition.builder.AssessmentPanelInviteStatisticsResourceBuilder.newAssessmentPanelInviteStatisticsResource;
import static org.junit.Assert.assertEquals;

public class AssessmentPanelInviteStatisticsResourceBuilderTest {

    @Test
    public void buildOne() {
        int expectedAssessorInvites = 9;
        int expectedAssessorsAccepted = 2;
        int expectedAssessorsRejected = 3;
        int expectedAssessorsPending = 4;

        AssessmentPanelInviteStatisticsResource inviteStatisticsResource = newAssessmentPanelInviteStatisticsResource()
                .withAssessorsInvited(expectedAssessorInvites)
                .withAssessorsAccepted(expectedAssessorsAccepted)
                .withAssessorsRejected(expectedAssessorsRejected)
                .build();

        assertEquals(expectedAssessorInvites, inviteStatisticsResource.getInvited());
        assertEquals(expectedAssessorsAccepted, inviteStatisticsResource.getAccepted());
        assertEquals(expectedAssessorsRejected, inviteStatisticsResource.getDeclined());
        assertEquals(expectedAssessorsPending, inviteStatisticsResource.getPending());
        int totaInvitesInDifferentStates = inviteStatisticsResource.getAccepted() + inviteStatisticsResource.getDeclined() + inviteStatisticsResource.getPending();
        assertEquals(expectedAssessorInvites,totaInvitesInDifferentStates);
    }

    @Test
    public void buildMany() {
        Integer[] expectedAssessorInvites = {9, 39};
        Integer[] expectedAssessorsAccepted = {2, 12};
        Integer[] expectedAssessorsRejected = {3, 13};
        Integer[] expectedAssessorsPending = {4, 14};

        List<AssessmentPanelInviteStatisticsResource> inviteStatisticsResources = newAssessmentPanelInviteStatisticsResource()
                .withAssessorsInvited(expectedAssessorInvites)
                .withAssessorsAccepted(expectedAssessorsAccepted)
                .withAssessorsRejected(expectedAssessorsRejected)
                .build(2);

        for (int i = 0; i < inviteStatisticsResources.size(); i++) {
            assertEquals((int) expectedAssessorInvites[i], inviteStatisticsResources.get(i).getInvited());
            assertEquals((int) expectedAssessorsAccepted[i], inviteStatisticsResources.get(i).getAccepted());
            assertEquals((int) expectedAssessorsRejected[i], inviteStatisticsResources.get(i).getDeclined());
            assertEquals((int) expectedAssessorsPending[i], inviteStatisticsResources.get(i).getPending());
        }
    }
}
