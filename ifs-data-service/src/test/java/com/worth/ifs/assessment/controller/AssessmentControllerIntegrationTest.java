package com.worth.ifs.assessment.controller;

import com.worth.ifs.BaseControllerIntegrationTest;
import com.worth.ifs.assessment.resource.AssessmentOutcomes;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.assessment.resource.AssessmentStates;
import com.worth.ifs.workflow.resource.ProcessOutcomeResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static com.worth.ifs.assessment.builder.ProcessOutcomeResourceBuilder.newProcessOutcomeResource;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AssessmentControllerIntegrationTest extends BaseControllerIntegrationTest<AssessmentController> {

    @Before
    public void setUp() throws Exception {
    }

    @Autowired
    @Override
    protected void setControllerUnderTest(final AssessmentController controller) {
        this.controller = controller;
    }

    @Test
    public void findById() {
        Long assessmentId = 1L;
        Long processRole = 7L;

        loginCompAdmin();
        AssessmentResource assessmentResource = controller.findById(assessmentId).getSuccessObject();
        assertEquals("recommend", assessmentResource.getEvent());
        assertEquals(processRole, assessmentResource.getProcessRole());

        List<Long> processOutcomes = assessmentResource.getProcessOutcomes();
        Optional<Long> processOutcome = processOutcomes.stream().filter(pr -> pr.equals(assessmentId)).findAny();
        assertTrue(processOutcome.isPresent());
        assertEquals(assessmentId, processOutcome.get());
    }

    @Test
    public void recommend() throws Exception {
        Long assessmentId = 2L;
        Long processRole = 8L;

        loginPaulPlum();
        AssessmentResource assessmentResource = controller.findById(assessmentId).getSuccessObject();
        assertEquals(AssessmentStates.OPEN.getState(), assessmentResource.getStatus());
        assertEquals(processRole, assessmentResource.getProcessRole());

        ProcessOutcomeResource processOutcome = newProcessOutcomeResource()
                .withOutcomeType(AssessmentOutcomes.RECOMMEND.getType())
                .build();
        controller.recommend(assessmentResource.getId(), processOutcome);

        AssessmentResource assessmentResult = controller.findById(assessmentId).getSuccessObject();
        assertEquals(AssessmentStates.ASSESSED.getState(), assessmentResult.getStatus());
    }

    @Test
    public void rejectInvitation() {
        Long assessmentId = 2L;
        Long processRole = 8L;

        loginPaulPlum();
        AssessmentResource assessmentResource = controller.findById(assessmentId).getSuccessObject();
        assertEquals(AssessmentStates.OPEN.getState(), assessmentResource.getStatus());
        assertEquals(processRole, assessmentResource.getProcessRole());

        ProcessOutcomeResource processOutcome = newProcessOutcomeResource()
                .withOutcomeType(AssessmentOutcomes.REJECT.getType())
                .build();
        controller.rejectInvitation(assessmentResource.getId(), processOutcome);

        AssessmentResource assessmentResult = controller.findById(assessmentId).getSuccessObject();
        assertEquals(AssessmentStates.REJECTED.getState(), assessmentResult.getStatus());
    }
}
