package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.invite.resource.AssessorInvitesToSendResource;
import org.junit.Test;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.invite.builder.AssessorInvitesToSendResourceBuilder.newAssessorInvitesToSendResource;
import static org.junit.Assert.assertEquals;

public class AssessorInvitesToSendResourceBuilderTest {

    @Test
    public void buildOne() {
        List<String> expectedRecipient = singletonList("recipient");
        long expectedCompetitionId = 1L;
        String expectedCompetitionName = "comp-name";
        String expectedContent = "content";

        AssessorInvitesToSendResource assessorInviteToSendResource = newAssessorInvitesToSendResource()
                .withRecipients(expectedRecipient)
                .withCompetitionId(expectedCompetitionId)
                .withCompetitionName(expectedCompetitionName)
                .withContent(expectedContent)
                .build();

        assertEquals(expectedRecipient, assessorInviteToSendResource.getRecipients());
        assertEquals(expectedCompetitionId, assessorInviteToSendResource.getCompetitionId());
        assertEquals(expectedCompetitionName, assessorInviteToSendResource.getCompetitionName());
        assertEquals(expectedContent, assessorInviteToSendResource.getContent());
    }

    @Test
    public void buildMany() {
        List<String> expectedRecipients1 = singletonList("recipient");
        List<String> expectedRecipients2 = singletonList("other");
        Long[] expectedCompetitionIds = {1L, 2L};
        String[] expectedCompetitionNames = {"comp-name", "name-comp"};
        String[] expectedContent = {"content1", "content2"};

        List<AssessorInvitesToSendResource> resources = newAssessorInvitesToSendResource()
                .withRecipients(expectedRecipients1, expectedRecipients2)
                .withCompetitionId(expectedCompetitionIds)
                .withCompetitionName(expectedCompetitionNames)
                .withContent(expectedContent)
                .build(2);

        assertEquals(expectedRecipients1, resources.get(0).getRecipients());
        assertEquals((long) expectedCompetitionIds[0], resources.get(0).getCompetitionId());
        assertEquals(expectedCompetitionNames[0], resources.get(0).getCompetitionName());
        assertEquals(expectedContent[0], resources.get(0).getContent());

        assertEquals(expectedRecipients2, resources.get(1).getRecipients());
        assertEquals((long) expectedCompetitionIds[1], resources.get(1).getCompetitionId());
        assertEquals(expectedCompetitionNames[1], resources.get(1).getCompetitionName());
        assertEquals(expectedContent[1], resources.get(1).getContent());
    }
}
