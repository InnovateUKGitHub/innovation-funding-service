package com.worth.ifs.assessment.controller;

import com.worth.ifs.BaseControllerIntegrationTest;
import com.worth.ifs.assessment.resource.AssessmentOutcomes;
import com.worth.ifs.assessment.resource.AssessmentResource;
import com.worth.ifs.assessment.resource.AssessmentStates;
import com.worth.ifs.workflow.domain.ProcessOutcome;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.util.List;
import java.util.Optional;

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
        String event = "recommend";
        Long processRole = 7L;

        AssessmentResource assessmentResource = controller.findById(assessmentId).getSuccessObject();
        assertEquals(event,assessmentResource.getEvent());
        assertEquals(processRole,assessmentResource.getProcessRole());

        List<Long> processOutcomes = assessmentResource.getProcessOutcomes();
        Optional<Long> processOutcome = processOutcomes.stream().filter(pr -> pr.equals(assessmentId)).findAny();
        assertTrue(processOutcome.isPresent());
        assertEquals(assessmentId,processOutcome.get());
    }

    @Test
    @Rollback
    public void rejectApplication() {
        Long assessmentId = 2L;
        Long processRole = 8L;

        AssessmentResource assessmentResource = controller.findById(assessmentId).getSuccessObject();
        assertEquals(AssessmentStates.OPEN.getState(),assessmentResource.getStatus());
        assertEquals(processRole,assessmentResource.getProcessRole());

        ProcessOutcome processOutcome = new ProcessOutcome();
        processOutcome.setOutcomeType(AssessmentOutcomes.REJECT.getType());
        controller.updateStatus(assessmentResource.getId(),processOutcome);
        AssessmentResource assessmentResult = controller.findById(assessmentId).getSuccessObject();
        assertEquals(AssessmentStates.REJECTED.getState(),assessmentResult.getStatus());
    }
}
