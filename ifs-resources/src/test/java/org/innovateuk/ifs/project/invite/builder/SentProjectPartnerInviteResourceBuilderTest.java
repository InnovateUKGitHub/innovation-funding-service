package org.innovateuk.ifs.project.invite.builder;

import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.project.invite.resource.SentProjectPartnerInviteResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.invite.constant.InviteStatus.OPENED;
import static org.innovateuk.ifs.invite.constant.InviteStatus.SENT;
import static org.innovateuk.ifs.project.invite.builder.SentProjectPartnerInviteResourceBuilder.newSentProjectPartnerInviteResource;
import static org.junit.Assert.*;

public class SentProjectPartnerInviteResourceBuilderTest {

    @Test
    public void buildOne() {
        String expectedUserName = "Nick White";
        String expectedOrganisationName = "Whitey Ltd";
        String expectedEmail = "nick.white@whitey.com";
        InviteStatus expectedStatus = SENT;

        SentProjectPartnerInviteResource sentProjectPartnerInviteResourceBuilder = newSentProjectPartnerInviteResource()
                .withOrganisationName(expectedOrganisationName)
                .withStatus(expectedStatus)
                .withUserName(expectedUserName)
                .withEmail(expectedEmail)
                .build();

        assertEquals(expectedUserName, sentProjectPartnerInviteResourceBuilder.getUserName());
        assertEquals(expectedOrganisationName, sentProjectPartnerInviteResourceBuilder.getOrganisationName());
        assertEquals(expectedEmail, sentProjectPartnerInviteResourceBuilder.getEmail());
        assertEquals(expectedStatus, sentProjectPartnerInviteResourceBuilder.getStatus());
    }

    @Test
    public void buildMany() {
        String[] expectedUserNames = {"Nick White", "Tim Russell"};
        String[] expectedOrganisationNames = {"Whitey Ltd", "Russell Ltd"};
        String[] expectedEmails = {"nick.white@whitey.com", "tim@russell.com"};
        InviteStatus[] expectedStatuses = {SENT, OPENED};

        List<SentProjectPartnerInviteResource> sentProjectPartnerInviteResourceBuilder = newSentProjectPartnerInviteResource()
                .withOrganisationName(expectedOrganisationNames)
                .withStatus(expectedStatuses)
                .withUserName(expectedUserNames)
                .withEmail(expectedEmails)
                .build(2);

        assertEquals(expectedUserNames[0], sentProjectPartnerInviteResourceBuilder.get(0).getUserName());
        assertEquals(expectedOrganisationNames[0], sentProjectPartnerInviteResourceBuilder.get(0).getOrganisationName());
        assertEquals(expectedEmails[0], sentProjectPartnerInviteResourceBuilder.get(0).getEmail());
        assertEquals(expectedStatuses[0], sentProjectPartnerInviteResourceBuilder.get(0).getStatus());

        assertEquals(expectedUserNames[1], sentProjectPartnerInviteResourceBuilder.get(1).getUserName());
        assertEquals(expectedOrganisationNames[1], sentProjectPartnerInviteResourceBuilder.get(1).getOrganisationName());
        assertEquals(expectedEmails[1], sentProjectPartnerInviteResourceBuilder.get(1).getEmail());
        assertEquals(expectedStatuses[1], sentProjectPartnerInviteResourceBuilder.get(1).getStatus());
    }

}