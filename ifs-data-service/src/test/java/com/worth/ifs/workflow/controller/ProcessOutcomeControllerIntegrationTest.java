package com.worth.ifs.workflow.controller;

import com.worth.ifs.BaseControllerIntegrationTest;
import com.worth.ifs.workflow.resource.ProcessOutcomeResource;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.worth.ifs.assessment.resource.AssessmentOutcomes.FUNDING_DECISION;
import static org.junit.Assert.assertEquals;

public class ProcessOutcomeControllerIntegrationTest extends BaseControllerIntegrationTest<ProcessOutcomeController>  {

    @Override
    @Autowired
    protected void setControllerUnderTest(ProcessOutcomeController controller) {
        this.controller = controller;
    }

    @Test
    public void testGetProcessOutcomeById() {
        Long processOutcomeId = 1L;

        ProcessOutcomeResource processOutcome = controller.findById(processOutcomeId).getSuccessObjectOrThrowException();

        assertEquals("YES",processOutcome.getOutcome());
        assertEquals(FUNDING_DECISION.getType(),processOutcome.getOutcomeType());
    }

}
