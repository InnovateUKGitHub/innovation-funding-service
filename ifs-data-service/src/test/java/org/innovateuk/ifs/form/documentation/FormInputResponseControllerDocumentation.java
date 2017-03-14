package org.innovateuk.ifs.form.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.form.controller.FormInputResponseController;
import org.innovateuk.ifs.form.resource.FormInputResponseResource;
import org.junit.Test;

import java.util.List;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.form.builder.FormInputResponseResourceBuilder.newFormInputResponseResource;
import static org.innovateuk.ifs.form.documentation.FormInputResponseResourceDocs.formInputResponseResourceFields;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;

public class FormInputResponseControllerDocumentation extends BaseControllerMockMVCTest<FormInputResponseController> {

    @Override
    protected FormInputResponseController supplyControllerUnderTest() {
        return new FormInputResponseController();
    }

    @Test
    public void findResponsesByApplication() throws Exception {

        long applicationId = 123L;

        List<FormInputResponseResource> expected = newFormInputResponseResource().build(2);

        when(formInputServiceMock.findResponsesByApplication(applicationId)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/forminputresponse/findResponsesByApplication/{applicationId}", applicationId))
                .andDo(document("forminputresponse/{method-name}",
                        pathParameters(
                                parameterWithName("applicationId").description("Id of the application")
                        ),
                        responseFields(
                                fieldWithPath("[]").description("List of formInputResponses")
                        )
                ));
    }

    @Test
    public void findByFormInputIdAndApplication() throws Exception {
        long applicationId = 1L;
        long formInputId = 2L;

        List<FormInputResponseResource> expected = newFormInputResponseResource().build(2);

        when(formInputServiceMock.findResponsesByFormInputIdAndApplicationId(formInputId, applicationId)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/forminputresponse/findResponseByFormInputIdAndApplicationId/{formInputId}/{applicationId}", formInputId, applicationId))
                .andDo(document("forminputresponse/{method-name}",
                        pathParameters(
                                parameterWithName("formInputId").description("Id of the form input"),
                                parameterWithName("applicationId").description("The id of the application")
                        ),
                        responseFields(
                                fieldWithPath("[]").description("List of formInputResponses")
                        )
                ));
    }

    @Test
    public void findByApplicationIdAndQuestionName() throws Exception {
        long applicationId = 1L;
        String questionName = "question";

        FormInputResponseResource expected = newFormInputResponseResource().build();

        when(formInputServiceMock.findResponseByApplicationIdAndQuestionName(applicationId, questionName)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/forminputresponse/findByApplicationIdAndQuestionName/{applicationId}/{questionName}", applicationId, questionName))
                .andDo(document("forminputresponse/{method-name}",
                        pathParameters(
                                parameterWithName("applicationId").description("The id of the application"),
                                parameterWithName("questionName").description("The name of the question")
                        ),
                        responseFields(formInputResponseResourceFields)
                ));
    }

    @Test
    public void findByApplicationIdAndQuestionId() throws Exception {
        long applicationId = 1L;
        long questionId = 2L;

        List<FormInputResponseResource> expected = newFormInputResponseResource().build(2);

        when(formInputServiceMock.findResponseByApplicationIdAndQuestionId(applicationId, questionId)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/forminputresponse/findByApplicationIdAndQuestionId/{applicationId}/{questionId}", applicationId, questionId))
                .andDo(document("forminputresponse/{method-name}",
                        pathParameters(
                                parameterWithName("applicationId").description("The id of the application"),
                                parameterWithName("questionId").description("The id of the question")
                        ),
                        responseFields(
                                fieldWithPath("[]").description("List of formInputResponses")
                        )
                ));
    }

}
