package com.worth.ifs.invite.controller;

import com.worth.ifs.BaseControllerIntegrationTest;
import com.worth.ifs.invite.resource.RejectionReasonResource;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.worth.ifs.invite.builder.RejectionReasonResourceBuilder.newRejectionReasonResource;
import static java.lang.Boolean.TRUE;
import static org.junit.Assert.assertEquals;

public class RejectionReasonControllerIntegrationTest extends BaseControllerIntegrationTest<RejectionReasonController> {

    @Autowired
    @Override
    protected void setControllerUnderTest(RejectionReasonController controller) {
        this.controller = controller;
    }

    @Test
    public void findAllActive() throws Exception {
        List<RejectionReasonResource> expected = newRejectionReasonResource()
                .withId(1L, 2L, 3L)
                .withActive(TRUE, TRUE, TRUE)
                .withReason("Not available", "Conflict of interest", "Not my area of expertise")
                .withPriority(1, 2, 3)
                .build(3);

        List<RejectionReasonResource> found = controller.findAllActive().getSuccessObjectOrThrowException();
        assertEquals(expected, found);
    }
}