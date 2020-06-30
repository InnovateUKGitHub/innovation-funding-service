package org.innovateuk.ifs.invite.resource;


import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ProjectUserInviteResourceTest {

    ProjectUserInviteResource setInviteResource;
    ProjectUserInviteResource constructedInviteResource;

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
    Long competitionId;

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
        competitionId = 5L;

        setInviteResource = new ProjectUserInviteResource();
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
        setInviteResource.setCompetitionId(competitionId);

        constructedInviteResource = new ProjectUserInviteResource(inviteId, userId, name, email, project, organisation, applicationId, hash, status, leadApplicant, competitionName, competitionId);
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
        assertEquals(competitionId, constructedInviteResource.getCompetitionId());
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
        assertEquals(competitionId, setInviteResource.getCompetitionId());
    }
}
