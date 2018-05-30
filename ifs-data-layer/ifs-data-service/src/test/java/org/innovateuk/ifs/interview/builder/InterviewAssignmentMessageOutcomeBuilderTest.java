package org.innovateuk.ifs.interview.builder;

import org.innovateuk.ifs.file.domain.FileEntry;
import org.innovateuk.ifs.interview.domain.InterviewAssignmentMessageOutcome;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.util.List;

import static org.innovateuk.ifs.file.builder.FileEntryBuilder.newFileEntry;
import static org.innovateuk.ifs.interview.builder.InterviewAssignmentMessageOutcomeBuilder.newInterviewAssignmentMessageOutcome;
import static org.junit.Assert.assertEquals;

public class InterviewAssignmentMessageOutcomeBuilderTest {

    @Test
    public void buildOne() {
        Long expectedId = 7L;
        String expectedSubject = "subject";
        String expectedMessage = "message";
        FileEntry expectedFeedback = newFileEntry().build();
        ZonedDateTime expectedCreatedOn = ZonedDateTime.now();
        ZonedDateTime expectedModifiedOn = ZonedDateTime.now().plusMinutes(1);

        InterviewAssignmentMessageOutcome messageOutcome = newInterviewAssignmentMessageOutcome()
                .withId(expectedId)
                .withSubject(expectedSubject)
                .withMessage(expectedMessage)
                .withFeedback(expectedFeedback)
                .withCreatedOn(expectedCreatedOn)
                .withModifiedOn(expectedModifiedOn)
                .build();

        assertEquals(expectedId, messageOutcome.getId());
        assertEquals(expectedSubject, messageOutcome.getSubject());
        assertEquals(expectedMessage, messageOutcome.getMessage());
        assertEquals(expectedFeedback, messageOutcome.getFeedback());
        assertEquals(expectedCreatedOn, messageOutcome.getCreatedOn());
        assertEquals(expectedModifiedOn, messageOutcome.getModifiedOn());
    }

    @Test
    public void buildMany() {
        Long[] expectedIds = { 7L, 13L };
        String[] expectedSubjects = {"subject1", "subject2"};
        String[] expectedMessages = {"message1", "message2"};
        FileEntry[] expectedFeedbacks = newFileEntry().buildArray(2, FileEntry.class);
        ZonedDateTime[] expectedCreatedOns = {ZonedDateTime.now(), ZonedDateTime.now().plusMinutes(1)};
        ZonedDateTime[] expectedModifiedOns ={ZonedDateTime.now().plusMinutes(2), ZonedDateTime.now().plusMinutes(3)};

        List<InterviewAssignmentMessageOutcome> messageOutcomes = newInterviewAssignmentMessageOutcome()
                .withId(expectedIds)
                .withSubject(expectedSubjects)
                .withMessage(expectedMessages)
                .withFeedback(expectedFeedbacks)
                .withCreatedOn(expectedCreatedOns)
                .withModifiedOn(expectedModifiedOns)
                .build(2);

        InterviewAssignmentMessageOutcome first = messageOutcomes.get(0);
        assertEquals(expectedIds[0], first.getId());
        assertEquals(expectedSubjects[0], first.getSubject());
        assertEquals(expectedMessages[0], first.getMessage());
        assertEquals(expectedFeedbacks[0], first.getFeedback());
        assertEquals(expectedCreatedOns[0], first.getCreatedOn());
        assertEquals(expectedModifiedOns[0], first.getModifiedOn());

        InterviewAssignmentMessageOutcome second = messageOutcomes.get(1);
        assertEquals(expectedIds[1], second.getId());
        assertEquals(expectedSubjects[1], second.getSubject());
        assertEquals(expectedMessages[1], second.getMessage());
        assertEquals(expectedFeedbacks[1], second.getFeedback());
        assertEquals(expectedCreatedOns[1], second.getCreatedOn());
        assertEquals(expectedModifiedOns[1], second.getModifiedOn());
    }
}
