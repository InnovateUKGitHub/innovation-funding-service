package org.innovateuk.ifs.review.builder;

import org.innovateuk.ifs.review.resource.ReviewInviteStatisticsResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.review.builder.ReviewInviteStatisticsResourceBuilder.newReviewInviteStatisticsResource;
import static org.junit.Assert.assertEquals;

public class ReviewInviteStatisticsResourceBuilderTest {

    @Test
    public void buildOne() {
        int expectedAssessorInvites = 9;
        int expectedAssessorsAccepted = 2;
        int expectedAssessorsRejected = 3;

        ReviewInviteStatisticsResource inviteStatisticsResource = newReviewInviteStatisticsResource()
                .withAssessorsInvited(expectedAssessorInvites)
                .withAssessorsAccepted(expectedAssessorsAccepted)
                .withAssessorsRejected(expectedAssessorsRejected)
                .build();

        assertEquals(expectedAssessorInvites, inviteStatisticsResource.getInvited());
        assertEquals(expectedAssessorsAccepted, inviteStatisticsResource.getAccepted());
        assertEquals(expectedAssessorsRejected, inviteStatisticsResource.getDeclined());
    }

    @Test
    public void buildMany() {
        Integer[] expectedAssessorInvites = {9, 39};
        Integer[] expectedAssessorsAccepted = {2, 12};
        Integer[] expectedAssessorsRejected = {3, 13};

        List<ReviewInviteStatisticsResource> inviteStatisticsResources = newReviewInviteStatisticsResource()
                .withAssessorsInvited(expectedAssessorInvites)
                .withAssessorsAccepted(expectedAssessorsAccepted)
                .withAssessorsRejected(expectedAssessorsRejected)
                .build(2);

        for (int i = 0; i < inviteStatisticsResources.size(); i++) {
            assertEquals((int) expectedAssessorInvites[i], inviteStatisticsResources.get(i).getInvited());
            assertEquals((int) expectedAssessorsAccepted[i], inviteStatisticsResources.get(i).getAccepted());
            assertEquals((int) expectedAssessorsRejected[i], inviteStatisticsResources.get(i).getDeclined());
        }
    }
}