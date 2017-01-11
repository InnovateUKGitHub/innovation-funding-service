package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.invite.resource.AssessorCreatedInviteResource;
import org.junit.Test;

import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.invite.builder.AssessorCreatedInviteResourceBuilder.newAssessorCreatedInviteResource;
import static org.junit.Assert.assertEquals;

public class AssessorCreatedInviteResourceBuilderTest {

    @Test
    public void buildOne() {
        String expectedName = "name";
        InnovationAreaResource expectedInnovationArea = newInnovationAreaResource().build();
        Boolean expectedCompliant = FALSE;
        String expectedEmail = "email";
        long expecetedInviteId = 2L;

        AssessorCreatedInviteResource assessorCreatedInviteResource = newAssessorCreatedInviteResource()
                .withName(expectedName)
                .withInnovationArea(expectedInnovationArea)
                .withCompliant(expectedCompliant)
                .withEmail(expectedEmail)
                .withInviteId(expecetedInviteId)
                .build();

        assertEquals(expectedName, assessorCreatedInviteResource.getName());
        assertEquals(expectedInnovationArea, assessorCreatedInviteResource.getInnovationArea());
        assertEquals(expectedCompliant, assessorCreatedInviteResource.isCompliant());
        assertEquals(expectedEmail, assessorCreatedInviteResource.getEmail());
        assertEquals(expecetedInviteId, assessorCreatedInviteResource.getInviteId());
    }

    @Test
    public void buildMany() {
        String[] expectedNames = {"name1", "name2"};
        InnovationAreaResource[] expectedInnovationAreas = newInnovationAreaResource().buildArray(2, InnovationAreaResource.class);
        Boolean[] expectedCompliants = {TRUE, FALSE};
        String[] expectedEmails = {"email1", "email2"};
        Long[] expectedInviteIds = {1L,2L};

        List<AssessorCreatedInviteResource> assessorCreatedInviteResources = newAssessorCreatedInviteResource()
                .withName(expectedNames)
                .withInnovationArea(expectedInnovationAreas)
                .withCompliant(expectedCompliants)
                .withEmail(expectedEmails)
                .withInviteId(expectedInviteIds)
                .build(2);

        AssessorCreatedInviteResource first = assessorCreatedInviteResources.get(0);
        assertEquals(expectedNames[0], first.getName());
        assertEquals(expectedInnovationAreas[0], first.getInnovationArea());
        assertEquals(expectedCompliants[0], first.isCompliant());
        assertEquals(expectedEmails[0], first.getEmail());
        assertEquals((long)expectedInviteIds[0], first.getInviteId());

        AssessorCreatedInviteResource second = assessorCreatedInviteResources.get(1);
        assertEquals(expectedNames[1], second.getName());
        assertEquals(expectedInnovationAreas[1], second.getInnovationArea());
        assertEquals(expectedCompliants[1], second.isCompliant());
        assertEquals(expectedEmails[1], second.getEmail());
        assertEquals((long)expectedInviteIds[1], second.getInviteId());
    }
}