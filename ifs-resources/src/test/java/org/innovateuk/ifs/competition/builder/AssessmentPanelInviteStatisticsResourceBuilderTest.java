package org.innovateuk.ifs.competition.builder;

import org.innovateuk.ifs.assessment.panel.resource.AssessmentPanelInviteStatisticsResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.competition.builder.AssessmentPanelInviteStatisticsResourceBuilder.newAssessmentPanelInviteStatisticsResource;
import static org.junit.Assert.assertEquals;

public class AssessmentPanelInviteStatisticsResourceBuilderTest {

    @Test
    public void buildOne() {
        int expectedAssessorInvites = 1;
        int expectedAssessorAccepts = 2;
        int expectedAssessorRejects = 3;
        int expectedAssessorsListed = 4;

        AssessmentPanelInviteStatisticsResource inviteStatisticsResource = newAssessmentPanelInviteStatisticsResource()
                .withAssessorsInvited(expectedAssessorInvites)
                .withAssessorsAccepted(expectedAssessorAccepts)
                .withAssessorsRejected(expectedAssessorRejects)
                .withAssessorsListed(expectedAssessorsListed)
                .build();

        assertEquals(expectedAssessorInvites, inviteStatisticsResource.getInvited());
        assertEquals(expectedAssessorAccepts, inviteStatisticsResource.getAccepted());
        assertEquals(expectedAssessorRejects, inviteStatisticsResource.getDeclined());
        assertEquals(expectedAssessorsListed, inviteStatisticsResource.getInviteList());
    }

    @Test
    public void buildMany() {
        Integer[] expectedAssessorInvites = {1, 11};
        Integer[] expectedAssessorAccepts = {2, 12};
        Integer[] expectedAssessorRejects = {3, 13};
        Integer[] expectedAssessorsListed = {4, 14};

        List<AssessmentPanelInviteStatisticsResource> inviteStatisticsResources = newAssessmentPanelInviteStatisticsResource()
                .withAssessorsInvited(expectedAssessorInvites)
                .withAssessorsAccepted(expectedAssessorAccepts)
                .withAssessorsRejected(expectedAssessorRejects)
                .withAssessorsListed(expectedAssessorsListed)
                .build(2);

        for (int i = 0; i < inviteStatisticsResources.size(); i++) {
            assertEquals((int) expectedAssessorInvites[i], inviteStatisticsResources.get(i).getInvited());
            assertEquals((int) expectedAssessorAccepts[i], inviteStatisticsResources.get(i).getAccepted());
            assertEquals((int) expectedAssessorRejects[i], inviteStatisticsResources.get(i).getDeclined());
            assertEquals((int) expectedAssessorsListed[i], inviteStatisticsResources.get(i).getInviteList());
        }
    }
}
