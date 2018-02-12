package org.innovateuk.ifs.assessment.interview.builder;

import org.innovateuk.ifs.assessment.interview.domain.AssessmentInterviewPanel;
import org.innovateuk.ifs.assessment.interview.resource.AssessmentInterviewPanelState;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.assessment.interview.builder.AssessmentInterviewPanelBuilder.newAssessmentInterviewPanel;
import static org.innovateuk.ifs.assessment.interview.resource.AssessmentInterviewPanelState.*;
import static org.innovateuk.ifs.assessment.interview.resource.AssessmentInterviewPanelState.AWAITING_FEEDBACK_RESPONSE;
import static org.innovateuk.ifs.user.builder.ProcessRoleBuilder.newProcessRole;
import static org.junit.Assert.assertEquals;

public class AssessmentInterviewPanelBuilderTest {

    @Test
    public void buildOne() {
        Long expectedId = 1L;
        AssessmentInterviewPanelState expectedState = CREATED;
        ProcessRole expectedProcessRole = newProcessRole().build();

        AssessmentInterviewPanel assessmentInterviewPanel = newAssessmentInterviewPanel()
                .withId(expectedId)
                .withState(expectedState)
                .withParticipant(expectedProcessRole)
                .build();

        assertEquals(expectedId, assessmentInterviewPanel.getId());
        assertEquals(expectedState, assessmentInterviewPanel.getActivityState());
        assertEquals(expectedProcessRole, assessmentInterviewPanel.getParticipant());
    }

    @Test
    public void buildMany() {
        Long[] expectedIds = {1L, 2L};
        AssessmentInterviewPanelState[] expectedStates = {AWAITING_FEEDBACK_RESPONSE, AWAITING_FEEDBACK_RESPONSE};
        ProcessRole[] expectedProcessRoles = newProcessRole().buildArray(2, ProcessRole.class);

        List<AssessmentInterviewPanel> assessmentInterviewPanels = newAssessmentInterviewPanel()
                .withId(expectedIds)
                .withState(expectedStates)
                .withParticipant(expectedProcessRoles)
                .build(2);

        AssessmentInterviewPanel first = assessmentInterviewPanels.get(0);
        assertEquals(expectedIds[0], first.getId());
        assertEquals(expectedStates[0], first.getActivityState());
        assertEquals(expectedProcessRoles[0], first.getParticipant());

        AssessmentInterviewPanel second = assessmentInterviewPanels.get(1);
        assertEquals(expectedIds[1], second.getId());
        assertEquals(expectedStates[1], second.getActivityState());
        assertEquals(expectedProcessRoles[1], second.getParticipant());
    }
}