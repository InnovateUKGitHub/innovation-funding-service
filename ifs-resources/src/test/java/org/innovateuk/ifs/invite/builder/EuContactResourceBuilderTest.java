package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.invite.resource.EuContactResource;
import org.junit.Test;

import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.innovateuk.ifs.invite.builder.EuContactResourceBuilder.newEuContactResource;
import static org.junit.Assert.assertEquals;

public class EuContactResourceBuilderTest {


    @Test
    public void buildOne() {
        String expectedName = "John Smith";
        Boolean expectedNotified = FALSE;
        String expectedEmail = "john@smith.com";
        String expectedTelephone = "01234";
        String expectedJobTitle = "newsagent";

        EuContactResource euContactResource = newEuContactResource()
                .withName(expectedName)
                .withNotified(expectedNotified)
                .withEmail(expectedEmail)
                .withTelephone(expectedTelephone)
                .withJobTitle(expectedJobTitle)
                .build();

        assertEquals(expectedName, euContactResource.getName());
        assertEquals(expectedNotified, euContactResource.getNotified());
        assertEquals(expectedEmail, euContactResource.getEmail());
        assertEquals(expectedJobTitle, euContactResource.getJobTitle());
        assertEquals(expectedTelephone, euContactResource.getTelephone());
    }

    @Test
    public void buildMany() {
        String[] expectedNames = {"John Doe", "Jane Doe"};
        Boolean[] expectedNotified = {TRUE, FALSE};
        String[] expectedEmails = {"john@doe.com", "jane@doe.com"};
        String[] expectedTelephones = {"9876", "6789"};
        String[] expectedJobTitles = {"nurse", "doctor"};

        List<EuContactResource> euContactResources = newEuContactResource()
                .withName(expectedNames)
                .withNotified(expectedNotified)
                .withEmail(expectedEmails)
                .withTelephone(expectedTelephones)
                .withJobTitle(expectedJobTitles)
                .build(2);

        EuContactResource first = euContactResources.get(0);
        assertEquals(expectedNames[0], first.getName());
        assertEquals(expectedNotified[0], first.getNotified());
        assertEquals(expectedEmails[0], first.getEmail());
        assertEquals(expectedTelephones[0], first.getTelephone());
        assertEquals(expectedJobTitles[0], first.getJobTitle());

        EuContactResource second = euContactResources.get(1);
        assertEquals(expectedNames[1], second.getName());
        assertEquals(expectedNotified[1], second.getNotified());
        assertEquals(expectedEmails[1], second.getEmail());
        assertEquals(expectedTelephones[1], second.getTelephone());
        assertEquals(expectedJobTitles[1], second.getJobTitle());
    }
}
