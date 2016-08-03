package com.worth.ifs.invite.domain;

import com.worth.ifs.user.domain.Organisation;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.invite.builder.ApplicationInviteBuilder.newInvite;
import static com.worth.ifs.user.builder.OrganisationBuilder.newOrganisation;

public class InviteOrganisationTest {
    InviteOrganisation setInviteOrganisation;
    InviteOrganisation constructedInviteOrganisation;

    String name;
    Organisation organisation;
    List<ApplicationInvite> invites;
    Long id;

    @Before
    public void setUp() throws Exception {
        name = "organisationTestName";
        organisation = newOrganisation().build();
        invites = newInvite().build(5);
        id = 1L;

        setInviteOrganisation = new InviteOrganisation();
        setInviteOrganisation.setId(id);
        setInviteOrganisation.setInvites(invites);
        setInviteOrganisation.setOrganisationName(name);
        setInviteOrganisation.setOrganisation(organisation);

        constructedInviteOrganisation = new InviteOrganisation(name, organisation, invites);
    }

    @Test
    public void gettingAnyAttributeAfterSettingShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(id, setInviteOrganisation.getId());
        Assert.assertEquals(invites, setInviteOrganisation.getInvites());
        Assert.assertEquals(name, setInviteOrganisation.getOrganisationName());
        Assert.assertEquals(organisation, setInviteOrganisation.getOrganisation());
    }

    @Test
    public void constructedInviteShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(invites, constructedInviteOrganisation.getInvites());
        Assert.assertEquals(name, constructedInviteOrganisation.getOrganisationName());
        Assert.assertEquals(organisation, constructedInviteOrganisation.getOrganisation());
    }
}