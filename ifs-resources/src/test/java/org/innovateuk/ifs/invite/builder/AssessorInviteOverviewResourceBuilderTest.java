package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.category.resource.InnovationAreaResource;
import org.innovateuk.ifs.invite.resource.AssessorInviteOverviewResource;
import org.innovateuk.ifs.invite.resource.ParticipantStatusResource;
import org.innovateuk.ifs.user.resource.BusinessType;
import org.junit.Test;

import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.innovateuk.ifs.category.builder.InnovationAreaResourceBuilder.newInnovationAreaResource;
import static org.innovateuk.ifs.invite.builder.AssessorInviteOverviewResourceBuilder.newAssessorInviteOverviewResource;
import static org.innovateuk.ifs.invite.resource.ParticipantStatusResource.ACCEPTED;
import static org.innovateuk.ifs.invite.resource.ParticipantStatusResource.PENDING;
import static org.innovateuk.ifs.user.resource.BusinessType.ACADEMIC;
import static org.innovateuk.ifs.user.resource.BusinessType.BUSINESS;
import static org.junit.Assert.assertEquals;

public class AssessorInviteOverviewResourceBuilderTest {

    @Test
    public void buildOne() {
        String expectedName = "name";
        List<InnovationAreaResource> expectedInnovationAreas = newInnovationAreaResource().build(2);
        Boolean expectedCompliant = FALSE;
        BusinessType expectedBusinessType = ACADEMIC;
        ParticipantStatusResource expectedStatus = ACCEPTED;
        String expectedDetails = "details";
        Long expectedInviteId = 1L;

        AssessorInviteOverviewResource assessorInviteOverviewResource = newAssessorInviteOverviewResource()
                .withName(expectedName)
                .withInnovationAreas(expectedInnovationAreas)
                .withCompliant(expectedCompliant)
                .withBusinessType(expectedBusinessType)
                .withStatus(expectedStatus)
                .withDetails(expectedDetails)
                .withInviteId(expectedInviteId)
                .build();

        assertEquals(expectedName, assessorInviteOverviewResource.getName());
        assertEquals(expectedInnovationAreas, assessorInviteOverviewResource.getInnovationAreas());
        assertEquals(expectedCompliant, assessorInviteOverviewResource.isCompliant());
        assertEquals(expectedBusinessType, assessorInviteOverviewResource.getBusinessType());
        assertEquals(expectedStatus, assessorInviteOverviewResource.getStatus());
        assertEquals(expectedDetails, assessorInviteOverviewResource.getDetails());
        assertEquals(expectedInviteId, assessorInviteOverviewResource.getInviteId());
    }

    @Test
    public void buildMany() {
        String[] expectedNames = {"name1", "name2"};
        @SuppressWarnings("unchecked") List<InnovationAreaResource>[] expectedInnovationAreas = new List[]{
                newInnovationAreaResource()
                        .withName("Creative economy", "Offshore Renewable Energy")
                        .build(2),
                newInnovationAreaResource()
                        .withName("Urban Living", "Advanced therapies")
                        .build(2)
        };
        Boolean[] expectedCompliants = {TRUE, FALSE};
        BusinessType[] expectedBusinessTypes = {ACADEMIC, BUSINESS};
        ParticipantStatusResource[] expectedStatuses = {PENDING, ACCEPTED};
        String[] expectedDetails = {"details1", "details2"};
        Long[] expectedInviteIds = {1L, 2L};

        List<AssessorInviteOverviewResource> assessorCreatedInviteResources = newAssessorInviteOverviewResource()
                .withName(expectedNames)
                .withInnovationAreas(expectedInnovationAreas)
                .withCompliant(expectedCompliants)
                .withBusinessType(expectedBusinessTypes)
                .withStatus(expectedStatuses)
                .withDetails(expectedDetails)
                .withInviteId(expectedInviteIds)
                .build(2);

        AssessorInviteOverviewResource first = assessorCreatedInviteResources.get(0);
        assertEquals(expectedNames[0], first.getName());
        assertEquals(expectedInnovationAreas[0], first.getInnovationAreas());
        assertEquals(expectedCompliants[0], first.isCompliant());
        assertEquals(expectedBusinessTypes[0], first.getBusinessType());
        assertEquals(expectedStatuses[0], first.getStatus());
        assertEquals(expectedDetails[0], first.getDetails());
        assertEquals(expectedInviteIds[0], first.getInviteId());

        AssessorInviteOverviewResource second = assessorCreatedInviteResources.get(1);
        assertEquals(expectedNames[1], second.getName());
        assertEquals(expectedInnovationAreas[1], second.getInnovationAreas());
        assertEquals(expectedCompliants[1], second.isCompliant());
        assertEquals(expectedBusinessTypes[1], second.getBusinessType());
        assertEquals(expectedStatuses[1], second.getStatus());
        assertEquals(expectedDetails[1], second.getDetails());
        assertEquals(expectedInviteIds[1], second.getInviteId());
    }
}