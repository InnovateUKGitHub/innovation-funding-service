package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.email.resource.EmailContent;
import org.innovateuk.ifs.invite.resource.AssessorInviteToSendResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.email.builders.EmailContentResourceBuilder.newEmailContentResource;
import static org.innovateuk.ifs.invite.builder.AssessorInviteToSendResourceBuilder.newAssessorInviteToSendResource;
import static org.junit.Assert.assertEquals;

public class AssessorInviteToSendResourceBuilderTest {

    @Test
    public void buildOne() {
        String expectedRecipient = "recipient";
        long expectedCompetitionId = 1L;
        String expectedCompetitionName = "comp-name";
        EmailContent expectedEmailContent = newEmailContentResource()
                .withSubject("subject")
                .withPlainText("plain text")
                .build();

        AssessorInviteToSendResource assessorInviteToSendResource = newAssessorInviteToSendResource()
                .withRecipient(expectedRecipient)
                .withCompetitionId(expectedCompetitionId)
                .withCompetitionName(expectedCompetitionName)
                .withEmailContent(expectedEmailContent)
                .build();

        assertEquals(expectedRecipient, assessorInviteToSendResource.getRecipient());
        assertEquals(expectedCompetitionId, assessorInviteToSendResource.getCompetitionId());
        assertEquals(expectedCompetitionName, assessorInviteToSendResource.getCompetitionName());
        assertEquals(expectedEmailContent, assessorInviteToSendResource.getEmailContent());
    }

    @Test
    public void buildMany() {
        String[] expectedRecipients = {"recipient", "other"};
        Long[] expectedCompetitionIds = {1L, 2L};
        String[] expectedCompetitionNames = {"comp-name", "name-comp"};
        EmailContent[] expectedEmailContents = newEmailContentResource()
                .withSubject("subject")
                .withPlainText("plain text")
                .buildArray(2, EmailContent.class);

        List<AssessorInviteToSendResource> resources = newAssessorInviteToSendResource()
                .withRecipient(expectedRecipients)
                .withCompetitionId(expectedCompetitionIds)
                .withCompetitionName(expectedCompetitionNames)
                .withEmailContent(expectedEmailContents)
                .build(2);

        assertEquals(expectedRecipients[0], resources.get(0).getRecipient());
        assertEquals((long) expectedCompetitionIds[0], resources.get(0).getCompetitionId());
        assertEquals(expectedCompetitionNames[0], resources.get(0).getCompetitionName());
        assertEquals(expectedEmailContents[0], resources.get(0).getEmailContent());

        assertEquals(expectedRecipients[1], resources.get(1).getRecipient());
        assertEquals((long) expectedCompetitionIds[1], resources.get(1).getCompetitionId());
        assertEquals(expectedCompetitionNames[1], resources.get(1).getCompetitionName());
        assertEquals(expectedEmailContents[1], resources.get(1).getEmailContent());

    }
}
