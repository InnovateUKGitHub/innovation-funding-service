package com.worth.ifs.invite.resource;

import com.worth.ifs.user.resource.OrganisationResource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static com.worth.ifs.invite.builder.InviteResourceBuilder.newInviteResource;
import static com.worth.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;

public class InviteOrganisationResourceTest {
    InviteOrganisationResource setInviteOrganisationResource;
    InviteOrganisationResource constructedInviteOrganisationResource;

    String name;
    OrganisationResource organisation;
    List<ApplicationInviteResource> invites;
    Long id;

    @Before
    public void setUp() throws Exception {
        name = "organisationTestName";
        organisation = newOrganisationResource().build();
        invites = newInviteResource().build(5);
        id = 1L;

        setInviteOrganisationResource = new InviteOrganisationResource();
        setInviteOrganisationResource.setId(id);
        setInviteOrganisationResource.setInviteResources(invites);
        setInviteOrganisationResource.setOrganisationName(name);
        setInviteOrganisationResource.setOrganisation(organisation.getId());

        constructedInviteOrganisationResource = new InviteOrganisationResource(id, name, organisation.getId(), invites);
    }

    @Test
    public void gettingAnyAttributeAfterSettingShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(id, setInviteOrganisationResource.getId());
        Assert.assertEquals(invites, setInviteOrganisationResource.getInviteResources());
        Assert.assertEquals(name, setInviteOrganisationResource.getOrganisationName());
        Assert.assertEquals(organisation.getId(), setInviteOrganisationResource.getOrganisation());
    }

    @Test
    public void constructedInviteShouldReturnCorrectAttributeValues() throws Exception {
        Assert.assertEquals(id, constructedInviteOrganisationResource.getId());
        Assert.assertEquals(invites, constructedInviteOrganisationResource.getInviteResources());
        Assert.assertEquals(name, constructedInviteOrganisationResource.getOrganisationName());
        Assert.assertEquals(organisation.getId(), constructedInviteOrganisationResource.getOrganisation());
    }
}