package org.innovateuk.ifs.interview.builder;

import org.innovateuk.ifs.interview.domain.Interview;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.workflow.resource.ProcessState;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.interview.resource.InterviewState.ASSIGNED;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.junit.Assert.assertEquals;

public class InterviewBuilderTest {

    @Test
    public void buildOne() {
        Long expectedId = 1L;
        ProcessState expectedStatus = ASSIGNED;
        ProcessRole expectedProcessRole = newProcessRole().build();

        Interview interview = InterviewBuilder.newInterview()
                .withId(expectedId)
                .withState(ASSIGNED)
                .withParticipant(expectedProcessRole)
                .build();

        assertEquals(expectedId, interview.getId());
        assertEquals(expectedStatus, interview.getProcessState());
        assertEquals(expectedProcessRole, interview.getParticipant());
    }

    @Test
    public void buildMany() {
        Long[] expectedIds = {1L, 2L};
        ProcessState[] expectedStatuses = {ASSIGNED, ASSIGNED};
        ProcessRole[] expectedProcessRoles = newProcessRole().buildArray(2, ProcessRole.class);

        List<Interview> interviews = InterviewBuilder.newInterview()
                .withId(expectedIds)
                .withState(ASSIGNED, ASSIGNED)
                .withParticipant(expectedProcessRoles)
                .build(2);

        Interview first = interviews.get(0);
        assertEquals(expectedIds[0], first.getId());
        assertEquals(expectedStatuses[0], first.getProcessState());
        assertEquals(expectedProcessRoles[0], first.getParticipant());

        Interview second = interviews.get(1);
        assertEquals(expectedIds[1], second.getId());
        assertEquals(expectedStatuses[1], second.getProcessState());
        assertEquals(expectedProcessRoles[1], second.getParticipant());
    }
}