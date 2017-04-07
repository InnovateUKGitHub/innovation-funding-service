package org.innovateuk.ifs.publiccontent.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentResource;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentSectionType;
import org.innovateuk.ifs.publiccontent.controller.PublicContentController;
import org.innovateuk.ifs.publiccontent.transactional.PublicContentService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.PublicContentResourceDocs.publicContentResourceBuilder;
import static org.innovateuk.ifs.documentation.PublicContentResourceDocs.publicContentResourceFields;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PublicContentControllerDocumentation extends BaseControllerMockMVCTest<PublicContentController> {
    @Mock
    PublicContentService publicContentService;
    private RestDocumentationResultHandler document;

    @Override
    protected PublicContentController supplyControllerUnderTest() {
        return new PublicContentController();
    }

    @Before
    public void setup() {
        this.document = document("public-content/{method-name}",
                preprocessResponse(prettyPrint()));
    }

    @Test
    public void findByCompetitionId() throws Exception {
        final Long competitionId = 1L;

        when(publicContentService.findByCompetitionId(competitionId)).thenReturn(serviceSuccess(publicContentResourceBuilder.build()));

        mockMvc.perform(get("/public-content/find-by-competition-id/{competitionId}", competitionId))
                .andExpect(status().isOk())
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("competitionId").description("The competition id of the required public content")
                        ),
                        responseFields(publicContentResourceFields)
                ));
    }

    @Test
    public void publishByCompetitionId() throws Exception {
        final Long competitionId = 1L;

        when(publicContentService.publishByCompetitionId(competitionId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/public-content/publish-by-competition-id/{competitionId}", competitionId))
                .andExpect(status().isOk())
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("competitionId").description("The competition id the public content to publish")
                        )
                ));
    }

    @Test
    public void updateSection() throws Exception {
        PublicContentResource resource = publicContentResourceBuilder.build();

        when(publicContentService.updateSection(any(), any())).thenReturn(serviceSuccess());

        mockMvc.perform(post("/public-content/update-section/{section}/{id}", PublicContentSectionType.DATES.name(), resource.getId(), "json")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(resource)))
                .andExpect(status().isOk())
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("id").description("The id of the public content to update"),
                                parameterWithName("section").description("The section of the public content to update")
                        ),
                        requestFields(publicContentResourceFields)
                ));

    }

    @Test
    public void markSectionAsComplete() throws Exception {
        PublicContentResource resource = publicContentResourceBuilder.build();

        when(publicContentService.markSectionAsComplete(any(), any())).thenReturn(serviceSuccess());

        mockMvc.perform(post("/public-content/mark-section-as-complete/{section}/{id}", PublicContentSectionType.DATES.name(), resource.getId(), "json")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(resource)))
                .andExpect(status().isOk())
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("id").description("The id of the public content to update"),
                                parameterWithName("section").description("The section of the public content to update")
                        ),
                        requestFields(publicContentResourceFields)
                ));

    }

}
