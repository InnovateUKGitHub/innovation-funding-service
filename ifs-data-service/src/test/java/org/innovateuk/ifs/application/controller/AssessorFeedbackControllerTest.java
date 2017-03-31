package org.innovateuk.ifs.application.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.transactional.AssessorFeedbackService;
import org.innovateuk.ifs.commons.rest.RestErrorResponse;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.junit.Before;
import org.junit.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.function.BiFunction;
import java.util.function.Function;

import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.innovateuk.ifs.commons.error.CommonErrors.internalServerErrorError;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.CollectionFunctions.asListOfPairs;
import static org.innovateuk.ifs.util.MapFunctions.asMap;
import static java.util.Collections.emptyList;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AssessorFeedbackControllerTest extends BaseControllerMockMVCTest<AssessorFeedbackController> {

    private RestDocumentationResultHandler document;

    @Before
    public void setUpDocumentation() throws Exception {
        this.document = document("assessorfeedback/assessorFeedbackDocument_{method-name}",
                preprocessResponse(prettyPrint()));
    }

    @Test
    public void testCreateAssessorFeedback() throws Exception {

        BiFunction<AssessorFeedbackService, FileEntryResource, ServiceResult<FileEntryResource>> serviceCall =
                (service, fileToUpload) -> service.createAssessorFeedbackFileEntry(eq(123L), eq(fileToUpload), fileUploadInputStreamExpectations());

        assertFileUploadProcess("/assessorfeedback/assessorFeedbackDocument", new Object[] {},
                asMap("applicationId", "123"),
                assessorFeedbackServiceMock,
                serviceCall).andDo(documentFileUploadMethod(document, asListOfPairs("applicationId", "123"), emptyList()));
    }

    @Test
    public void testUpdateAssessorFeedback() throws Exception {

        BiFunction<AssessorFeedbackService, FileEntryResource, ServiceResult<Void>> updateCall =
                (service, fileToUpdate) -> assessorFeedbackServiceMock.updateAssessorFeedbackFileEntry(eq(123L), eq(fileToUpdate), fileUploadInputStreamExpectations());

        assertFileUpdateProcess("/assessorfeedback/assessorFeedbackDocument", new Object[] {},
                asMap("applicationId", "123"),
                assessorFeedbackServiceMock, updateCall).
                andDo(documentFileUpdateMethod(document, asListOfPairs("applicationId", "123"), emptyList()));
    }

    @Test
    public void testDeleteAssessorFeedback() throws Exception {

        Function<AssessorFeedbackService, ServiceResult<Void>> deleteCall =
                service -> assessorFeedbackServiceMock.deleteAssessorFeedbackFileEntry(123L);

        assertDeleteFile("/assessorfeedback/assessorFeedbackDocument", new Object[] {},
                asMap("applicationId", "123"),
                assessorFeedbackServiceMock, deleteCall).
                andDo(documentFileDeleteMethod(document, asListOfPairs("applicationId", "123"), emptyList()));

        verify(assessorFeedbackServiceMock).deleteAssessorFeedbackFileEntry(123L);
    }

    @Test
    public void testGetAssessorFeedbackFileContents() throws Exception {

        Function<AssessorFeedbackService, ServiceResult<FileAndContents>> getFileAction =
                (service) -> service.getAssessorFeedbackFileEntryContents(123L);

        assertGetFileContents("/assessorfeedback/assessorFeedbackDocument", new Object[] {},
                asMap("applicationId", "123"),
                assessorFeedbackServiceMock, getFileAction).
                andDo(documentFileGetContentsMethod(document, asListOfPairs("applicationId", "123"), emptyList()));
    }

    @Test
    public void testGetAssessorFeedbackFileEntry() throws Exception {

        Function<AssessorFeedbackService, ServiceResult<FileEntryResource>> getFileAction =
                (service    ) -> service.getAssessorFeedbackFileEntryDetails(123L);

        assertGetFileDetails("/assessorfeedback/assessorFeedbackDocument/fileentry", new Object[] {},
                asMap("applicationId", "123"),
                assessorFeedbackServiceMock, getFileAction).
                andDo(documentFileGetDetailsMethod(document, asListOfPairs("applicationId", "123"), emptyList()));
    }

    private RestDocumentationResultHandler documentAssessorFeedbackUploaded() {
    	return document("assessor-feedback/assessorFeedbackUploaded",
    			requestParameters(
                        parameterWithName("competitionId").description("Id of the competition that we are checking if feedback is uploaded for all submitted applications")
                ),
                requestHeaders(
                        headerWithName("IFS_AUTH_TOKEN").description("The authentication token for the logged in user")
                ));
    }

    private RestDocumentationResultHandler documentSubmitAssessorFeedback() {
    	return document("assessor-feedback/submitAssessorFeedback",
                requestHeaders(
                        headerWithName("IFS_AUTH_TOKEN").description("The authentication token for the logged in user")
                ));
    }

    @Override
    protected AssessorFeedbackController supplyControllerUnderTest() {
        return new AssessorFeedbackController();
    }
}
