package org.innovateuk.ifs.interview.builder;

import org.innovateuk.ifs.interview.resource.InterviewInviteStatisticsResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.interview.builder.InterviewInviteStatisticsResourceBuilder.newInterviewInviteStatisticsResource;
import static org.junit.Assert.assertEquals;

public class InterviewInviteStatisticsResourceBuilderTest {

    @Test
    public void buildOne() {
        int expectedAssessorInvited = 9;
        int expectedAssessorsAccepted = 2;
        int expectedAssessorsRejected = 3;
        int expectedAssessorsOnInviteList = 4;

        InterviewInviteStatisticsResource inviteStatisticsResource = newInterviewInviteStatisticsResource()
                .withAssessorsInvited(expectedAssessorInvited)
                .withAssessorsAccepted(expectedAssessorsAccepted)
                .withAssessorsRejected(expectedAssessorsRejected)
                .withAssessorsOnInviteList(expectedAssessorsOnInviteList)
                .build();

        assertEquals(expectedAssessorInvited, inviteStatisticsResource.getAssessorsInvited());
        assertEquals(expectedAssessorsAccepted, inviteStatisticsResource.getAssessorsAccepted());
        assertEquals(expectedAssessorsRejected, inviteStatisticsResource.getAssessorsRejected());
        assertEquals(expectedAssessorsOnInviteList, inviteStatisticsResource.getAssessorsOnInviteList());
    }

    @Test
    public void buildMany() {
        Integer[] expectedAssessorInviteds = {9, 39};
        Integer[] expectedAssessorsAccepteds = {2, 12};
        Integer[] expectedAssessorsRejecteds = {3, 13};
        Integer[] expectedAssessorsOnInviteLists = {4, 14};

        List<InterviewInviteStatisticsResource> inviteStatisticsResources = newInterviewInviteStatisticsResource()
                .withAssessorsInvited(expectedAssessorInviteds)
                .withAssessorsAccepted(expectedAssessorsAccepteds)
                .withAssessorsRejected(expectedAssessorsRejecteds)
                .withAssessorsOnInviteList(expectedAssessorsOnInviteLists)
                .build(2);

        for (int i = 0; i < inviteStatisticsResources.size(); i++) {
            assertEquals((int) expectedAssessorInviteds[i], inviteStatisticsResources.get(i).getAssessorsInvited());
            assertEquals((int) expectedAssessorsAccepteds[i], inviteStatisticsResources.get(i).getAssessorsAccepted());
            assertEquals((int) expectedAssessorsRejecteds[i], inviteStatisticsResources.get(i).getAssessorsRejected());
            assertEquals((int) expectedAssessorsOnInviteLists[i], inviteStatisticsResources.get(i).getAssessorsOnInviteList());
        }
    }
}