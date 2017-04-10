package org.innovateuk.ifs.thread.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.project.financecheck.controller.ProjectFinanceQueriesController;
import org.innovateuk.threads.resource.FinanceChecksSectionType;
import org.innovateuk.threads.resource.PostResource;
import org.innovateuk.threads.resource.QueryResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static java.time.ZonedDateTime.now;
import static java.util.Arrays.asList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.QueryFieldsDocs.queryResourceFields;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProjectFinanceQueriesControllerDocumentation extends BaseControllerMockMVCTest<ProjectFinanceQueriesController> {

    private RestDocumentationResultHandler document;

    @Before
    public void setup() {
        this.document = document("project/finance/queries/{method-name}",
                preprocessResponse(prettyPrint()));
    }


    @Test
    public void findOne() throws Exception {
        final Long queryId = 3L;
        List<PostResource> posts = asList(new PostResource(97L, newUserResource().withId(7L).build(), "Post message", asList(), now()));
        final QueryResource query = new QueryResource(3L, 22L, posts, FinanceChecksSectionType.VIABILITY, "New query title", true, now());
        when(financeCheckQueriesService.findOne(queryId)).thenReturn(serviceSuccess(query));

        mockMvc.perform(get("/project/finance/queries/{queryId}", queryId))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(query)))
                .andDo(this.document.snippets(
                        pathParameters(parameterWithName("queryId").description("Id of the Query to be fetched")),
                        responseFields(queryResourceFields())));
    }

    @Test
    public void findAll() throws Exception {
        final Long contextId = 22L;
        List<PostResource> posts = asList(new PostResource(33L, newUserResource().withId(7L).build(), "Post message", asList(), now()));
        final QueryResource query = new QueryResource(3L, 22L, posts, FinanceChecksSectionType.VIABILITY, "New query title", true, now());
        when(financeCheckQueriesService.findAll(contextId)).thenReturn(serviceSuccess(asList(query)));

        mockMvc.perform(get("/project/finance/queries/all/{projectFinanceId}", contextId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(asList(query))))
                .andDo(this.document.snippets(
                        responseFields(fieldWithPath("[]").description("List of Queries the authenticated user has access to")),
                        pathParameters(parameterWithName("projectFinanceId").description("The id of the project finance under which the expected queries live."))));
    }


    @Test
    public void addPost() throws Exception {
        Long queryId = 22L;
        PostResource post = new PostResource(null, newUserResource().withId(7L).build(), "Post message", asList(), null);
        when(financeCheckQueriesService.addPost(post, queryId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/finance/queries/{queryId}/post", queryId)
                .contentType(APPLICATION_JSON)
                .content(toJson(post)))
                .andExpect(status().isCreated())
                .andDo(this.document.snippets(
                        pathParameters(parameterWithName("queryId").description("Id of the Query to which the Post is to be added to."))));
    }

    @Test
    public void create() throws Exception {
        List<PostResource> posts = asList(new PostResource(null, newUserResource().withId(7L).build(), "Post message", asList(), null));
        final QueryResource query = new QueryResource(null, 22L, posts, FinanceChecksSectionType.VIABILITY, "New query title", false, null);

        when(financeCheckQueriesService.create(query)).thenReturn(serviceSuccess(55L));

        mockMvc.perform(MockMvcRequestBuilders.post("/project/finance/queries")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(query)))
                .andExpect(content().string(objectMapper.writeValueAsString(55L)))
                .andExpect(status().isCreated())
                .andDo(this.document.snippets(requestFields(queryResourceFields())));
    }

    @Override
    public void setupMockMvc() {
        controller = new ProjectFinanceQueriesController(financeCheckQueriesService);
        super.setupMockMvc();
    }

    @Override
    protected ProjectFinanceQueriesController supplyControllerUnderTest() {
        return null;
    }
}
