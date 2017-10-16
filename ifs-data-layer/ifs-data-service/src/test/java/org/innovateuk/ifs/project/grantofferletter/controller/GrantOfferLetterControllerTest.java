package org.innovateuk.ifs.project.grantofferletter.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterState;
import org.innovateuk.ifs.project.grantofferletter.transactional.GrantOfferLetterService;
import org.junit.Test;

import java.util.function.BiFunction;
import java.util.function.Function;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static java.util.Collections.emptyMap;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class GrantOfferLetterControllerTest extends BaseControllerMockMVCTest<GrantOfferLetterController> {

    @Test
    public void getGrantOfferLetterFileContents() throws Exception {
        Long projectId = 123L;

        Function<GrantOfferLetterService, ServiceResult<FileAndContents>> serviceCallToUpload =
                (service) -> service.getGrantOfferLetterFileAndContents(projectId);

        assertGetFileContents("/project/{projectId}/grant-offer", new Object[] {projectId},
                emptyMap(), grantOfferLetterServiceMock, serviceCallToUpload).
                andDo(documentFileGetContentsMethod("project/{method-name}"));
    }

    @Test
    public void getAdditionalContractFileContents() throws Exception {
        Long projectId = 123L;

        Function<GrantOfferLetterService, ServiceResult<FileAndContents>> serviceCallToUpload =
                (service) -> service.getAdditionalContractFileAndContents(projectId);

        assertGetFileContents("/project/{projectId}/additional-contract", new Object[] {projectId},
                emptyMap(), grantOfferLetterServiceMock, serviceCallToUpload).
                andDo(documentFileGetContentsMethod("project/{method-name}"));
    }

    @Test
    public void getGrantOfferLetterFileEntryDetails() throws Exception {
        Long projectId = 123L;

        Function<GrantOfferLetterService, ServiceResult<FileEntryResource>> serviceCallToUpload =
                (service) -> service.getSignedGrantOfferLetterFileEntryDetails(projectId);

        assertGetFileDetails("/project/{projectId}/signed-grant-offer/details", new Object[] {projectId},
                emptyMap(),
                grantOfferLetterServiceMock, serviceCallToUpload).
                andDo(documentFileGetDetailsMethod("project/{method-name}"));
    }

    @Test
    public void getAdditionalContractFileEntryDetails() throws Exception {
        Long projectId = 123L;

        Function<GrantOfferLetterService, ServiceResult<FileEntryResource>> serviceCallToUpload =
                (service) -> service.getAdditionalContractFileEntryDetails(projectId);

        assertGetFileDetails("/project/{projectId}/additional-contract/details", new Object[] {projectId},
                emptyMap(),
                grantOfferLetterServiceMock, serviceCallToUpload).
                andDo(documentFileGetDetailsMethod("project/{method-name}"));
    }

    @Test
    public void addGrantOfferLetter() throws Exception {
        Long projectId = 123L;

        BiFunction<GrantOfferLetterService, FileEntryResource, ServiceResult<FileEntryResource>> serviceCallToUpload =
                (service, fileToUpload) -> service.createSignedGrantOfferLetterFileEntry(eq(projectId), eq(fileToUpload), fileUploadInputStreamExpectations());

        assertFileUploadProcess("/project/" + projectId + "/signed-grant-offer/", grantOfferLetterServiceMock, serviceCallToUpload).
                andDo(documentFileUploadMethod("project/{method-name}"));
    }

    @Test
    public void addAdditionalContractFile() throws Exception {
        Long projectId = 123L;

        BiFunction<GrantOfferLetterService, FileEntryResource, ServiceResult<FileEntryResource>> serviceCallToUpload =
                (service, fileToUpload) -> service.createAdditionalContractFileEntry(eq(projectId), eq(fileToUpload), fileUploadInputStreamExpectations());

        assertFileUploadProcess("/project/" + projectId + "/additional-contract/", grantOfferLetterServiceMock, serviceCallToUpload).
                andDo(documentFileUploadMethod("project/{method-name}"));

    }

    @Test
    public void removeGrantOfferLetterFile() throws Exception {

        Long projectId = 123L;

        when(grantOfferLetterServiceMock.removeGrantOfferLetterFileEntry(projectId)).thenReturn(serviceSuccess());

        mockMvc.perform(delete("/project/{projectId}/grant-offer", projectId)).
                andExpect(status().isNoContent()).
                andDo(document("project/{method-name}"));
    }

    @Test
    public void removeSignedGrantOfferLetterFile() throws Exception {

        Long projectId = 123L;

        when(grantOfferLetterServiceMock.removeSignedGrantOfferLetterFileEntry(projectId)).thenReturn(serviceSuccess());

        mockMvc.perform(delete("/project/{projectId}/signed-grant-offer-letter", projectId)).
                andExpect(status().isNoContent()).
                andDo(document("project/{method-name}"));
    }

    @Test
    public void getGrantOfferLetterWorkflowState() throws Exception {

        Long projectId = 123L;

        when(grantOfferLetterServiceMock.getGrantOfferLetterWorkflowState(projectId)).thenReturn(serviceSuccess(GrantOfferLetterState.APPROVED));

        mockMvc.perform(get("/project/{projectId}/grant-offer-letter/state", 123L))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(GrantOfferLetterState.APPROVED)))
                .andDo(document("project/{method-name}"))
                .andReturn();

        verify(grantOfferLetterServiceMock).getGrantOfferLetterWorkflowState(projectId);
    }

    @Override
    protected GrantOfferLetterController supplyControllerUnderTest() {
        return new GrantOfferLetterController();
    }
}
