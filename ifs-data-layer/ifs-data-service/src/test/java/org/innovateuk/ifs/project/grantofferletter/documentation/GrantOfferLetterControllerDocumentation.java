package org.innovateuk.ifs.project.grantofferletter.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.project.grantofferletter.resource.GrantOfferLetterState;
import org.innovateuk.ifs.project.grantofferletter.controller.GrantOfferLetterController;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.project.grantofferletter.transactional.GrantOfferLetterService;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.util.function.BiFunction;
import java.util.function.Function;

import static java.util.Collections.emptyMap;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Api documentation for grant offer letter using ASCII docs
 **/
public class GrantOfferLetterControllerDocumentation extends BaseControllerMockMVCTest<GrantOfferLetterController> {


    @Override
    protected GrantOfferLetterController supplyControllerUnderTest() {
        return new GrantOfferLetterController();
    }

    @Test
    public void addGrantOfferLetter() throws Exception {

        Long projectId = 123L;

        BiFunction<GrantOfferLetterService, FileEntryResource, ServiceResult<FileEntryResource>> serviceCallToUpload =
                (service, fileToUpload) -> service.createSignedGrantOfferLetterFileEntry(eq(projectId), eq(fileToUpload), fileUploadInputStreamExpectations());

        assertFileUploadProcess("/project/" + projectId + "/signed-grant-offer", grantOfferLetterServiceMock, serviceCallToUpload).
                andDo(documentFileUploadMethod("project/{method-name}"));
    }

    @Test
    public void removeGrantOfferLetterFile() throws Exception {

        Long projectId = 123L;

        when(grantOfferLetterServiceMock.removeGrantOfferLetterFileEntry(projectId)).thenReturn(serviceSuccess());

        mockMvc.perform(delete("/project/{projectId}/grant-offer", projectId)).
                andExpect(status().isNoContent())
                .andDo(document("project/{method-name}",
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project for which Grant Offer Letter needs to be removed")
                        )
                ));
    }

    @Test
    public void removeSignedGrantOfferLetterFile() throws Exception {

        Long projectId = 123L;

        when(grantOfferLetterServiceMock.removeSignedGrantOfferLetterFileEntry(projectId)).thenReturn(serviceSuccess());

        mockMvc.perform(delete("/project/{projectId}/signed-grant-offer-letter", projectId)).
                andExpect(status().isNoContent())
                .andDo(document("project/{method-name}",
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project for which Signed Grant Offer Letter needs to be removed")
                        )
                ));
    }

    @Test
    public void addGeneratedGrantOfferLetter() throws Exception {

        Long projectId = 111L;

        BiFunction<GrantOfferLetterService, FileEntryResource, ServiceResult<FileEntryResource>> serviceCallToUpload =
                (service, fileToUpload) -> service.createGrantOfferLetterFileEntry(eq(projectId), eq(fileToUpload), fileUploadInputStreamExpectations());

        assertFileUploadProcess("/project/" + projectId + "/grant-offer", grantOfferLetterServiceMock, serviceCallToUpload).
                andDo(documentFileUploadMethod("project/{method-name}"));
    }

    @Test
    public void addAdditionalContractFile() throws Exception {

        Long projectId = 123L;

        BiFunction<GrantOfferLetterService, FileEntryResource, ServiceResult<FileEntryResource>> serviceCallToUpload =
                (service, fileToUpload) -> service.createAdditionalContractFileEntry(eq(projectId), eq(fileToUpload), fileUploadInputStreamExpectations());

        assertFileUploadProcess("/project/" + projectId + "/additional-contract", grantOfferLetterServiceMock, serviceCallToUpload).
                andDo(documentFileUploadMethod("project/{method-name}"));
    }

    @Test
    public void getGrantOfferLetterFileEntryDetails() throws Exception {

        Long projectId = 123L;

        Function<GrantOfferLetterService, ServiceResult<FileEntryResource>> serviceCallToUpload =
                (service) -> service.getSignedGrantOfferLetterFileEntryDetails(projectId);

        assertGetFileDetails("/project/{projectId}/signed-grant-offer/details", new Object[]{projectId}, emptyMap(),
                grantOfferLetterServiceMock, serviceCallToUpload).
                andDo(documentFileGetDetailsMethod("project/{method-name}"));
    }

    @Test
    public void getGeneratedGrantOfferLetterFileEntryDetails() throws Exception {

        Long projectId = 123L;

        Function<GrantOfferLetterService, ServiceResult<FileEntryResource>> serviceCallToUpload =
                (service) -> service.getGrantOfferLetterFileEntryDetails(projectId);

        assertGetFileDetails("/project/{projectId}/grant-offer/details", new Object[]{projectId}, emptyMap(),
                grantOfferLetterServiceMock, serviceCallToUpload).
                andDo(documentFileGetDetailsMethod("project/{method-name}"));
    }


    @Test
    public void getAdditionalContractFileEntryDetails() throws Exception {

        Long projectId = 123L;

        Function<GrantOfferLetterService, ServiceResult<FileEntryResource>> serviceCallToUpload =
                (service) -> service.getAdditionalContractFileEntryDetails(projectId);

        assertGetFileDetails("/project/{projectId}/additional-contract/details", new Object[]{projectId}, emptyMap(),
                grantOfferLetterServiceMock, serviceCallToUpload).
                andDo(documentFileGetDetailsMethod("project/{method-name}"));
    }

    @Test
    public void getAdditionalContractFileContents() throws Exception {

        Long projectId = 123L;

        Function<GrantOfferLetterService, ServiceResult<FileAndContents>> serviceCallToUpload =
                (service) -> service.getAdditionalContractFileAndContents(projectId);

        assertGetFileContents("/project/{projectId}/additional-contract", new Object[]{projectId},
                emptyMap(), grantOfferLetterServiceMock, serviceCallToUpload).
                andDo(documentFileGetContentsMethod("project/{method-name}"));
    }

    @Test
    public void getGrantOfferLetterFileContents() throws Exception {

        Long projectId = 123L;

        Function<GrantOfferLetterService, ServiceResult<FileAndContents>> serviceCallToUpload =
                (service) -> service.getSignedGrantOfferLetterFileAndContents(projectId);


        assertGetFileContents("/project/{projectId}/signed-grant-offer", new Object[]{projectId},
                emptyMap(), grantOfferLetterServiceMock, serviceCallToUpload).
                andDo(documentFileGetContentsMethod("project/{method-name}"));
    }

    @Test
    public void getGeneratedGrantOfferLetterFileContents() throws Exception {

        Long projectId = 123L;

        Function<GrantOfferLetterService, ServiceResult<FileAndContents>> serviceCallToUpload =
                (service) -> service.getGrantOfferLetterFileAndContents(projectId);


        assertGetFileContents("/project/{projectId}/grant-offer", new Object[]{projectId},
                emptyMap(), grantOfferLetterServiceMock, serviceCallToUpload).
                andDo(documentFileGetContentsMethod("project/{method-name}"));
    }

    @Test
    public void postSubmitGrantOfferLetter() throws Exception {
        Long projectId = 123L;

        when(grantOfferLetterServiceMock.submitGrantOfferLetter(projectId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{id}/grant-offer/submit", projectId))
                .andDo(document("project/{method-name}",
                        pathParameters(
                                parameterWithName("id").description("Id of the project whos offer letter is being submitted")
                        )
                ));
    }

    @Test
    public void sendGrantOfferLetter() throws Exception {
        when(grantOfferLetterServiceMock.sendGrantOfferLetter(123L)).thenReturn(serviceSuccess());
        mockMvc.perform(post("/project/{projectId}/grant-offer/send", 123L))
                .andExpect(status().isOk())
                .andDo(document("project/{method-name}",
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project for which the documents are being submitted to.")
                        )));
    }

    @Test
    public void isSendGrantOfferLetterAllowed() throws Exception {
        when(grantOfferLetterServiceMock.isSendGrantOfferLetterAllowed(123L)).thenReturn(ServiceResult.serviceSuccess(Boolean.TRUE));
        MvcResult mvcResult = mockMvc.perform(get("/project/{projectId}/is-send-grant-offer-letter-allowed", 123L))
                .andExpect(status().isOk())
                .andDo(document("project/{method-name}",
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project for which the documents are being submitted to.")
                        )))
                .andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().equals("true"));
    }

    @Test
    public void isGrantOfferLetterAlreadySent() throws Exception {
        when(grantOfferLetterServiceMock.isGrantOfferLetterAlreadySent(123L)).thenReturn(ServiceResult.serviceSuccess(Boolean.TRUE));
        MvcResult mvcResult = mockMvc.perform(get("/project/{projectId}/is-grant-offer-letter-already-sent", 123L))
                .andExpect(status().isOk())
                .andDo(document("project/{method-name}",
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project for which the documents are being submitted to.")
                        )))
                .andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().equals("true"));
    }

    @Test
    public void approveOrRejectSignedGrantOfferLetter() throws Exception{
        when(grantOfferLetterServiceMock.approveOrRejectSignedGrantOfferLetter(123L, ApprovalType.APPROVED)).thenReturn(ServiceResult.serviceSuccess());
        mockMvc.perform(post("/project/{projectId}/signed-grant-offer-letter/approval/{approvalType}", 123L, ApprovalType.APPROVED))
                .andExpect(status().isOk())
                .andDo(document("project/{method-name}",
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project for which the signed Grant Offer Letter is being approved/rejected."),
                                parameterWithName("approvalType").description("Approval or rejection.")
                        )))
                .andReturn();
    }

    @Test
    public void isSignedGrantOfferLetterApproved() throws Exception{
        when(grantOfferLetterServiceMock.isSignedGrantOfferLetterApproved(123L)).thenReturn(ServiceResult.serviceSuccess(Boolean.TRUE));
        MvcResult mvcResult = mockMvc.perform(get("/project/{projectId}/signed-grant-offer-letter/approval", 123L))
                .andExpect(status().isOk())
                .andDo(document("project/{method-name}",
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project for which the approval status of the signed Grant Offer Letter is requested.")
                        )))
                .andReturn();
        assertTrue(mvcResult.getResponse().getContentAsString().equals("true"));
    }

    @Test
    public void getGrantOfferLetterWorkflowState() throws Exception {

        Long projectId = 123L;

        when(grantOfferLetterServiceMock.getGrantOfferLetterWorkflowState(projectId)).thenReturn(serviceSuccess(GrantOfferLetterState.APPROVED));

        mockMvc.perform(get("/project/{projectId}/grant-offer-letter/state", 123L))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(GrantOfferLetterState.APPROVED)))
                .andDo(document("project/grant-offer-letter/state/{method-name}",
                        pathParameters(
                                parameterWithName("projectId").description("Id of the project for which Grant Offer Letter Workflow state is being retrieved.")
                        )
                        )
                )
                .andReturn();

        verify(grantOfferLetterServiceMock).getGrantOfferLetterWorkflowState(projectId);
    }

}
