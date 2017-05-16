package org.innovateuk.ifs.project.otherdocuments.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.project.otherdocuments.controller.OtherDocumentsController;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.HttpServletRequest;
import java.time.ZonedDateTime;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class OtherDocumentsControllerDocumentation extends BaseControllerMockMVCTest<OtherDocumentsController> {

    private RestDocumentationResultHandler document;

    @Override
    protected OtherDocumentsController supplyControllerUnderTest() {
        return new OtherDocumentsController();
    }

    @Before
    public void setup(){
        this.document = document("project/{method-name}",
                preprocessResponse(prettyPrint()));
    }

    @Test
    public void isOtherDocumentsSubmitAllowed() throws Exception {
        UserResource userResource = newUserResource()
                .withId(1L)
                .withUID("123abc")
                .build();
        when(otherDocumentsServiceMock.isOtherDocumentsSubmitAllowed(123L, 1L)).thenReturn(serviceSuccess(true));
        when(userAuthenticationService.getAuthenticatedUser(any(HttpServletRequest.class))).thenReturn(userResource);

        MvcResult mvcResult = mockMvc.perform(get("/project/{projectId}/partner/documents/ready", 123L))
                .andExpect(status().isOk())
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project for which the documents are being submitted to.")
                        )))
                .andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().equals("true"));
    }

    @Test
    public void isOtherDocumentsSubmitNotAllowedWhenDocumentsNotFullyUploaded() throws Exception {
        UserResource userResource = newUserResource()
                .withId(1L)
                .withUID("123abc")
                .build();
        when(otherDocumentsServiceMock.isOtherDocumentsSubmitAllowed(123L, 1L)).thenReturn(serviceSuccess(false));
        when(userAuthenticationService.getAuthenticatedUser(any(HttpServletRequest.class))).thenReturn(userResource);

        MvcResult mvcResult = mockMvc.perform(get("/project/{projectId}/partner/documents/ready", 123L))
                .andExpect(status().isOk())
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project for which the documents are being submitted to.")
                        )))
                .andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().equals("false"));
    }

    @Test
    public void setPartnerDocumentsSubmittedDate() throws Exception {
        when(otherDocumentsServiceMock.saveDocumentsSubmitDateTime(isA(Long.class), isA(ZonedDateTime.class))).thenReturn(serviceSuccess());
        mockMvc.perform(post("/project/{projectId}/partner/documents/submit", 123L))
                .andExpect(status().isOk())
                .andDo(this.document.snippets(
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project for which the documents are being submitted to.")
                        )));
    }
}
