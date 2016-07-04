package com.worth.ifs.assessment.controller;

import com.worth.ifs.BaseControllerIntegrationTest;
import com.worth.ifs.assessment.resource.AssessmentResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

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
}
