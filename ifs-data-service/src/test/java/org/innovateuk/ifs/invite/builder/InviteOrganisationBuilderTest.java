package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.invite.domain.ApplicationInvite;
import org.innovateuk.ifs.invite.domain.InviteOrganisation;
import org.innovateuk.ifs.user.domain.Organisation;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.invite.builder.ApplicationInviteBuilder.newApplicationInvite;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationBuilder.newInviteOrganisation;
import static org.innovateuk.ifs.user.builder.OrganisationBuilder.newOrganisation;
import static org.junit.Assert.assertEquals;

public class InviteOrganisationBuilderTest {

    @Test
    public void buildOne() throws Exception {
        String expectedOrganisationName = "name";
        Organisation expectedOrganisation = newOrganisation().build();
        List<ApplicationInvite> expectedInvites = newApplicationInvite().build(2);

        InviteOrganisation inviteOrganisation = newInviteOrganisation()
                .withOrganisationName(expectedOrganisationName)
                .withOrganisation(expectedOrganisation)
                .withInvites(expectedInvites)
                .build();

        assertEquals(expectedOrganisationName, inviteOrganisation.getOrganisationName());
        assertEquals(expectedOrganisation, inviteOrganisation.getOrganisation());
        assertEquals(expectedInvites, inviteOrganisation.getInvites());
    }

    @Test
    public void buildMany() {
        String[] expectedOrganisationNames = {"name1", "name2"};
        Organisation[] expectedOrganisations = newOrganisation().buildArray(2, Organisation.class);
        @SuppressWarnings("unchecked") List<ApplicationInvite>[] expectedInvites = new List[]{
                newApplicationInvite().build(2),
                newApplicationInvite().build(2)
        };

        List<InviteOrganisation> inviteOrganisations = newInviteOrganisation()
                .withOrganisationName(expectedOrganisationNames)
                .withOrganisation(expectedOrganisations)
                .withInvites(expectedInvites)
                .build(2);

        InviteOrganisation first = inviteOrganisations.get(0);
        assertEquals(expectedOrganisationNames[0], first.getOrganisationName());
        assertEquals(expectedOrganisations[0], first.getOrganisation());
        assertEquals(expectedInvites[0], first.getInvites());

        InviteOrganisation second = inviteOrganisations.get(1);
        assertEquals(expectedOrganisationNames[1], second.getOrganisationName());
        assertEquals(expectedOrganisations[1], second.getOrganisation());
        assertEquals(expectedInvites[1], second.getInvites());
    }
}