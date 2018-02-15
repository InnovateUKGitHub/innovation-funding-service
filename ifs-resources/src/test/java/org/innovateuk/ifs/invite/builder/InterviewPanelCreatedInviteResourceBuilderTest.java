package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.invite.resource.AvailableApplicationResource;
import org.innovateuk.ifs.invite.resource.InterviewPanelCreatedInviteResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.invite.builder.AvailableApplicationResourceBuilder.newAvailableApplicationResource;
import static org.innovateuk.ifs.invite.builder.InterviewPanelCreatedInviteResourceBuilder.newInterviewPanelCreatedInviteResource;
import static org.junit.Assert.assertEquals;

public class InterviewPanelCreatedInviteResourceBuilderTest {

    @Test
    public void buildOne() {
        long expectedApplicationId = 1L;
        String expectedApplicationName = "applicationName";
        String expectedLeadOrganisation = "leadOrganisation";

        InterviewPanelCreatedInviteResource interviewPanelCreatedInviteResource = newInterviewPanelCreatedInviteResource()
                .withApplicationId(expectedApplicationId)
                .withApplicationName(expectedApplicationName)
                .withLeadOrganisationName(expectedLeadOrganisation)
                .build();

        assertEquals(expectedApplicationId, interviewPanelCreatedInviteResource.getId());
        assertEquals(expectedApplicationName, interviewPanelCreatedInviteResource.getApplicationName());
        assertEquals(expectedLeadOrganisation, interviewPanelCreatedInviteResource.getLeadOrganisationName());
    }

    @Test
    public void buildMany() {
        Long[] expectedApplicationIds = {1L, 2L};
        String[] expectedApplicationNames = {"applicationName1", "applicationName2"};
        String[] expectedLeadOrganisations = {"leadOrganisation1", "leadOrganisation2"};

        List<InterviewPanelCreatedInviteResource> interviewPanelCreatedInviteResources = newInterviewPanelCreatedInviteResource()
                .withApplicationId(expectedApplicationIds)
                .withApplicationName(expectedApplicationNames)
                .withLeadOrganisationName(expectedLeadOrganisations)
                .build(2);

        InterviewPanelCreatedInviteResource first = interviewPanelCreatedInviteResources.get(0);
        assertEquals(expectedApplicationIds[0], first.getApplicationId());
        assertEquals(expectedApplicationNames[0], first.getApplicationName());
        assertEquals(expectedLeadOrganisations[0], first.getLeadOrganisationName());

        InterviewPanelCreatedInviteResource second = interviewPanelCreatedInviteResources.get(1);
        assertEquals(expectedApplicationIds[1], second.getApplicationId());
        assertEquals(expectedApplicationNames[1], second.getApplicationName());
        assertEquals(expectedLeadOrganisations[1], second.getLeadOrganisationName());
    }
}
