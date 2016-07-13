package com.worth.ifs.workflow.controller;

import com.worth.ifs.BaseControllerIntegrationTest;
import com.worth.ifs.assessment.resource.AssessmentOutcomes;
import com.worth.ifs.workflow.resource.ProcessOutcomeResource;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

public class ProcessOutcomeControllerIntegrationTest extends BaseControllerIntegrationTest<ProcessOutcomeController>  {

    @Override
    @Autowired
    protected void setControllerUnderTest(ProcessOutcomeController controller) {
        this.controller = controller;
    }

    public static final long PROCESS_OUTCOME_ID = 1L;

    @Test
    public void testGetProcessOutcomeById() {
        ProcessOutcomeResource processOutcome = controller.findById(PROCESS_OUTCOME_ID);

        assertEquals("YES",processOutcome.getOutcome());
        assertEquals(AssessmentOutcomes.RECOMMEND.getType(),processOutcome.getOutcomeType());
    }

}
