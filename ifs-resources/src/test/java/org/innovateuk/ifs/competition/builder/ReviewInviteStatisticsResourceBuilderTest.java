package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.review.resource.ReviewInviteStatisticsResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.competition.builder.AssessmentPanelInviteStatisticsResourceBuilder.newAssessmentPanelInviteStatisticsResource;
import static org.junit.Assert.assertEquals;

public class ReviewInviteStatisticsResourceBuilderTest {

    @Test
    public void buildOne() {
        int expectedAssessorInvites = 9;
        int expectedAssessorsAccepted = 2;
        int expectedAssessorsRejected = 3;
        int expectedAssessorsPending = 4;

        ReviewInviteStatisticsResource inviteStatisticsResource = newAssessmentPanelInviteStatisticsResource()
                .withAssessorsInvited(expectedAssessorInvites)
                .withAssessorsAccepted(expectedAssessorsAccepted)
                .withAssessorsRejected(expectedAssessorsRejected)
                .build();

        assertEquals(expectedAssessorInvites, inviteStatisticsResource.getInvited());
        assertEquals(expectedAssessorsAccepted, inviteStatisticsResource.getAccepted());
        assertEquals(expectedAssessorsRejected, inviteStatisticsResource.getDeclined());
        assertEquals(expectedAssessorsPending, inviteStatisticsResource.getPending());
    }

    @Test
    public void buildMany() {
        Integer[] expectedAssessorInvites = {9, 39};
        Integer[] expectedAssessorsAccepted = {2, 12};
        Integer[] expectedAssessorsRejected = {3, 13};
        Integer[] expectedAssessorsPending = {4, 14};

        List<ReviewInviteStatisticsResource> inviteStatisticsResources = newAssessmentPanelInviteStatisticsResource()
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
