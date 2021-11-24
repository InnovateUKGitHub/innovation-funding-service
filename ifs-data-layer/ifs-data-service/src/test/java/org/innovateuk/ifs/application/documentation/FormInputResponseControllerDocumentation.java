package org.innovateuk.ifs.application.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.controller.FormInputResponseController;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.application.transactional.FormInputResponseService;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static org.innovateuk.ifs.application.builder.FormInputResponseResourceBuilder.newFormInputResponseResource;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.PROJECT_SUMMARY;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;

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

        mockMvc.perform(get("/forminputresponse/find-responses-by-application/{applicationId}", applicationId)
                .header("IFS_AUTH_TOKEN", "123abc"));
    }

    @Test
    public void findByFormInputIdAndApplication() throws Exception {
        long applicationId = 1L;
        long formInputId = 2L;

        List<FormInputResponseResource> expected = newFormInputResponseResource().build(2);

        when(formInputResponseServiceMock.findResponsesByFormInputIdAndApplicationId(formInputId, applicationId)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/forminputresponse/find-response-by-form-input-id-and-application-id/{formInputId}/{applicationId}", formInputId, applicationId)
                .header("IFS_AUTH_TOKEN", "123abc"));
    }

    @Test
    public void findByApplicationIdAndQuestionSetupType() throws Exception {
        long applicationId = 1L;
        QuestionSetupType questionSetupType = PROJECT_SUMMARY;

        FormInputResponseResource expected = newFormInputResponseResource().build();

        when(formInputResponseServiceMock.findResponseByApplicationIdAndQuestionSetupType(applicationId, questionSetupType)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/forminputresponse/find-by-application-id-and-question-setup-type/{applicationId}/{questionSetupType}", applicationId, questionSetupType)
                .header("IFS_AUTH_TOKEN", "123abc"));
    }

    @Test
    public void findByApplicationIdAndQuestionId() throws Exception {
        long applicationId = 1L;
        long questionId = 2L;

        List<FormInputResponseResource> expected = newFormInputResponseResource().build(2);

        when(formInputResponseServiceMock.findResponseByApplicationIdAndQuestionId(applicationId, questionId)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/forminputresponse/find-by-application-id-and-question-id/{applicationId}/{questionId}", applicationId, questionId)
                .header("IFS_AUTH_TOKEN", "123abc"));
    }

}
