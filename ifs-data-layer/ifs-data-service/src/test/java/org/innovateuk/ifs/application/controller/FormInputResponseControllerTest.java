package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.documentation.FormInputResponseResourceDocs;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.application.transactional.FormInputResponseService;
import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.builder.FileEntryResourceBuilder;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.question.resource.QuestionSetupType;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.List;

import static org.innovateuk.ifs.application.builder.FormInputResponseResourceBuilder.newFormInputResponseResource;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_NOT_FOUND;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.question.resource.QuestionSetupType.PROJECT_SUMMARY;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FormInputResponseControllerTest extends BaseControllerMockMVCTest<FormInputResponseController> {

    @Mock
    FormInputResponseService formInputResponseService;

    @Override
    protected FormInputResponseController supplyControllerUnderTest() {
        return new FormInputResponseController();
    }

    @Test
    public void testGetByFormInputIdAndApplicationId() throws Exception {
        final FileEntryResource fileEntry = FileEntryResourceBuilder.newFileEntryResource().withName("appendix.pdf").withFilesizeBytes(50L).withMediaType("application/pdf").build();
        final FormInputResponseResource formInputResponseResource = newFormInputResponseResource().
                withApplication(123L).
                withFileName("appendix.pdf").
                withUpdatedBy(1L).
                withUpdatedByUserName("Steve Smith").
                withFileEntry(fileEntry.getId()).
                withFormInputs(Collections.singletonList(456L)).
                build();
        List<FormInputResponseResource> formInputResponseResources = Collections.singletonList(formInputResponseResource);

        when(formInputResponseService.findResponsesByFormInputIdAndApplicationId(anyLong(), anyLong())).thenReturn(serviceSuccess(formInputResponseResources));

        mockMvc.
                perform(
                        get("/forminputresponse/findResponseByFormInputIdAndApplicationId/{formInputId}/{applicationId}", 456, 123).
                                header("IFS_AUTH_TOKEN", "123abc")
                ).
                andExpect(status().isOk()).
                andExpect(content().string(objectMapper.writeValueAsString(formInputResponseResources))).
                andDo(document("forminputresponse/find-responses-by-form-input-id-and-application-id",
                        pathParameters(
                                parameterWithName("formInputId").description("Form Input Id"),
                                parameterWithName("applicationId").description("Application Id")
                        ),
                        requestHeaders(
                                headerWithName("IFS_AUTH_TOKEN").description("The authentication token for the logged in user")
                        ),
                        responseFields(
                                fieldWithPath("[]").description("List of applications the user is allowed to see")
                        ).andWithPrefix(
                                "[].", FormInputResponseResourceDocs.formInputResponseResourceFields)
                        )
                ).
                andReturn();
    }

    @Test
    public void testGetByFormInputIdAndApplicationIdButFormInputNotFound() throws Exception {
        assertGetByFormInputIdAndApplicationIdButErrorOccurs(new Error(GENERAL_NOT_FOUND), "formInputIdNotFound", NOT_FOUND, GENERAL_NOT_FOUND.name());
    }


    @Test
    public void testGetByFormInputIdAndApplicationIdButApplicationNotFound() throws Exception {
        assertGetByFormInputIdAndApplicationIdButErrorOccurs(new Error(GENERAL_NOT_FOUND), "applicationNotFound", NOT_FOUND, GENERAL_NOT_FOUND.name());
    }

    @Test
    public void findByApplicationIdAndQuestionSetupType() throws Exception {
        long applicationId = 1L;
        QuestionSetupType questionSetupType = PROJECT_SUMMARY;

        FormInputResponseResource formInputResponseResource = newFormInputResponseResource().build();

        when(formInputResponseService.findResponseByApplicationIdAndQuestionSetupType(applicationId, questionSetupType))
                .thenReturn(serviceSuccess(formInputResponseResource));

        mockMvc.perform(MockMvcRequestBuilders.get(
                "/forminputresponse/findByApplicationIdAndQuestionSetupType/{applicationId}/{questionSetupType}",
                applicationId, questionSetupType))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(formInputResponseResource)));

        verify(formInputResponseService, only()).findResponseByApplicationIdAndQuestionSetupType(applicationId, questionSetupType);
    }

    private void assertGetByFormInputIdAndApplicationIdButErrorOccurs(Error errorToReturn, String documentationSuffix, HttpStatus expectedStatus, String expectedErrorKey) throws Exception {
        ServiceResult<List<FormInputResponseResource>> failureResponse = serviceFailure(errorToReturn);

        when(formInputResponseService.findResponsesByFormInputIdAndApplicationId(anyLong(), anyLong())).thenReturn(failureResponse);
        MvcResult response = mockMvc.
                perform(
                        get("/forminputresponse/findResponseByFormInputIdAndApplicationId/{formInputId}/{applicationId}", 456, 123).
                                header("IFS_AUTH_TOKEN", "123abc")).
                andDo(document("forminputresponse/find-by-form-input-id-and-application-id-" + documentationSuffix)).
                andReturn();

        assertEquals(expectedStatus.value(), response.getResponse().getStatus());
        assertResponseErrorKeyEqual(expectedErrorKey, errorToReturn, response);
    }
}
