package org.innovateuk.ifs.project.projectdetails.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.file.service.FileAndContents;
import org.innovateuk.ifs.project.transactional.ProjectGrantOfferService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;

import java.util.function.BiFunction;
import java.util.function.Function;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static java.util.Collections.emptyMap;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class ProjectGrantOfferControllerTest extends BaseControllerMockMVCTest<ProjectGrantOfferController> {

    private RestDocumentationResultHandler document;

    @Before
    public void setUpDocumentation() throws Exception {
        this.document = document("project/{method-name}",
                preprocessResponse(prettyPrint()));
    }


    @Test
    public void getGrantOfferLetterFileContents() throws Exception {
        Long projectId = 123L;

        Function<ProjectGrantOfferService, ServiceResult<FileAndContents>> serviceCallToUpload =
                (service) -> service.getGrantOfferLetterFileAndContents(projectId);

        assertGetFileContents("/project/{projectId}/grant-offer", new Object[] {projectId},
                emptyMap(), projectGrantOfferServiceMock, serviceCallToUpload).
                andDo(documentFileGetContentsMethod(document));
    }

    @Test
    public void getAdditionalContractrFileContents() throws Exception {
        Long projectId = 123L;

        Function<ProjectGrantOfferService, ServiceResult<FileAndContents>> serviceCallToUpload =
                (service) -> service.getAdditionalContractFileAndContents(projectId);

        assertGetFileContents("/project/{projectId}/additional-contract", new Object[] {projectId},
                emptyMap(), projectGrantOfferServiceMock, serviceCallToUpload).
                andDo(documentFileGetContentsMethod(document));
    }


    @Test
    public void getGrantOfferLetterFileEntryDetails() throws Exception {
        Long projectId = 123L;

        Function<ProjectGrantOfferService, ServiceResult<FileEntryResource>> serviceCallToUpload =
                (service) -> service.getSignedGrantOfferLetterFileEntryDetails(projectId);

        assertGetFileDetails("/project/{projectId}/signed-grant-offer/details", new Object[] {projectId},
                emptyMap(),
                projectGrantOfferServiceMock, serviceCallToUpload).
                andDo(documentFileGetDetailsMethod(document));
    }
    @Test
    public void getAdditionaContractFileEntryDetails() throws Exception {
        Long projectId = 123L;

        Function<ProjectGrantOfferService, ServiceResult<FileEntryResource>> serviceCallToUpload =
                (service) -> service.getAdditionalContractFileEntryDetails(projectId);

        assertGetFileDetails("/project/{projectId}/additional-contract/details", new Object[] {projectId},
                emptyMap(),
                projectGrantOfferServiceMock, serviceCallToUpload).
                andDo(documentFileGetDetailsMethod(document));
    }


    @Test
    public void addGrantOfferLetter() throws Exception {
        Long projectId = 123L;

        BiFunction<ProjectGrantOfferService, FileEntryResource, ServiceResult<FileEntryResource>> serviceCallToUpload =
                (service, fileToUpload) -> service.createSignedGrantOfferLetterFileEntry(eq(projectId), eq(fileToUpload), fileUploadInputStreamExpectations());

        assertFileUploadProcess("/project/" + projectId + "/signed-grant-offer/", projectGrantOfferServiceMock, serviceCallToUpload).
                andDo(documentFileUploadMethod(document));
    }

    @Test
    public void addAdditionalContractFile() throws Exception {
        Long projectId = 123L;

        BiFunction<ProjectGrantOfferService, FileEntryResource, ServiceResult<FileEntryResource>> serviceCallToUpload =
                (service, fileToUpload) -> service.createAdditionalContractFileEntry(eq(projectId), eq(fileToUpload), fileUploadInputStreamExpectations());

        assertFileUploadProcess("/project/" + projectId + "/additional-contract/", projectGrantOfferServiceMock, serviceCallToUpload).
                andDo(documentFileUploadMethod(document));

    }

    @Test
    public void removeGrantOfferLetterFile() throws Exception {

        Long projectId = 123L;

        when(projectGrantOfferServiceMock.removeGrantOfferLetterFileEntry(projectId)).thenReturn(serviceSuccess());

        mockMvc.perform(delete("/project/{projectId}/grant-offer", projectId)).
                andExpect(status().isNoContent());
    }

    @Test
    public void removeSignedGrantOfferLetterFile() throws Exception {

        Long projectId = 123L;

        when(projectGrantOfferServiceMock.removeSignedGrantOfferLetterFileEntry(projectId)).thenReturn(serviceSuccess());

        mockMvc.perform(delete("/project/{projectId}/signed-grant-offer-letter", projectId)).
                andExpect(status().isNoContent());
    }

    @Override
    protected ProjectGrantOfferController supplyControllerUnderTest() {
        return new ProjectGrantOfferController();
    }
}
