package org.innovateuk.ifs.thread.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.project.finance.controller.ProjectFinanceQueriesController;
import org.innovateuk.threads.resource.PostResource;
import org.innovateuk.threads.resource.QueryResource;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.QueryFieldsDocs.queryResourceFields;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProjectFinanceQueriesControllerDocumentation extends BaseControllerMockMVCTest<ProjectFinanceQueriesController> {

    @Test
    public void testCreate() throws Exception {
        final QueryResource query = new QueryResource(35L, null, null, null, null, false, null);
        when(projectFinanceQueriesService.create(query)).thenReturn(serviceSuccess(query.id));

        mockMvc.perform(MockMvcRequestBuilders.post("/project/finance/queries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(query)))
                .andExpect(content().string(objectMapper.writeValueAsString(35L)))
                .andExpect(status().isCreated())
                .andDo(document("queries/{method-name}", requestFields(queryResourceFields)));
    }

    @Test
    public void testFindOne() throws Exception {
        final Long queryId = 22L;
        QueryResource query = new QueryResource(queryId, null, null, null, null, false, null);
        when(projectFinanceQueriesService.findOne(queryId)).thenReturn(serviceSuccess(query));

        mockMvc.perform(get("/project/finance/queries/{queryId}", queryId))
                .andExpect(status().isOk())
                .andDo(document("queries/{method-name}",
                        pathParameters(parameterWithName("queryId").description("Id of the Query to be fetched")),
                        responseFields(queryResourceFields)));
    }

    @Test
    public void testFindAll() throws Exception {
        final Long contextId = 22L;
        QueryResource query = new QueryResource(3L, null, null, null, null, false, null);
        when(projectFinanceQueriesService.findAll(contextId)).thenReturn(serviceSuccess(asList(query)));

        mockMvc.perform(get("/project/finance/queries/all/{projectFinanceId}", contextId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(asList(query))))
                .andDo(document("queries/{method-name}",
                        responseFields(fieldWithPath("[]").description("List of Queries the authenticated user has access to")),
                        pathParameters(parameterWithName("projectFinanceId").description("The id of the project finance under which the expected queries live."))));
    }


    @Test
    public void testAddPost() throws Exception {
        Long queryId = 22L;
        PostResource post = new PostResource(33L, null, null, null, null);
        when(projectFinanceQueriesService.addPost(post, queryId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/finance/queries/{queryId}/post", queryId)
                .contentType(APPLICATION_JSON)
                .content(toJson(post)))
                .andExpect(status().isCreated())
                .andDo(document("queries/{method-name}",
                        pathParameters(parameterWithName("queryId").description("Id of the Query to which the Post is to be added to."))));
    }

    @Override
    public void setupMockMvc() {
        controller = new ProjectFinanceQueriesController(projectFinanceQueriesService);
        super.setupMockMvc();
    }

    @Override
    protected ProjectFinanceQueriesController supplyControllerUnderTest() {
        return null;
    }


}
