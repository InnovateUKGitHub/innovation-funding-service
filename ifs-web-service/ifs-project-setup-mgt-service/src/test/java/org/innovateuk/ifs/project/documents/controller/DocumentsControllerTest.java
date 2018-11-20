package org.innovateuk.ifs.project.documents.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.file.controller.viewmodel.FileDetailsViewModel;
import org.innovateuk.ifs.file.resource.FileEntryResource;
import org.innovateuk.ifs.project.document.resource.DocumentStatus;
import org.innovateuk.ifs.project.document.resource.ProjectDocumentDecision;
import org.innovateuk.ifs.project.documents.form.DocumentForm;
import org.innovateuk.ifs.project.documents.populator.DocumentsPopulator;
import org.innovateuk.ifs.project.documents.service.DocumentsRestService;
import org.innovateuk.ifs.project.documents.viewmodel.AllDocumentsViewModel;
import org.innovateuk.ifs.project.documents.viewmodel.DocumentViewModel;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_PROJECT_DOCUMENT_CANNOT_BE_ACCEPTED_OR_REJECTED;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.file.builder.FileEntryResourceBuilder.newFileEntryResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class DocumentsControllerTest extends BaseControllerMockMVCTest<DocumentsController> {

    @Mock
    private DocumentsPopulator populator;

    @Mock
    private DocumentsRestService documentsRestService;

    @Test
    public void viewAllDocuments() throws Exception {

        long projectId = 1L;
        AllDocumentsViewModel viewModel = new AllDocumentsViewModel(18L, 19L, projectId, "Project 12", emptyList());

        when(populator.populateAllDocuments(projectId)).thenReturn(viewModel);
        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/document/all"))
                .andExpect(view().name("project/documents-all"))
                .andReturn();

        AllDocumentsViewModel returnedViewModel = (AllDocumentsViewModel) result.getModelAndView().getModel().get("model");
        assertEquals(viewModel, returnedViewModel);
    }

    @Test
    public void viewDocument() throws Exception {

        long projectId = 1L;
        long documentConfigId = 2L;

        DocumentViewModel viewModel = new DocumentViewModel(projectId, "Project 12", 19L,
                documentConfigId, "Risk Register", null, DocumentStatus.UNSET, "");

        when(populator.populateViewDocument(projectId, documentConfigId)).thenReturn(viewModel);
        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/document/config/" + documentConfigId))
                .andExpect(view().name("project/document"))
                .andReturn();

        Map<String, Object> model = result.getModelAndView().getModel();
        DocumentViewModel returnedViewModel = (DocumentViewModel) model.get("model");
        assertEquals(viewModel, returnedViewModel);
        assertEquals(new DocumentForm(), model.get("form"));
    }
    @Test
    public void downloadDocumentWhenFileDoesNotExist() throws Exception {

        long projectId = 1L;
        long documentConfigId = 2L;

        when(documentsRestService.getFileContents(projectId, documentConfigId)).
                thenReturn(restSuccess(Optional.empty()));

        when(documentsRestService.getFileEntryDetails(projectId, documentConfigId)).
                thenReturn(restSuccess(Optional.empty()));

        mockMvc.perform(get("/project/" + projectId + "/document/config/" + documentConfigId + "/download"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("404"));
    }
    @Test
    public void downloadDocument() throws Exception {

        long projectId = 1L;
        long documentConfigId = 2L;

        ByteArrayResource fileContents = new ByteArrayResource("My content!".getBytes());
        FileEntryResource fileEntryDetails = newFileEntryResource().withName("Risk Register").build();

        when(documentsRestService.getFileContents(projectId, documentConfigId)).
                thenReturn(restSuccess(Optional.of(fileContents)));

        when(documentsRestService.getFileEntryDetails(projectId, documentConfigId)).
                thenReturn(restSuccess(Optional.of(fileEntryDetails)));

        MvcResult result = mockMvc.perform(get("/project/" + projectId + "/document/config/" + documentConfigId + "/download")).
                andExpect(status().isOk()).
                andReturn();

        assertEquals("My content!", result.getResponse().getContentAsString());
        assertEquals("inline; filename=\"" + fileEntryDetails.getName() + "\"",
                result.getResponse().getHeader("Content-Disposition"));
    }

    @Test
    public void documentDecision() throws Exception {

        long projectId = 1L;
        long documentConfigId = 2L;

        when(documentsRestService.documentDecision(projectId, documentConfigId, new ProjectDocumentDecision(true, null))).thenReturn(restSuccess());

        mockMvc.perform(
                post("/project/" + projectId + "/document/config/" + documentConfigId)
                        .param("approved", "true"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/project/" + projectId + "/document/config/" + documentConfigId));

        verify(documentsRestService).documentDecision(projectId, documentConfigId, new ProjectDocumentDecision(true, null));
    }
    @Test
    public void submitDocumentFailure() throws Exception {

        long projectId = 1L;
        long documentConfigId = 2L;

        when(documentsRestService.documentDecision(projectId, documentConfigId, new ProjectDocumentDecision(true, null))).
                thenReturn(restFailure(PROJECT_SETUP_PROJECT_DOCUMENT_CANNOT_BE_ACCEPTED_OR_REJECTED));

        FileEntryResource fileEntryDetails = newFileEntryResource().withName("Risk Register").build();
        FileDetailsViewModel fileDetailsViewModel = new FileDetailsViewModel(fileEntryDetails);

        DocumentViewModel viewModel = new DocumentViewModel(projectId, "Project 12", 19L,
                documentConfigId, "Risk Register", fileDetailsViewModel, DocumentStatus.APPROVED, "");

        when(populator.populateViewDocument(projectId, documentConfigId)).thenReturn(viewModel);

        MvcResult result = mockMvc.perform(
                post("/project/" + projectId + "/document/config/" + documentConfigId)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("project/document"))
                .andReturn();

        Map<String, Object> model = result.getModelAndView().getModel();
        DocumentViewModel returnedViewModel = (DocumentViewModel) model.get("model");
        DocumentForm form = new DocumentForm();
        form.setApproved(true);

        assertEquals(viewModel, returnedViewModel);
        assertEquals(form, model.get("form"));
    }

    @Override
    protected DocumentsController supplyControllerUnderTest() {
        return new DocumentsController(populator, documentsRestService);
    }
}