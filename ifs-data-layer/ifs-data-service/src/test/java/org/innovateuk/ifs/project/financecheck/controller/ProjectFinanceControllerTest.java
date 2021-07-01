package org.innovateuk.ifs.project.financecheck.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.finance.resource.ProjectFinanceResource;
import org.innovateuk.ifs.finance.transactional.ProjectFinanceService;
import org.innovateuk.ifs.project.financechecks.controller.ProjectFinanceController;
import org.junit.Test;
import org.mockito.Mock;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.finance.builder.ProjectFinanceResourceBuilder.newProjectFinanceResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProjectFinanceControllerTest extends BaseControllerMockMVCTest<ProjectFinanceController> {

    @Mock
    private ProjectFinanceService projectFinanceService;


    @Test
    public void financeDetails() throws Exception {
        ProjectFinanceResource projectFinanceResource = newProjectFinanceResource().build();

        when(projectFinanceService.financeChecksDetails(123L, 456L)).thenReturn(serviceSuccess(projectFinanceResource));

        mockMvc.perform(get("/project/{projectId}/organisation/{organisationId}/finance-details", "123", "456"))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(projectFinanceResource)));

        verify(projectFinanceService).financeChecksDetails(123L, 456L);
    }

    @Test
    public void updateFinanceDetails() throws Exception {
        ProjectFinanceResource projectFinance = newProjectFinanceResource().build();

        when(projectFinanceService.updateProjectFinance(argThat(received -> projectFinance.getId().equals(received.getId())))).thenReturn(serviceSuccess(projectFinance));

        mockMvc.perform(put("/project/project-finance")
                .contentType(APPLICATION_JSON)
                .content(toJson(projectFinance )))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(projectFinance)));
    }

    @Override
    protected ProjectFinanceController supplyControllerUnderTest() {
        return new ProjectFinanceController();
    }


}
