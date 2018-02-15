package org.innovateuk.ifs.assessment.interview.builder;

import org.innovateuk.ifs.interview.domain.Interview;
import org.innovateuk.ifs.interview.resource.InterviewState;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.workflow.resource.ProcessState;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.assessment.interview.builder.AssessmentInterviewBuilder.newAssessmentInterview;
import static org.innovateuk.ifs.interview.resource.InterviewState.ACCEPTED;
import static org.innovateuk.ifs.interview.resource.InterviewState.PENDING;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.junit.Assert.assertEquals;

public class InterviewBuilderTest {

    @Test
    public void buildOne() throws Exception {
        Long expectedId = 1L;
        ProcessState expectedStatus = ACCEPTED;
        ProcessRole expectedProcessRole = newProcessRole().build();

        Interview interview = newAssessmentInterview()
                .withId(expectedId)
                .withState(ACCEPTED)
                .withParticipant(expectedProcessRole)
                .build();

        assertEquals(expectedId, interview.getId());
        assertEquals(expectedStatus, interview.getActivityState());
        assertEquals(expectedProcessRole, interview.getParticipant());
    }

    @Test
    public void buildMany() {
        Long[] expectedIds = {1L, 2L};
        ProcessState[] expectedStatuses = {InterviewState.PENDING, ACCEPTED};
        ProcessRole[] expectedProcessRoles = newProcessRole().buildArray(2, ProcessRole.class);

        List<Interview> interviews = newAssessmentInterview()
                .withId(expectedIds)
                .withState(PENDING, ACCEPTED)
                .withParticipant(expectedProcessRoles)
                .build(2);

        Interview first = interviews.get(0);
        assertEquals(expectedIds[0], first.getId());
        assertEquals(expectedStatuses[0], first.getActivityState());
        assertEquals(expectedProcessRoles[0], first.getParticipant());

        Interview second = interviews.get(1);
        assertEquals(expectedIds[1], second.getId());
        assertEquals(expectedStatuses[1], second.getActivityState());
        assertEquals(expectedProcessRoles[1], second.getParticipant());
    }
}
