package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.invite.domain.ApplicationInvite;
import org.innovateuk.ifs.invite.domain.InviteOrganisation;
import org.innovateuk.ifs.user.domain.User;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.application.builder.ApplicationBuilder.newApplication;
import static org.innovateuk.ifs.invite.builder.ApplicationInviteBuilder.newApplicationInvite;
import static org.innovateuk.ifs.invite.builder.InviteOrganisationBuilder.newInviteOrganisation;
import static org.innovateuk.ifs.user.builder.UserBuilder.newUser;
import static org.junit.Assert.assertEquals;

public class ApplicationInviteBuilderTest {

    @Test
    public void buildOne() {
        Long expectedId = 7L;
        String expectedName = "name";
        String expectedEmail = "example@test.com";
        User expectedUser = newUser().build();
        Application expectedApplication = newApplication().build();
        InviteOrganisation expectedInviteOrganisation = newInviteOrganisation().build();

        ApplicationInvite applicationInvite = newApplicationInvite()
                .withId(expectedId)
                .withName(expectedName)
                .withEmail(expectedEmail)
                .withUser(expectedUser)
                .withApplication(expectedApplication)
                .withInviteOrganisation(expectedInviteOrganisation)
                .build();

        assertEquals(expectedId, applicationInvite.getId());
        assertEquals(expectedName, applicationInvite.getName());
        assertEquals(expectedEmail, applicationInvite.getEmail());
        assertEquals(expectedUser, applicationInvite.getUser());
        assertEquals(expectedApplication, applicationInvite.getTarget());
        assertEquals(expectedInviteOrganisation, applicationInvite.getInviteOrganisation());
    }

    @Test
    public void buildMany() {
        Long[] expectedIds = {7L, 13L};
        String[] expectedNames = {"name1", "name2"};
        String[] expectedEmails = {"example1@test.com", "example2@test.com"};
        User[] expectedUsers = newUser().withId(5L, 11L).buildArray(2, User.class);
        Application[] expectedApplications = newApplication().buildArray(2, Application.class);
        InviteOrganisation[] expectedInviteOrganisations = newInviteOrganisation().buildArray(2, InviteOrganisation.class);

        List<ApplicationInvite> applicationInvites = newApplicationInvite()
                .withId(expectedIds)
                .withName(expectedNames)
                .withEmail(expectedEmails)
                .withUser(expectedUsers)
                .withApplication(expectedApplications)
                .withInviteOrganisation(expectedInviteOrganisations)
                .build(2);

        ApplicationInvite first = applicationInvites.get(0);
        assertEquals(expectedIds[0], first.getId());
        assertEquals(expectedNames[0], first.getName());
        assertEquals(expectedEmails[0], first.getEmail());
        assertEquals(expectedUsers[0], first.getUser());
        assertEquals(expectedApplications[0], first.getTarget());
        assertEquals(expectedInviteOrganisations[0], first.getInviteOrganisation());

        ApplicationInvite second = applicationInvites.get(1);
        assertEquals(expectedIds[1], second.getId());
        assertEquals(expectedNames[1], second.getName());
        assertEquals(expectedEmails[1], second.getEmail());
        assertEquals(expectedUsers[1], second.getUser());
        assertEquals(expectedApplications[1], second.getTarget());
        assertEquals(expectedInviteOrganisations[1], second.getInviteOrganisation());
    }
}