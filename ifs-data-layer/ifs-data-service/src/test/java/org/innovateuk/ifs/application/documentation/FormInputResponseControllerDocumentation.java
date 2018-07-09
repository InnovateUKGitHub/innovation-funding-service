package org.innovateuk.ifs.application.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.controller.FormInputResponseController;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.application.transactional.FormInputResponseService;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.restdocs.payload.PayloadDocumentation;

import java.util.List;

import static org.innovateuk.ifs.application.builder.FormInputResponseResourceBuilder.newFormInputResponseResource;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.PROJECT_SUMMARY;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

public class FormInputResponseControllerDocumentation extends BaseControllerMockMVCTest<FormInputResponseController> {

    @Mock
    private FormInputResponseService formInputResponseServiceMock;

    @Override
    protected FormInputResponseController supplyControllerUnderTest() {
        return new FormInputResponseController();
    }

    @Test
    public void findResponsesByApplication() throws Exception {

        long applicationId = 123L;

        List<FormInputResponseResource> expected = newFormInputResponseResource().build(2);

        when(formInputResponseServiceMock.findResponsesByApplication(applicationId)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/forminputresponse/findResponsesByApplication/{applicationId}", applicationId))
                .andDo(document("forminputresponse/{method-name}",
                        pathParameters(
                                parameterWithName("applicationId").description("Id of the application")
                        ),
                        responseFields(
                                fieldWithPath("[]").description("List of formInputResponses")
                        ).andWithPrefix("[].", FormInputResponseResourceDocs.formInputResponseResourceFields)
                ));
    }

    @Test
    public void findByFormInputIdAndApplication() throws Exception {
        long applicationId = 1L;
        long formInputId = 2L;

        List<FormInputResponseResource> expected = newFormInputResponseResource().build(2);

        when(formInputResponseServiceMock.findResponsesByFormInputIdAndApplicationId(formInputId, applicationId)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/forminputresponse/findResponseByFormInputIdAndApplicationId/{formInputId}/{applicationId}", formInputId, applicationId))
                .andDo(document("forminputresponse/{method-name}",
                        pathParameters(
                                parameterWithName("formInputId").description("Id of the form input"),
                                parameterWithName("applicationId").description("The id of the application")
                        ),
                        responseFields(
                                fieldWithPath("[]").description("List of formInputResponses")
                        ).andWithPrefix("[].", FormInputResponseResourceDocs.formInputResponseResourceFields)
                ));
    }

    @Test
    public void findByApplicationIdAndQuestionSetupType() throws Exception {
        long applicationId = 1L;
        QuestionSetupType questionSetupType = PROJECT_SUMMARY;

        FormInputResponseResource expected = newFormInputResponseResource().build();

        when(formInputResponseServiceMock.findResponseByApplicationIdAndQuestionSetupType(applicationId, questionSetupType)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/forminputresponse/findByApplicationIdAndQuestionSetupType/{applicationId}/{questionSetupType}", applicationId, questionSetupType))
                .andDo(document("forminputresponse/{method-name}",
                        pathParameters(
                                parameterWithName("applicationId").description("The id of the application"),
                                parameterWithName("questionSetupType").description("The setup type of the question")
                        ),
                        PayloadDocumentation.responseFields(FormInputResponseResourceDocs.formInputResponseResourceFields)
                ));
    }

    @Test
    public void findByApplicationIdAndQuestionId() throws Exception {
        long applicationId = 1L;
        long questionId = 2L;

        List<FormInputResponseResource> expected = newFormInputResponseResource().build(2);

        when(formInputResponseServiceMock.findResponseByApplicationIdAndQuestionId(applicationId, questionId)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/forminputresponse/findByApplicationIdAndQuestionId/{applicationId}/{questionId}", applicationId, questionId))
                .andDo(document("forminputresponse/{method-name}",
                        pathParameters(
                                parameterWithName("applicationId").description("The id of the application"),
                                parameterWithName("questionId").description("The id of the question")
                        ),
                        responseFields(
                                fieldWithPath("[]").description("List of formInputResponses")
                        ).andWithPrefix("[].", FormInputResponseResourceDocs.formInputResponseResourceFields)
                ));
    }

}
