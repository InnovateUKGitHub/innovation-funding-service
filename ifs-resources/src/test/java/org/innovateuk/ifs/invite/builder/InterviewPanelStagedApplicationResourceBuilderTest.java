package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.invite.resource.InterviewPanelStagedApplicationResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.invite.builder.InterviewPanelCreatedInviteResourceBuilder.newInterviewPanelStagedApplicationResource;
import static org.junit.Assert.assertEquals;

public class InterviewPanelStagedApplicationResourceBuilderTest {

    @Test
    public void buildOne() {
        long expectedId = 1L;
        long expectedApplicationId = 3L;
        String expectedApplicationName = "applicationName";
        String expectedLeadOrganisation = "leadOrganisation";

        InterviewPanelStagedApplicationResource interviewPanelStagedApplicationResource = newInterviewPanelStagedApplicationResource()
                .withId(expectedId)
                .withApplicationId(expectedApplicationId)
                .withApplicationName(expectedApplicationName)
                .withLeadOrganisationName(expectedLeadOrganisation)
                .build();

        assertEquals(expectedId, interviewPanelStagedApplicationResource.getId());
        assertEquals(expectedApplicationId, interviewPanelStagedApplicationResource.getApplicationId());
        assertEquals(expectedApplicationName, interviewPanelStagedApplicationResource.getApplicationName());
        assertEquals(expectedLeadOrganisation, interviewPanelStagedApplicationResource.getLeadOrganisationName());
    }

    @Test
    public void buildMany() {
        Long[] expectedIds = {1L, 2L};
        Long[] expectedApplicationIds = {3L, 4L};
        String[] expectedApplicationNames = {"applicationName1", "applicationName2"};
        String[] expectedLeadOrganisations = {"leadOrganisation1", "leadOrganisation2"};

        List<InterviewPanelStagedApplicationResource> interviewPanelStagedApplicationResources = newInterviewPanelStagedApplicationResource()
                .withId(expectedIds)
                .withApplicationId(expectedApplicationIds)
                .withApplicationName(expectedApplicationNames)
                .withLeadOrganisationName(expectedLeadOrganisations)
                .build(2);

        InterviewPanelStagedApplicationResource first = interviewPanelStagedApplicationResources.get(0);
        assertEquals((long) expectedIds[0], first.getId());
        assertEquals((long) expectedApplicationIds[0], first.getApplicationId());
        assertEquals(expectedApplicationNames[0], first.getApplicationName());
        assertEquals(expectedLeadOrganisations[0], first.getLeadOrganisationName());

        InterviewPanelStagedApplicationResource second = interviewPanelStagedApplicationResources.get(1);
        assertEquals((long) expectedIds[1], second.getId());
        assertEquals((long) expectedApplicationIds[1], second.getApplicationId());
        assertEquals(expectedApplicationNames[1], second.getApplicationName());
        assertEquals(expectedLeadOrganisations[1], second.getLeadOrganisationName());
    }
}