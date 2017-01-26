package org.innovateuk.ifs.finance.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.finance.resource.cost.GrantClaim;
import org.junit.Test;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProjectFinanceRowControllerTest extends BaseControllerMockMVCTest<ProjectFinanceRowController> {

    @Override
    protected ProjectFinanceRowController supplyControllerUnderTest() {
        return new ProjectFinanceRowController();
    }

    @Test
    public void addProjectCostWithoutPersisting() throws Exception{

        when(projectFinanceRowServiceMock.addCostWithoutPersisting(123L, 456L)).thenReturn(serviceSuccess(new GrantClaim()));

        mockMvc.perform(get("/cost/project/add-without-persisting/{projectFinanceId}/{questionId}", "123", "456"))
                .andExpect(status().isCreated());

        verify(projectFinanceRowServiceMock, times(1)).addCostWithoutPersisting(123L, 456L);
    }
}
