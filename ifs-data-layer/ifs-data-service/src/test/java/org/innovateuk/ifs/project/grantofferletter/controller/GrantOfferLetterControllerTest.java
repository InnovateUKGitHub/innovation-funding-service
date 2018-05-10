package org.innovateuk.ifs.project.grantofferletter.controller;

import org.innovateuk.ifs.BaseFileControllerMockMVCTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.file.service.FilesizeAndTypeFileValidator;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterApprovalResource;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterState;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterStateResource;
import org.innovateuk.ifs.project.grantofferletter.transactional.GrantOfferLetterService;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterEvent.GOL_SIGNED;
import static org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterStateResource.stateInformationForNonPartnersView;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class GrantOfferLetterControllerTest extends BaseFileControllerMockMVCTest<GrantOfferLetterController> {

    private static final long projectId = 123L;
    private static final long maxFilesize = 1234L;
    private static final List<String> mediaTypes = singletonList("application/pdf");

    @Mock(name = "fileValidator")
    private FilesizeAndTypeFileValidator<List<String>> fileValidatorMock;

    @Mock
    private GrantOfferLetterService grantOfferLetterServiceMock;

    @Override
    protected GrantOfferLetterController supplyControllerUnderTest() {
        GrantOfferLetterController controller = new GrantOfferLetterController();
        ReflectionTestUtils.setField(controller, "maxFilesizeBytesForProjectSetupGrantOfferLetter", maxFilesize);
        ReflectionTestUtils.setField(controller, "validMediaTypesForProjectSetupGrantOfferLetter", mediaTypes);
        return controller;
    }

    @Test
    public void getGrantOfferLetterFileContents() throws Exception {

        Function<GrantOfferLetterService, ServiceResult<FileAndContents>> serviceCallToUpload =
                (service) -> service.getGrantOfferLetterFileAndContents(projectId);

        assertGetFileContents("/project/{projectId}/grant-offer", new Object[] {projectId},
                emptyMap(), grantOfferLetterServiceMock, serviceCallToUpload).
                andDo(documentFileGetContentsMethod("project/{method-name}"));
    }

    @Test
    public void getAdditionalContractFileContents() throws Exception {

        Function<GrantOfferLetterService, ServiceResult<FileAndContents>> serviceCallToUpload =
                (service) -> service.getAdditionalContractFileAndContents(projectId);

        assertGetFileContents("/project/{projectId}/additional-contract", new Object[] {projectId},
                emptyMap(), grantOfferLetterServiceMock, serviceCallToUpload).
                andDo(documentFileGetContentsMethod("project/{method-name}"));
    }

    @Test
    public void getGrantOfferLetterFileEntryDetails() throws Exception {

        Function<GrantOfferLetterService, ServiceResult<FileEntryResource>> serviceCallToUpload =
                (service) -> service.getSignedGrantOfferLetterFileEntryDetails(projectId);

        assertGetFileDetails("/project/{projectId}/signed-grant-offer/details", new Object[] {projectId},
                emptyMap(),
                grantOfferLetterServiceMock, serviceCallToUpload).
                andDo(documentFileGetDetailsMethod("project/{method-name}"));
    }

    @Test
    public void getAdditionalContractFileEntryDetails() throws Exception {

        Function<GrantOfferLetterService, ServiceResult<FileEntryResource>> serviceCallToUpload =
                (service) -> service.getAdditionalContractFileEntryDetails(projectId);

        assertGetFileDetails("/project/{projectId}/additional-contract/details", new Object[] {projectId},
                emptyMap(),
                grantOfferLetterServiceMock, serviceCallToUpload).
                andDo(documentFileGetDetailsMethod("project/{method-name}"));
    }

    @Test
    public void addGrantOfferLetter() throws Exception {

        BiFunction<GrantOfferLetterService, FileEntryResource, ServiceResult<FileEntryResource>> serviceCallToUpload =
                (service, fileToUpload) -> service.createSignedGrantOfferLetterFileEntry(eq(projectId), eq(fileToUpload), fileUploadInputStreamExpectations());

        assertFileUploadProcess("/project/" + projectId + "/signed-grant-offer/", fileValidatorMock, mediaTypes, grantOfferLetterServiceMock, serviceCallToUpload).
                andDo(documentFileUploadMethod("project/{method-name}"));
    }

    @Test
    public void addAdditionalContractFile() throws Exception {

        BiFunction<GrantOfferLetterService, FileEntryResource, ServiceResult<FileEntryResource>> serviceCallToUpload =
                (service, fileToUpload) -> service.createAdditionalContractFileEntry(eq(projectId), eq(fileToUpload), fileUploadInputStreamExpectations());

        assertFileUploadProcess("/project/" + projectId + "/additional-contract/", fileValidatorMock, mediaTypes, grantOfferLetterServiceMock, serviceCallToUpload).
                andDo(documentFileUploadMethod("project/{method-name}"));

    }

    @Test
    public void removeGrantOfferLetterFile() throws Exception {

        when(grantOfferLetterServiceMock.removeGrantOfferLetterFileEntry(projectId)).thenReturn(serviceSuccess());

        mockMvc.perform(delete("/project/{projectId}/grant-offer", projectId)).
                andExpect(status().isNoContent()).
                andDo(document("project/{method-name}"));
    }

    @Test
    public void removeSignedGrantOfferLetterFile() throws Exception {

        when(grantOfferLetterServiceMock.removeSignedGrantOfferLetterFileEntry(projectId)).thenReturn(serviceSuccess());

        mockMvc.perform(delete("/project/{projectId}/signed-grant-offer-letter", projectId)).
                andExpect(status().isNoContent()).
                andDo(document("project/{method-name}"));
    }

    @Test
    public void approveOrRejectSignedGrantOfferLetter() throws Exception {

        GrantOfferLetterApprovalResource grantOfferLetterApprovalResource = new GrantOfferLetterApprovalResource(ApprovalType.APPROVED, null);
        when(grantOfferLetterServiceMock.approveOrRejectSignedGrantOfferLetter(projectId, grantOfferLetterApprovalResource)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/signed-grant-offer-letter/approval", projectId)
                        .contentType(APPLICATION_JSON)
                        .content(toJson(grantOfferLetterApprovalResource)))
                .andExpect(status().isOk())
                .andReturn();

        verify(grantOfferLetterServiceMock).approveOrRejectSignedGrantOfferLetter(projectId, grantOfferLetterApprovalResource);
    }

    @Test
    public void getGrantOfferLetterState() throws Exception {

        GrantOfferLetterStateResource stateInformation = stateInformationForNonPartnersView(GrantOfferLetterState.APPROVED, GOL_SIGNED);

        when(grantOfferLetterServiceMock.getGrantOfferLetterState(projectId)).thenReturn(serviceSuccess(stateInformation));

        mockMvc.perform(get("/project/{projectId}/grant-offer-letter/current-state", 123L))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(stateInformation)))
                .andDo(document("project/grant-offer-letter/current-state/{method-name}"))
                .andReturn();

        verify(grantOfferLetterServiceMock).getGrantOfferLetterState(projectId);
    }
}
