package org.innovateuk.ifs.thread.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.project.queries.controller.ProjectFinanceQueriesController;
import org.innovateuk.threads.resource.PostResource;
import org.innovateuk.threads.resource.QueryResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class ProjectFinanceQueriesControllerTest extends BaseControllerMockMVCTest<ProjectFinanceQueriesController> {

    @Override
    protected ProjectFinanceQueriesController supplyControllerUnderTest() {
        return new ProjectFinanceQueriesController(financeCheckQueriesService);
    }

    @Override
    public void setupMockMvc() {
        controller = new ProjectFinanceQueriesController(financeCheckQueriesService);
        super.setupMockMvc();
    }

    @Test
    public void testFindOne() throws Exception {
        final Long queryId = 22L;
        QueryResource query = new QueryResource(queryId, null, null, null, null, false, null);
        when(financeCheckQueriesService.findOne(queryId)).thenReturn(serviceSuccess(query));

        mockMvc.perform(get("/project/finance/queries/{threadId}", queryId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(query)));

        verify(financeCheckQueriesService).findOne(22L);
    }

    @Test
    public void testFindAll() throws Exception {
        final Long contextId = 22L;
        QueryResource query = new QueryResource(3L, null, null, null, null, false, null);
        when(financeCheckQueriesService.findAll(contextId)).thenReturn(serviceSuccess(asList(query)));

        mockMvc.perform(get("/project/finance/queries/all/{contextClassId}", contextId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(asList(query))));

        verify(financeCheckQueriesService).findAll(contextId);
    }

    @Test
    public void testCreate() throws Exception {
        final Long contextId = 22L;
        final QueryResource query = new QueryResource(35L, contextId, null, null, null, false, null);
        when(financeCheckQueriesService.create(query)).thenReturn(serviceSuccess(query.id));

        mockMvc.perform(post("/project/finance/queries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(query)))
                .andExpect(content().string(objectMapper.writeValueAsString(35L)))
                .andExpect(status().isCreated());

        verify(financeCheckQueriesService).create(query);
    }

    @Test
    public void testAddPost() throws Exception {
        Long threadId = 22L;
        PostResource post = new PostResource(33L, null, null, null, null);
        when(financeCheckQueriesService.addPost(post, threadId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/finance/queries/{threadId}/post", threadId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(post)))
                .andExpect(status().isCreated());

        verify(financeCheckQueriesService).addPost(post, threadId);
    }
}