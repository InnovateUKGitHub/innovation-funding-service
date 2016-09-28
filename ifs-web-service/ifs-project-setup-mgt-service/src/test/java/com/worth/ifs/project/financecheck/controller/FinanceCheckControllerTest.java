package com.worth.ifs.project.financecheck.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.project.finance.resource.FinanceCheckResource;
import com.worth.ifs.project.resource.ProjectOrganisationCompositeId;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static com.worth.ifs.project.builder.FinanceCheckResourceBuilder.newFinanceCheckResource;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class FinanceCheckControllerTest extends BaseControllerMockMVCTest<FinanceCheckController> {
    @Test
    public void testView() throws Exception {
        Long projectId = 123L;
        Long organisationId = 456L;
        ProjectOrganisationCompositeId key = new ProjectOrganisationCompositeId(projectId, organisationId);
        when(financeCheckServiceMock.getByProjectAndOrganisation(key)).thenReturn(newFinanceCheckResource().build());
        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/organisation/" + organisationId + "/finance-check")).
                andExpect(view().name("project/finance-check")).
                andReturn();
    }

    @Test
    public void testUpdate() throws Exception {
        Long projectId = 123L;
        Long organisationId = 456L;
        FinanceCheckResource financeCheckResource = newFinanceCheckResource().build();
        MvcResult result = mockMvc.perform(post("/project/" + projectId + "/organisation/" + organisationId + "/finance-check").
                contentType(MediaType.APPLICATION_FORM_URLENCODED)).
                andExpect(status().is3xxRedirection()).
                andReturn();
    }

    @Override
    protected FinanceCheckController supplyControllerUnderTest() {
        return new FinanceCheckController();
    }
}
