package com.worth.ifs.project.otherdocuments.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.address.resource.AddressResource;
import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.application.resource.CompetitionSummaryResource;
import com.worth.ifs.commons.error.Error;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.file.controller.viewmodel.FileDetailsViewModel;
import com.worth.ifs.file.resource.FileEntryResource;
import com.worth.ifs.project.builder.ProjectResourceBuilder;
import com.worth.ifs.project.monitoringofficer.viewmodel.ProjectMonitoringOfficerViewModel;
import com.worth.ifs.project.otherdocuments.controller.ProjectOtherDocumentsController;
import com.worth.ifs.project.otherdocuments.viewmodel.ProjectOtherDocumentsViewModel;
import com.worth.ifs.project.resource.MonitoringOfficerResource;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.ProjectUserResource;
import com.worth.ifs.user.builder.OrganisationResourceBuilder;
import com.worth.ifs.user.resource.OrganisationResource;
import org.junit.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static com.worth.ifs.BaseBuilderAmendFunctions.id;
import static com.worth.ifs.address.builder.AddressResourceBuilder.newAddressResource;
import static com.worth.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static com.worth.ifs.application.builder.CompetitionSummaryResourceBuilder.newCompetitionSummaryResource;
import static com.worth.ifs.commons.error.CommonErrors.unsupportedMediaTypeError;
import static com.worth.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_OTHER_DOCUMENTS_MUST_BE_UPLOADED_BEFORE_SUBMIT;
import static com.worth.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static com.worth.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static com.worth.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static com.worth.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static com.worth.ifs.user.resource.UserRoleType.PROJECT_MANAGER;
import static org.apache.coyote.http11.Constants.a;
import static org.mockito.Mockito.when;
import static com.worth.ifs.commons.service.ServiceResult.serviceFailure;
import static com.worth.ifs.commons.service.ServiceResult.serviceSuccess;
import static com.worth.ifs.file.resource.builders.FileEntryResourceBuilder.newFileEntryResource;
import static com.worth.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static com.worth.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_ATOM_XML;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class ProjectOtherDocumentsControllerMockMvcTest extends BaseControllerMockMVCTest<ProjectOtherDocumentsController> {

    long projectId = 123L;

    OrganisationResource leadOrganisation = newOrganisationResource().withId(1L).withName("Test Lead Organisation").build();

    private void setupViewOtherDocumentsTestExpectations(ProjectResource project) {

        List<ProjectUserResource> projectUsers = newProjectUserResource().with(id(999L)).withUserName("Dave Smith").withPhoneNumber("01234123123")
                .withEmail("d@d.com")
                .withRoleName(PROJECT_MANAGER.getName()).build(1);

        List<OrganisationResource> partnerOrganisations = newOrganisationResource().build(3);

        when(projectService.getById(projectId)).thenReturn(project);
        when(projectService.getCollaborationAgreementFileDetails(projectId)).thenReturn(Optional.empty());
        when(projectService.getExploitationPlanFileDetails(projectId)).thenReturn(Optional.empty());
        when(projectService.getPartnerOrganisationsForProject(projectId)).thenReturn(partnerOrganisations);
        when(projectService.getLeadOrganisation(projectId)).thenReturn(leadOrganisation);
        when(projectService.getProjectUsersForProject(project.getId())).thenReturn(projectUsers);
    }


    private void assertProjectDetailsPrepopulatedOk(ProjectOtherDocumentsViewModel model) {

        assertEquals(Long.valueOf(123), model.getProjectId());
        assertEquals("Project 1", model.getProjectName());
        assertEquals("Test Lead Organisation", model.getLeadPartnerOrganisationName());
        assertEquals("Dave Smith", model.getProjectManagerName());
        assertEquals("01234123123", model.getProjectManagerTelephone());
        assertEquals("d@d.com", model.getProjectManagerEmail());

        List<String> testOrgList= new ArrayList<String>(Arrays.asList("OrganisationResource 1", "OrganisationResource 2", "OrganisationResource 3"));
        assertEquals(asList(testOrgList), asList(model.getPartnerOrganisationNames()));
    }

    @Test
    public void testViewOtherDocumentsPage() throws Exception {

        ProjectResource project = newProjectResource().withId(projectId).build();

        setupViewOtherDocumentsTestExpectations (project);

        MvcResult result = mockMvc.perform(get("/project/123/other-documents")).
                andExpect(view().name("project/other-documents")).
                andReturn();

        Map<String, Object> modelMap = result.getModelAndView().getModel();
        ProjectOtherDocumentsViewModel model = (ProjectOtherDocumentsViewModel) result.getModelAndView().getModel().get("model");

        // assert the project details are correct
        assertProjectDetailsPrepopulatedOk(model);

    }

    @Test
    public void testViewOtherDocumentsPageWithExistingDocuments() throws Exception {

        ProjectResource project = newProjectResource().withId(projectId).build();

        setupViewOtherDocumentsTestExpectations (project);

        FileEntryResource existingCollaborationAgreement = newFileEntryResource().build();
        FileEntryResource existingExplotationPlan = newFileEntryResource().build();

        when(projectService.getCollaborationAgreementFileDetails(projectId)).thenReturn(Optional.of(existingCollaborationAgreement));
        when(projectService.getExploitationPlanFileDetails(projectId)).thenReturn(Optional.of(existingExplotationPlan));

        MvcResult result = mockMvc.perform(get("/project/123/other-documents")).
                andExpect(view().name("project/other-documents")).
                andReturn();

        Map<String, Object> modelMap = result.getModelAndView().getModel();
        ProjectOtherDocumentsViewModel model = (ProjectOtherDocumentsViewModel) result.getModelAndView().getModel().get("model");

        // assert the project details are correct
        assertProjectDetailsPrepopulatedOk(model);

        FileDetailsViewModel expectedCollaborationAgreement = new FileDetailsViewModel(existingCollaborationAgreement);
        FileDetailsViewModel expectedExploitationPlan = new FileDetailsViewModel(existingExplotationPlan);

   }

    @Test
    public void testDownloadCollaborationAgreement() throws Exception {

        FileEntryResource fileDetails = newFileEntryResource().withName("A name").build();
        ByteArrayResource fileContents = new ByteArrayResource("My content!".getBytes());

        when(projectService.getCollaborationAgreementFile(123L)).
                thenReturn(Optional.of(fileContents));

        when(projectService.getCollaborationAgreementFileDetails(123L)).
                thenReturn(Optional.of(fileDetails));

        MvcResult result = mockMvc.perform(get("/project/123/other-documents/collaboration-agreement")).
                andExpect(status().isOk()).
                andReturn();

        assertEquals("My content!", result.getResponse().getContentAsString());
        assertEquals("inline; filename=\"" + fileDetails.getName() + "\"",
                result.getResponse().getHeader("Content-Disposition"));
    }

    @Test
    public void testDownloadCollaborationAgreementButFileDoesntExist() throws Exception {

        when(projectService.getExploitationPlanFile(123L)).
                thenReturn(Optional.empty());

        when(projectService.getExploitationPlanFileDetails(123L)).
                thenReturn(Optional.empty());

        mockMvc.perform(get("/project/123/other-documents/exploitation-plan")).
                andExpect(status().isNotFound()).
                andExpect(view().name("404"));
    }

    @Test
    public void testDownloadExploitationPlan() throws Exception {

        FileEntryResource fileDetails = newFileEntryResource().withName("A name").build();
        ByteArrayResource fileContents = new ByteArrayResource("My content!".getBytes());

        when(projectService.getExploitationPlanFile(123L)).
                thenReturn(Optional.of(fileContents));

        when(projectService.getExploitationPlanFileDetails(123L)).
                thenReturn(Optional.of(fileDetails));

        MvcResult result = mockMvc.perform(get("/project/123/other-documents/exploitation-plan")).
                andExpect(status().isOk()).
                andReturn();

        assertEquals("My content!", result.getResponse().getContentAsString());
        assertEquals("inline; filename=\"" + fileDetails.getName() + "\"",
                result.getResponse().getHeader("Content-Disposition"));
    }

    @Test
    public void testDownloadExploitationPlanButFileDoesntExist() throws Exception {

        when(projectService.getExploitationPlanFile(123L)).
                thenReturn(Optional.empty());

        when(projectService.getExploitationPlanFileDetails(123L)).
                thenReturn(Optional.empty());

        mockMvc.perform(get("/project/123/other-documents/exploitation-plan")).
                andExpect(status().isNotFound()).
                andExpect(view().name("404"));
    }

    @Override
    protected ProjectOtherDocumentsController supplyControllerUnderTest() {
        return new ProjectOtherDocumentsController();
    }
}