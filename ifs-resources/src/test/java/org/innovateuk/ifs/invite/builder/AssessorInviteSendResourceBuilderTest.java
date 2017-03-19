package org.innovateuk.ifs.invite.builder;

import org.innovateuk.ifs.invite.resource.AssessorInviteSendResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.invite.builder.AssessorInviteSendResourceBuilder.newAssessorInviteSendResource;
import static org.junit.Assert.assertEquals;

public class AssessorInviteSendResourceBuilderTest {

    @Test
    public void buildOne() {
        String expectedSubject = "subject";
        String expectedContent = "content";

        AssessorInviteSendResource assessorInviteSendResource = newAssessorInviteSendResource()
                .withSubject(expectedSubject)
                .withContent(expectedContent)
                .build();

        assertEquals(expectedSubject, assessorInviteSendResource.getSubject());
        assertEquals(expectedContent, assessorInviteSendResource.getContent());
    }

    @Test
    public void buildMany() {
        String[] expectedSubject = {"subject1", "subject2"};
        String[] expectedContent = {"content1", "content2"};

        List<AssessorInviteSendResource> assessorInviteSendResources = newAssessorInviteSendResource()
                .withSubject(expectedSubject)
                .withContent(expectedContent)
                .build(2);

        AssessorInviteSendResource first = assessorInviteSendResources.get(0);
        assertEquals(expectedSubject[0], first.getSubject());
        assertEquals(expectedContent[0], first.getContent());

        AssessorInviteSendResource second = assessorInviteSendResources.get(1);
        assertEquals(expectedSubject[1], second.getSubject());
        assertEquals(expectedContent[1], second.getContent());
    }
}