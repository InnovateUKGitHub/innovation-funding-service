package org.innovateuk.ifs.thread.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.finance.domain.ProjectFinance;
import org.innovateuk.ifs.project.finance.controller.ProjectFinanceQueriesController;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceQueriesService;
import org.innovateuk.ifs.project.finance.service.ProjectFinanceQueriesServiceImpl;
import org.innovateuk.threads.resource.PostResource;
import org.innovateuk.threads.resource.QueryResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;

import static java.lang.System.out;
import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.AlertDocs.alertResourceFields;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class ProjectFinanceQueriesControllerTest extends BaseControllerMockMVCTest<ProjectFinanceQueriesController> {

    @Override
    protected ProjectFinanceQueriesController supplyControllerUnderTest() {
        return new ProjectFinanceQueriesController(projectFinanceQueriesService);
    }

    @Test
    public void testFindOne() throws Exception {
        final Long queryId = 22L;
        QueryResource query = new QueryResource(queryId, null, null, null, null, false, null);
        when(projectFinanceQueriesService.findOne(queryId)).thenReturn(serviceSuccess(query));

        mockMvc.perform(get("/project/finance/queries/{threadId}", queryId))
            .andExpect(status().isOk())
            .andExpect(content().json(toJson(query)));

        verify(projectFinanceQueriesService).findOne(22L);
    }

    @Test
    public void testFindAll() throws Exception {
        final Long contextId = 22L;
        QueryResource query = new QueryResource(3L, null, null, null, null, false, null);
        when(projectFinanceQueriesService.findAll(contextId)).thenReturn(serviceSuccess(asList(query)));

        mockMvc.perform(get("/project/finance/queries/all/{contextClassId}", contextId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(asList(query))));

        verify(projectFinanceQueriesService).findAll(contextId);
    }

    @Test
    public void testCreate() throws Exception {
        final Long contextId = 22L;
        final QueryResource query = new QueryResource(35L, contextId, null, null, null, false, null);
        when(projectFinanceQueriesService.create(query)).thenReturn(serviceSuccess(query.id));

        mockMvc.perform(post("/project/finance/queries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(query)))
                .andExpect(content().string(objectMapper.writeValueAsString(35L)))
                .andExpect(status().isCreated());

        verify(projectFinanceQueriesService).create(query);
    }

    @Test
    public void testAddPost() throws Exception {
        Long threadId = 22L;
        PostResource post = new PostResource(33L, null, null, null, null);
        when(projectFinanceQueriesService.addPost(post, threadId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/finance/queries/{threadId}/post", threadId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(post)))
                .andExpect(status().isCreated());

        verify(projectFinanceQueriesService).addPost(post, threadId);
    }
}