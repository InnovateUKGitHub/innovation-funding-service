package org.innovateuk.ifs.invite.domain;


import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.organisation.domain.Organisation;
import org.innovateuk.ifs.project.core.domain.Project;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ProjectUserInviteTest {

    private ProjectUserInvite setInvite;
    private ProjectUserInvite constructedInvite;
    private Long inviteId;
    private String name;
    private String email;
    private Project project;
    private String hash;
    private Organisation organisation;
    private InviteStatus status;

    @Before
    public void setUp() throws Exception {
        inviteId = 1L;
        name = "testnameforProject";
        email = "testproject-invite@email.test";
        project = new Project();
        hash = "123abcdef";
        organisation = new Organisation();

        setInvite = new ProjectUserInvite();
        setInvite.setId(inviteId);
        setInvite.setName(name);
        setInvite.setEmail(email);
        setInvite.setTarget(project);
        setInvite.setHash(hash);
        setInvite.setOrganisation(organisation);

        constructedInvite = new ProjectUserInvite(name, email,hash, organisation, project, status);
    }

    @Test
    public void constructedProjectInviteShouldReturnCorrectAttributes() throws Exception {
        assertEquals(name, constructedInvite.getName());
        assertEquals(email, constructedInvite.getEmail());
        assertEquals(project, constructedInvite.getTarget());
        assertEquals(hash, constructedInvite.getHash());
        assertEquals(organisation, constructedInvite.getOrganisation());
        assertEquals(status, constructedInvite.getStatus());
    }

    @Test
    public void gettingProjectInviteAnyAttributeAfterSettingShouldReturnCorrectValue() throws Exception {
        assertEquals(inviteId, setInvite.getId());
        assertEquals(name, setInvite.getName());
        assertEquals(email, setInvite.getEmail());
        assertEquals(project, setInvite.getTarget());
        assertEquals(hash, setInvite.getHash());
        assertEquals(organisation, setInvite.getOrganisation());
    }
}