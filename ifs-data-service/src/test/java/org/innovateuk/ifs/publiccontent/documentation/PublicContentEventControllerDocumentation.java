package org.innovateuk.ifs.publiccontent.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentEventResource;
import org.innovateuk.ifs.publiccontent.controller.PublicContentEventController;
import org.innovateuk.ifs.publiccontent.transactional.PublicContentEventService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.PublicContentEventResourceDocs.publicContentEventResourceBuilder;
import static org.innovateuk.ifs.documentation.PublicContentEventResourceDocs.publicContentEventResourceFields;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PublicContentEventControllerDocumentation extends BaseControllerMockMVCTest<PublicContentEventController> {
    @Mock
    private PublicContentEventService publicContentEventService;
    private RestDocumentationResultHandler document;

    @Override
    protected PublicContentEventController supplyControllerUnderTest() {
        return new PublicContentEventController();
    }

    @Before
    public void setup() {
        this.document = document("public-content/events/{method-name}",
                preprocessResponse(prettyPrint()));
    }

    @Test
    public void saveEvent() throws Exception {
        PublicContentEventResource resource = publicContentEventResourceBuilder.build();

        when(publicContentEventService.saveEvent(resource)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/public-content/events/save-event")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(resource)))
                .andExpect(status().isOk())
                .andDo(this.document.snippets(
                    requestFields(publicContentEventResourceFields)
                ));
    }

    @Test
    public void resetAndSaveEvent() throws Exception {
        List<PublicContentEventResource> resources = publicContentEventResourceBuilder.build(2);

        when(publicContentEventService.resetAndSaveEvents(1L, resources)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/public-content/events/reset-and-save-events?id=1")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(resources)))
                .andExpect(status().isOk())
                .andDo(this.document.snippets(
                        requestParameters(
                                parameterWithName("id").description("The id of the public content that's being reset")
                        ),
                        requestFields(fieldWithPath("[]").description("List of public content events"))
                                .andWithPrefix("[].", publicContentEventResourceFields)
                ));
    }
}
