package com.worth.ifs.project.finance.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.project.controller.ProjectFinanceController;
import org.junit.Test;

import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProjectFinanceControllerTest extends BaseControllerMockMVCTest<ProjectFinanceController> {

    @Test
    public void testGenerateSpendProfile() throws Exception {

        when(projectFinanceServiceMock.generateSpendProfile(123L)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/123/spend-profile/generate")).
                andExpect(status().isCreated());

        verify(projectFinanceServiceMock).generateSpendProfile(123L);
    }

    @Override
    protected ProjectFinanceController supplyControllerUnderTest() {
        return new ProjectFinanceController();
    }
}
