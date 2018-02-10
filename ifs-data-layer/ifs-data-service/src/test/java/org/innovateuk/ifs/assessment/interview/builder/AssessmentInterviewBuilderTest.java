package org.innovateuk.ifs.assessment.interview.builder;

import org.innovateuk.ifs.assessment.interview.domain.AssessmentInterview;
import org.innovateuk.ifs.assessment.interview.resource.AssessmentInterviewState;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.workflow.resource.ProcessState;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.assessment.interview.builder.AssessmentInterviewBuilder.newAssessmentInterview;
import static org.innovateuk.ifs.assessment.interview.resource.AssessmentInterviewState.ACCEPTED;
import static org.innovateuk.ifs.assessment.interview.resource.AssessmentInterviewState.PENDING;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.junit.Assert.assertEquals;

public class AssessmentInterviewBuilderTest {

    @Test
    public void buildOne() throws Exception {
        Long expectedId = 1L;
        ProcessState expectedStatus = ACCEPTED;
        ProcessRole expectedProcessRole = newProcessRole().build();

        AssessmentInterview assessmentInterview = newAssessmentInterview()
                .withId(expectedId)
                .withState(ACCEPTED)
                .withParticipant(expectedProcessRole)
                .build();

        assertEquals(expectedId, assessmentInterview.getId());
        assertEquals(expectedStatus, assessmentInterview.getActivityState());
        assertEquals(expectedProcessRole, assessmentInterview.getParticipant());
    }

    @Test
    public void buildMany() {
        Long[] expectedIds = {1L, 2L};
        ProcessState[] expectedStatuses = {AssessmentInterviewState.PENDING, ACCEPTED};
        ProcessRole[] expectedProcessRoles = newProcessRole().buildArray(2, ProcessRole.class);

        List<AssessmentInterview> AssessmentInterviews = newAssessmentInterview()
                .withId(expectedIds)
                .withState(PENDING, ACCEPTED)
                .withParticipant(expectedProcessRoles)
                .build(2);

        AssessmentInterview first = AssessmentInterviews.get(0);
        assertEquals(expectedIds[0], first.getId());
        assertEquals(expectedStatuses[0], first.getActivityState());
        assertEquals(expectedProcessRoles[0], first.getParticipant());

        AssessmentInterview second = AssessmentInterviews.get(1);
        assertEquals(expectedIds[1], second.getId());
        assertEquals(expectedStatuses[1], second.getActivityState());
        assertEquals(expectedProcessRoles[1], second.getParticipant());
    }
}
