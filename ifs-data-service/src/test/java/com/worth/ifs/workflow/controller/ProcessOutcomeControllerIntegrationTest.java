package com.worth.ifs.workflow.controller;

import com.worth.ifs.BaseControllerIntegrationTest;
import com.worth.ifs.assessment.resource.AssessmentOutcomes;
import com.worth.ifs.commons.rest.RestResult;
import com.worth.ifs.workflow.resource.ProcessOutcomeResource;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
        assertEquals(AssessmentOutcomes.RECOMMEND.getType(),processOutcome.getOutcomeType());
    }

}
