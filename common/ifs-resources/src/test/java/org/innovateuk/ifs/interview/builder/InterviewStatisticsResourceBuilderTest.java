package org.innovateuk.ifs.interview.builder;

import org.innovateuk.ifs.interview.resource.InterviewStatisticsResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.interview.builder.InterviewStatisticsResourceBuilder.newInterviewStatisticsResource;
import static org.junit.Assert.assertEquals;

public class InterviewStatisticsResourceBuilderTest {

    @Test
    public void buildOne() {
        int expectedApplicationsAssigned = 9;
        int expectedRespondedToFeedback = 2;
        int expectedAssessorsAccepted = 3;

        InterviewStatisticsResource interviewStatisticsResource = newInterviewStatisticsResource()
                .withApplicationsAssigned(expectedApplicationsAssigned)
                .withRespondedToFeedback(expectedRespondedToFeedback)
                .withAssessorsAccepted(expectedAssessorsAccepted)
                .build();

        assertEquals(expectedApplicationsAssigned, interviewStatisticsResource.getApplicationsAssigned());
        assertEquals(expectedRespondedToFeedback, interviewStatisticsResource.getRespondedToFeedback());
        assertEquals(expectedAssessorsAccepted, interviewStatisticsResource.getAssessorsAccepted());
    }

    @Test
    public void buildMany() {
        Integer[] expectedApplicationsAssigneds = {9, 39};
        Integer[] expectedRespondedToFeedbacks = {2, 12};
        Integer[] expectedAssessorsAccepteds = {3, 13};

        List<InterviewStatisticsResource> inviteStatisticsResources = newInterviewStatisticsResource()
                .withApplicationsAssigned(expectedApplicationsAssigneds)
                .withRespondedToFeedback(expectedRespondedToFeedbacks)
                .withAssessorsAccepted(expectedAssessorsAccepteds)
                .build(2);

        for (int i = 0; i < inviteStatisticsResources.size(); i++) {
            assertEquals((int) expectedApplicationsAssigneds[i], inviteStatisticsResources.get(i).getApplicationsAssigned());
            assertEquals((int) expectedRespondedToFeedbacks[i], inviteStatisticsResources.get(i).getRespondedToFeedback());
            assertEquals((int) expectedAssessorsAccepteds[i], inviteStatisticsResources.get(i).getAssessorsAccepted());
        }
    }
}