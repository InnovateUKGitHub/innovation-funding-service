package com.worth.ifs.assessment.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.TestPropertySource;

import static com.worth.ifs.commons.rest.RestResult.restSuccess;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
@TestPropertySource(locations = "classpath:application.properties")
public class AssessmentAssignmentAcceptControllerTest extends BaseControllerMockMVCTest<AssessmentAssignmentAcceptController> {

    private static final String restUrl = "/assign-accept/";

    @Override
    protected AssessmentAssignmentAcceptController supplyControllerUnderTest() {
        return new AssessmentAssignmentAcceptController();
    }

    @Test
    public void acceptInvite() throws Exception {
        Long assignmentId = 1L;

        mockMvc.perform(get("/{assignmentId}/assignment/accepted", assignmentId))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/assessor/assessor-competition-dashboard"));


    }
}
