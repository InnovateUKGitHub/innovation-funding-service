package org.innovateuk.ifs.interview.builder;

import org.innovateuk.ifs.interview.domain.InterviewAssignment;
import org.innovateuk.ifs.interview.resource.InterviewAssignmentState;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.interview.resource.InterviewAssignmentState.AWAITING_FEEDBACK_RESPONSE;
import static org.innovateuk.ifs.interview.resource.InterviewAssignmentState.CREATED;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.junit.Assert.assertEquals;

public class InterviewAssignmentBuilderTest {

    @Test
    public void buildOne() {
        Long expectedId = 1L;
        InterviewAssignmentState expectedState = CREATED;
        ProcessRole expectedProcessRole = newProcessRole().build();

        InterviewAssignment interviewAssignment = InterviewAssignmentBuilder.newInterviewAssignment()
                .withId(expectedId)
                .withState(expectedState)
                .withParticipant(expectedProcessRole)
                .build();

        assertEquals(expectedId, interviewAssignment.getId());
        assertEquals(expectedState, interviewAssignment.getProcessState());
        assertEquals(expectedProcessRole, interviewAssignment.getParticipant());
    }

    @Test
    public void buildMany() {
        Long[] expectedIds = {1L, 2L};
        InterviewAssignmentState[] expectedStates = {AWAITING_FEEDBACK_RESPONSE, AWAITING_FEEDBACK_RESPONSE};
        ProcessRole[] expectedProcessRoles = newProcessRole().buildArray(2, ProcessRole.class);

        List<InterviewAssignment> interviewAssignments = InterviewAssignmentBuilder.newInterviewAssignment()
                .withId(expectedIds)
                .withState(expectedStates)
                .withParticipant(expectedProcessRoles)
                .build(2);

        InterviewAssignment first = interviewAssignments.get(0);
        assertEquals(expectedIds[0], first.getId());
        assertEquals(expectedStates[0], first.getProcessState());
        assertEquals(expectedProcessRoles[0], first.getParticipant());

        InterviewAssignment second = interviewAssignments.get(1);
        assertEquals(expectedIds[1], second.getId());
        assertEquals(expectedStates[1], second.getProcessState());
        assertEquals(expectedProcessRoles[1], second.getParticipant());
    }
}