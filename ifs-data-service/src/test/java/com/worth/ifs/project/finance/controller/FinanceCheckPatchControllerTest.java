package com.worth.ifs.project.finance.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.project.controller.FinanceCheckPatchController;
import org.junit.Test;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FinanceCheckPatchControllerTest extends BaseControllerMockMVCTest<FinanceCheckPatchController> {

    @Test
    public void testGenerateFinanceChecksForAllProjects() throws Exception {
        when(projectServiceMock.generateFinanceChecksForAllProjects()).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/patch/generateFinanceChecksForAllProjects")).andExpect(status().isOk());

        verify(projectServiceMock).generateFinanceChecksForAllProjects();
    }

    @Override
    protected FinanceCheckPatchController supplyControllerUnderTest() {
        return new FinanceCheckPatchController();
    }
}
