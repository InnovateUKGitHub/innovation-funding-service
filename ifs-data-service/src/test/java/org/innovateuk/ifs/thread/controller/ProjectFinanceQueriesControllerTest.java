package org.innovateuk.ifs.thread.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.project.finance.controller.ProjectFinanceQueriesController;
import org.innovateuk.threads.resource.QueryResource;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

import java.util.ArrayList;
import java.util.Arrays;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.AlertDocs.alertResourceFields;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProjectFinanceQueriesControllerTest extends BaseControllerMockMVCTest<ProjectFinanceQueriesController> {

    @Autowired ProjectFinanceQueriesController projectFinanceQueriesController;

    @Override
    protected ProjectFinanceQueriesController supplyControllerUnderTest() {
        return projectFinanceQueriesController;
    }

    @Test
    public void testCreateQuery() throws Exception {
        QueryResource query = new QueryResource(2L, null, null, null, null, false, null);
        when(projectFinanceQueriesService.create(query)).thenReturn(serviceSuccess(2L));
        when(projectFinanceQueriesService.findAll(22L)).thenReturn(serviceSuccess(asList(query)));

        mockMvc.perform(get("/project/finance/queries/22"))
            .andExpect(status().isOk())
            .andExpect(content().json(toJson(asList(query))));

//        verify(projectFinanceQueriesService).create(query);
    }
}