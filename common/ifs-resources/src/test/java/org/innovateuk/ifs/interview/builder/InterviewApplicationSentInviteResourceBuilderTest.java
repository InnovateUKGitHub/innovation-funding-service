package org.innovateuk.ifs.interview.builder;

import org.innovateuk.ifs.interview.resource.InterviewApplicationSentInviteResource;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.List;

import static org.innovateuk.ifs.interview.builder.InterviewApplicationSentInviteResourceBuilder.newInterviewApplicationSentInviteResource;
import static org.junit.Assert.assertEquals;

public class InterviewApplicationSentInviteResourceBuilderTest {
    @Test
    public void buildOne() {
        String expectedSubject = "subject";
        String expectedContent = "content";
        ZonedDateTime expectedAssigned = ZonedDateTime.now();


        InterviewApplicationSentInviteResource invite = newInterviewApplicationSentInviteResource()
                .withSubject(expectedSubject)
                .withContent(expectedContent)
                .withAssigned(expectedAssigned)
                .build();

        assertEquals(expectedSubject, invite.getSubject());
        assertEquals(expectedContent, invite.getContent());
        assertEquals(expectedAssigned, invite.getAssigned());
    }

    @Test
    public void buildMany() {
        String[] expectedSubject = {"subject", "subject2"};
        String[] expectedContent = {"content", "content2"};
        ZonedDateTime[] expectedAssigned = {ZonedDateTime.now(), ZonedDateTime.now().plusDays(1)};

        List<InterviewApplicationSentInviteResource> invites = newInterviewApplicationSentInviteResource()
                .withSubject(expectedSubject)
                .withContent(expectedContent)
                .withAssigned(expectedAssigned)
                .build(2);

        assertEquals(expectedSubject[0], invites.get(0).getSubject());
        assertEquals(expectedContent[0], invites.get(0).getContent());
        assertEquals(expectedAssigned[0], invites.get(0).getAssigned());

        assertEquals(expectedSubject[1], invites.get(1).getSubject());
        assertEquals(expectedContent[1], invites.get(1).getContent());
        assertEquals(expectedAssigned[1], invites.get(1).getAssigned());
    }
}
