package com.worth.ifs.invite.resource;


import com.worth.ifs.invite.constant.InviteStatus;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class InviteProjectResourceTest {

    InviteProjectResource setInviteResource;
    InviteProjectResource constructedInviteResource;

    Long inviteId;
    Long userId;
    String name;
    String nameConfirmed;
    String email;
    Long project;
    Long applicationId;
    String hash;
    Long organisation;
    InviteStatus status;
    String leadApplicant;
    String competitionName;

    @Before
    public void setUp() throws Exception {
        inviteId = 1L;
        userId = 40L;
        name = "testname";
        nameConfirmed = "testnameConfirmed";
        email = "test@email.test";
        project = 4L;
        applicationId = 2L;
        hash = "123abc";
        organisation = 3L;
        status = InviteStatus.OPENED;
        leadApplicant = "leadApplicant";
        competitionName = "competitionName";

        setInviteResource = new InviteProjectResource();
        setInviteResource.setId(inviteId);
        setInviteResource.setName(name);
        setInviteResource.setNameConfirmed(nameConfirmed);
        setInviteResource.setEmail(email);
        setInviteResource.setApplicationId(applicationId);
        setInviteResource.setHash(hash);
        setInviteResource.setOrganisation(organisation);
        setInviteResource.setStatus(status);
        setInviteResource.setCompetitionName(competitionName);
        setInviteResource.setLeadApplicant(leadApplicant);

        constructedInviteResource = new InviteProjectResource(inviteId, userId, name, email, project, organisation, applicationId, hash, status, leadApplicant, competitionName);

    }

    @Test
    public void constructedProjectInviteResourceShouldReturnCorrectAttributes() throws Exception {
        assertEquals(inviteId, constructedInviteResource.getId());
        assertEquals(name, constructedInviteResource.getName());
        assertEquals(email, constructedInviteResource.getEmail());
        assertEquals(applicationId, constructedInviteResource.getApplicationId());
        assertEquals(hash, constructedInviteResource.getHash());
        assertEquals(organisation, constructedInviteResource.getOrganisation());
        assertEquals(status, constructedInviteResource.getStatus());
        assertEquals(leadApplicant, constructedInviteResource.getLeadApplicant());
        assertEquals(competitionName, constructedInviteResource.getCompetitionName());
    }


    @Test
    public void gettingAnyAttributeAfterSettingShouldReturnCorrectValue() throws Exception {
        assertEquals(inviteId, setInviteResource.getId());
        assertEquals(name, setInviteResource.getName());
        assertEquals(nameConfirmed, setInviteResource.getNameConfirmed());
        assertEquals(email, setInviteResource.getEmail());
        assertEquals(applicationId, setInviteResource.getApplicationId());
        assertEquals(hash, setInviteResource.getHash());
        assertEquals(organisation, setInviteResource.getOrganisation());
        assertEquals(status, setInviteResource.getStatus());
        assertEquals(leadApplicant, setInviteResource.getLeadApplicant());
        assertEquals(competitionName, setInviteResource.getCompetitionName());
    }
}