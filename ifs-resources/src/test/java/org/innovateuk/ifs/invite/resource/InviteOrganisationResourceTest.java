package org.innovateuk.ifs.invite.resource;

import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.invite.builder.ApplicationInviteResourceBuilder.newApplicationInviteResource;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;

public class InviteOrganisationResourceTest {
    InviteOrganisationResource setInviteOrganisationResource;
    InviteOrganisationResource constructedInviteOrganisationResource;

    String name;
    String type;
    OrganisationResource organisation;
    List<ApplicationInviteResource> invites;
    Long id;

    @Before
    public void setUp() throws Exception {
        name = "organisationTestName";
        type = "organisationTypeName";
        organisation = newOrganisationResource().build();
        invites = newApplicationInviteResource().build(5);
        id = 1L;

        setInviteOrganisationResource = new InviteOrganisationResource();
        setInviteOrganisationResource.setId(id);
        setInviteOrganisationResource.setInviteResources(invites);
        setInviteOrganisationResource.setOrganisationName(name);
        setInviteOrganisationResource.setOrganisation(organisation.getId());

        constructedInviteOrganisationResource = new InviteOrganisationResource(id, name, type, organisation.getId(), invites);
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
