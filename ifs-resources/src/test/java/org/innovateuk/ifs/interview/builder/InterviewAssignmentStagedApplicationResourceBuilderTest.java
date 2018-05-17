package org.innovateuk.ifs.interview.builder;

import org.innovateuk.ifs.invite.resource.InterviewAssignmentStagedApplicationResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.interview.builder.InterviewAssignmentCreatedInviteResourceBuilder.newInterviewAssignmentStagedApplicationResource;
import static org.junit.Assert.assertEquals;

public class InterviewAssignmentStagedApplicationResourceBuilderTest {

    @Test
    public void buildOne() {
        long expectedId = 1L;
        long expectedApplicationId = 3L;
        String expectedApplicationName = "applicationName";
        String expectedLeadOrganisation = "leadOrganisation";

        InterviewAssignmentStagedApplicationResource interviewAssignmentStagedApplicationResource = newInterviewAssignmentStagedApplicationResource()
                .withId(expectedId)
                .withApplicationId(expectedApplicationId)
                .withApplicationName(expectedApplicationName)
                .withLeadOrganisationName(expectedLeadOrganisation)
                .build();

        assertEquals(expectedId, interviewAssignmentStagedApplicationResource.getId());
        assertEquals(expectedApplicationId, interviewAssignmentStagedApplicationResource.getApplicationId());
        assertEquals(expectedApplicationName, interviewAssignmentStagedApplicationResource.getApplicationName());
        assertEquals(expectedLeadOrganisation, interviewAssignmentStagedApplicationResource.getLeadOrganisationName());
    }

    @Test
    public void buildMany() {
        Long[] expectedIds = {1L, 2L};
        Long[] expectedApplicationIds = {3L, 4L};
        String[] expectedApplicationNames = {"applicationName1", "applicationName2"};
        String[] expectedLeadOrganisations = {"leadOrganisation1", "leadOrganisation2"};

        List<InterviewAssignmentStagedApplicationResource> interviewAssignmentStagedApplicationResources = newInterviewAssignmentStagedApplicationResource()
                .withId(expectedIds)
                .withApplicationId(expectedApplicationIds)
                .withApplicationName(expectedApplicationNames)
                .withLeadOrganisationName(expectedLeadOrganisations)
                .build(2);

        InterviewAssignmentStagedApplicationResource first = interviewAssignmentStagedApplicationResources.get(0);
        assertEquals((long) expectedIds[0], first.getId());
        assertEquals((long) expectedApplicationIds[0], first.getApplicationId());
        assertEquals(expectedApplicationNames[0], first.getApplicationName());
        assertEquals(expectedLeadOrganisations[0], first.getLeadOrganisationName());

        InterviewAssignmentStagedApplicationResource second = interviewAssignmentStagedApplicationResources.get(1);
        assertEquals((long) expectedIds[1], second.getId());
        assertEquals((long) expectedApplicationIds[1], second.getApplicationId());
        assertEquals(expectedApplicationNames[1], second.getApplicationName());
        assertEquals(expectedLeadOrganisations[1], second.getLeadOrganisationName());
    }
}