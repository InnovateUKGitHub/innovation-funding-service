package org.innovateuk.ifs.project.projectdetails.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.builder.PartnerOrganisationResourceBuilder;
import org.innovateuk.ifs.project.constant.ProjectActivityStates;
import org.innovateuk.ifs.project.projectdetails.form.PartnerProjectLocationForm;
import org.innovateuk.ifs.project.projectdetails.form.ProjectDetailsStartDateForm;
import org.innovateuk.ifs.project.projectdetails.viewmodel.PartnerProjectLocationViewModel;
import org.innovateuk.ifs.project.projectdetails.viewmodel.ProjectDetailsStartDateViewModel;
import org.innovateuk.ifs.project.projectdetails.viewmodel.ProjectDetailsViewModel;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.service.PartnerOrganisationRestService;
import org.innovateuk.ifs.project.status.populator.SetupStatusViewModelPopulator;
import org.innovateuk.ifs.project.status.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.projectdetails.ProjectDetailsService;
import org.innovateuk.ifs.status.StatusService;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.user.service.UserRestService;
import org.innovateuk.ifs.user.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.name;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_LOCATION_CANNOT_BE_UPDATED_IF_GOL_GENERATED;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.project.AddressLookupBaseController.FORM_ATTR_NAME;
import static org.innovateuk.ifs.project.builder.ProjectPartnerStatusResourceBuilder.newProjectPartnerStatusResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.builder.ProjectTeamStatusResourceBuilder.newProjectTeamStatusResource;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.innovateuk.ifs.user.resource.Role.PARTNER;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(MockitoJUnitRunner.Silent.class)
public class ProjectDetailsControllerTest extends BaseControllerMockMVCTest<ProjectDetailsController> {

    @Mock
    private SetupStatusViewModelPopulator setupStatusViewModelPopulatorMock;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private ProjectService projectService;

    @Mock
    private PartnerOrganisationRestService partnerOrganisationRestService;

    @Mock
    private StatusService statusService;

    @Mock
    private OrganisationRestService organisationRestService;

    @Mock
    private ProjectDetailsService projectDetailsService;

    @Mock
    private UserService userService;

    @Mock
    private UserRestService userRestService;

    @Override
    protected ProjectDetailsController supplyControllerUnderTest() {
        return new ProjectDetailsController();
    }

    @Test
    public void testProjectDetails() throws Exception {
        Long projectId = 20L;

        boolean partnerProjectLocationRequired = true;
        CompetitionResource competitionResource = newCompetitionResource()
                .withLocationPerPartner(partnerProjectLocationRequired)
                .build();
        ProjectResource project = newProjectResource()
                .withId(projectId)
                .withCompetition(competitionResource.getId())
                .build();

        OrganisationResource leadOrganisation = newOrganisationResource().build();

        List<ProjectUserResource> projectUsers = newProjectUserResource().
                withUser(loggedInUser.getId()).
                withOrganisation(leadOrganisation.getId()).
                withRole(PARTNER).
                build(1);

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectPartnerStatusResource()
                        .withIsLeadPartner(true)
                        .withMonitoringOfficerStatus(ProjectActivityStates.NOT_STARTED)
                        .withSpendProfileStatus(ProjectActivityStates.PENDING)
                        .withGrantOfferStatus(ProjectActivityStates.NOT_REQUIRED).build()).
                build();

        when(competitionRestService.getCompetitionById(competitionResource.getId())).thenReturn(restSuccess(competitionResource));
        when(projectService.getById(project.getId())).thenReturn(project);
        when(projectService.getProjectUsersForProject(project.getId())).thenReturn(projectUsers);
        when(projectService.getLeadOrganisation(project.getId())).thenReturn(leadOrganisation);
        when(organisationRestService.getOrganisationById(leadOrganisation.getId())).thenReturn(restSuccess(leadOrganisation));
        when(projectService.isUserLeadPartner(projectId, loggedInUser.getId())).thenReturn(true);
        List<PartnerOrganisationResource> partnerOrganisationResourceList = PartnerOrganisationResourceBuilder.newPartnerOrganisationResource().build(3);
        when(partnerOrganisationRestService.getProjectPartnerOrganisations(projectId)).thenReturn(restSuccess(partnerOrganisationResourceList));

        when(statusService.getProjectTeamStatus(projectId, Optional.empty())).thenReturn(teamStatus);
        when(setupStatusViewModelPopulatorMock.checkLeadPartnerProjectDetailsProcessCompleted(teamStatus)).thenReturn(true);

        when(organisationRestService.getOrganisationById(leadOrganisation.getId())).thenReturn(restSuccess(leadOrganisation));

        MvcResult result = mockMvc.perform(get("/project/{id}/details", projectId))
                .andExpect(status().isOk())
                .andExpect(view().name("project/details"))
                .andExpect(model().attributeDoesNotExist("readOnlyView"))
                .andReturn();

        ProjectDetailsViewModel model = (ProjectDetailsViewModel) result.getModelAndView().getModel().get("model");
        assertEquals(project, model.getProject());
        assertEquals(singletonList(leadOrganisation), model.getOrganisations());
        assertTrue(model.isUserLeadPartner());
        assertTrue(model.isSpendProfileGenerated());
        assertFalse(model.isReadOnly());
        assertFalse(model.isGrantOfferLetterGenerated());
    }

    @Test
    public void testProjectDetailsReadOnlyView() throws Exception {
        Long projectId = 20L;

        boolean partnerProjectLocationRequired = true;
        CompetitionResource competitionResource = newCompetitionResource()
                .withLocationPerPartner(partnerProjectLocationRequired)
                .build();
        ProjectResource project = newProjectResource()
                .withId(projectId)
                .withCompetition(competitionResource.getId())
                .build();

        OrganisationResource leadOrganisation = newOrganisationResource().build();

        List<ProjectUserResource> projectUsers = newProjectUserResource().
                withUser(loggedInUser.getId()).
                withOrganisation(leadOrganisation.getId()).
                withRole(PARTNER).
                build(1);

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectPartnerStatusResource()
                        .withIsLeadPartner(true)
                        .withMonitoringOfficerStatus(ProjectActivityStates.NOT_STARTED)
                        .build()).
                build();

        when(competitionRestService.getCompetitionById(competitionResource.getId())).thenReturn(restSuccess(competitionResource));
        when(projectService.getById(project.getId())).thenReturn(project);
        when(projectService.getProjectUsersForProject(project.getId())).thenReturn(projectUsers);
        when(projectService.getLeadOrganisation(project.getId())).thenReturn(leadOrganisation);
        when(organisationRestService.getOrganisationById(leadOrganisation.getId())).thenReturn(restSuccess(leadOrganisation));
        List<PartnerOrganisationResource> partnerOrganisationResourceList = PartnerOrganisationResourceBuilder.newPartnerOrganisationResource().build(3);
        when(partnerOrganisationRestService.getProjectPartnerOrganisations(projectId)).thenReturn(restSuccess(partnerOrganisationResourceList));

        when(projectService.isUserLeadPartner(projectId, loggedInUser.getId())).thenReturn(true);
        when(statusService.getProjectTeamStatus(projectId, Optional.empty())).thenReturn(teamStatus);

        when(organisationRestService.getOrganisationById(leadOrganisation.getId())).thenReturn(restSuccess(leadOrganisation));

        MvcResult result = mockMvc.perform(get("/project/{id}/readonly", projectId))
                .andExpect(status().isOk())
                .andExpect(view().name("project/details"))
                .andReturn();

        ProjectDetailsViewModel model = (ProjectDetailsViewModel) result.getModelAndView().getModel().get("model");

        assertEquals(project, model.getProject());
        assertEquals(singletonList(leadOrganisation), model.getOrganisations());
        assertTrue(model.isUserLeadPartner());
        assertFalse(model.isSpendProfileGenerated());
        assertTrue(model.isReadOnly());
        assertTrue(model.isGrantOfferLetterGenerated());
    }

    @Test
    public void testProjectManagerAndAddressCannotBeChangedWhenGOLAlreadyGenerated() throws Exception {
        Long projectId = 20L;

        boolean partnerProjectLocationRequired = true;
        CompetitionResource competitionResource = newCompetitionResource()
                .withLocationPerPartner(partnerProjectLocationRequired)
                .build();
        ProjectResource project = newProjectResource()
                .withId(projectId)
                .withCompetition(competitionResource.getId())
                .build();

        OrganisationResource leadOrganisation = newOrganisationResource().build();

        List<ProjectUserResource> projectUsers = newProjectUserResource().
                withUser(loggedInUser.getId()).
                withOrganisation(leadOrganisation.getId()).
                withRole(PARTNER).
                build(1);

        ProjectTeamStatusResource teamStatus = newProjectTeamStatusResource().
                withProjectLeadStatus(newProjectPartnerStatusResource().withIsLeadPartner(true).withSpendProfileStatus(ProjectActivityStates.COMPLETE).build()).
                build();

        when(competitionRestService.getCompetitionById(competitionResource.getId())).thenReturn(restSuccess(competitionResource));
        when(projectService.getById(project.getId())).thenReturn(project);
        when(projectService.getProjectUsersForProject(project.getId())).thenReturn(projectUsers);
        when(projectService.getLeadOrganisation(project.getId())).thenReturn(leadOrganisation);
        when(organisationRestService.getOrganisationById(leadOrganisation.getId())).thenReturn(restSuccess(leadOrganisation));
        List<PartnerOrganisationResource> partnerOrganisationResourceList = PartnerOrganisationResourceBuilder.newPartnerOrganisationResource().build(3);
        when(partnerOrganisationRestService.getProjectPartnerOrganisations(projectId)).thenReturn(restSuccess(partnerOrganisationResourceList));
        when(projectService.isUserLeadPartner(projectId, loggedInUser.getId())).thenReturn(true);
        when(statusService.getProjectTeamStatus(projectId, Optional.empty())).thenReturn(teamStatus);

        when(organisationRestService.getOrganisationById(leadOrganisation.getId())).thenReturn(restSuccess(leadOrganisation));

        MvcResult result = mockMvc.perform(get("/project/{id}/details", projectId))
                .andExpect(status().isOk())
                .andExpect(view().name("project/details"))
                .andExpect(model().attributeDoesNotExist("readOnlyView"))
                .andReturn();

        ProjectDetailsViewModel model = (ProjectDetailsViewModel) result.getModelAndView().getModel().get("model");
        assertTrue(model.isGrantOfferLetterGenerated());
    }

    @Test
    public void testViewStartDate() throws Exception {
        ApplicationResource applicationResource = newApplicationResource().build();

        ProjectResource project = newProjectResource().
                withApplication(applicationResource).
                with(name("My Project")).
                withDuration(4L).
                withTargetStartDate(LocalDate.now().withDayOfMonth(5)).
                withDuration(4L).
                build();

        OrganisationResource leadOrganisation = newOrganisationResource().build();
        List<ProjectUserResource> projectUsers = newProjectUserResource().
                withUser(loggedInUser.getId()).
                withOrganisation(leadOrganisation.getId()).
                withRole(PARTNER).
                build(1);

        when(projectService.getById(project.getId())).thenReturn(project);
        when(projectService.getProjectUsersForProject(project.getId())).thenReturn(projectUsers);
        when(projectService.getLeadOrganisation(project.getId())).thenReturn(leadOrganisation);

        MvcResult result = mockMvc.perform(get("/project/{id}/details/start-date", project.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("project/details-start-date"))
                .andReturn();

        Map<String, Object> model = result.getModelAndView().getModel();
        ProjectDetailsStartDateViewModel viewModel = (ProjectDetailsStartDateViewModel) model.get("model");

        assertEquals(project.getId(), viewModel.getProjectId());
        assertEquals(project.getApplication(), (long) viewModel.getApplicationId());
        assertEquals(project.getName(), viewModel.getProjectName());
        assertEquals(project.getDurationInMonths(), Long.valueOf(viewModel.getProjectDurationInMonths()));

        ProjectDetailsStartDateForm form = (ProjectDetailsStartDateForm) model.get(FORM_ATTR_NAME);
        assertEquals(project.getTargetStartDate().withDayOfMonth(1), form.getProjectStartDate());
    }

    @Test
    public void testUpdateStartDate() throws Exception {
        ApplicationResource applicationResource = newApplicationResource().build();
        ProjectResource projectResource = newProjectResource().withApplication(applicationResource).build();

        when(projectService.getById(projectResource.getId())).thenReturn(projectResource);
        when(projectDetailsService.updateProjectStartDate(projectResource.getId(), LocalDate.of(2017, 6, 3))).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{id}/details/start-date", projectResource.getId()).
                contentType(MediaType.APPLICATION_FORM_URLENCODED).
                param("projectStartDate", "projectStartDate").
                param("projectStartDate.dayOfMonth", "3").
                param("projectStartDate.monthValue", "6").
                param("projectStartDate.year", "2017"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/project/" + projectResource.getId() + "/details"))
                .andReturn();

    }

    @Test
    public void testViewPartnerProjectLocation() throws Exception {

        long projectId = 1L;
        long organisationId = 2L;

        ProjectResource projectResource = newProjectResource()
                .withId(projectId)
                .withName("Project 1")
                .build();

        PartnerOrganisationResource partnerOrganisation = PartnerOrganisationResourceBuilder.newPartnerOrganisationResource()
                .withPostcode("TW14 9QG")
                .build();
        when(partnerOrganisationRestService.getPartnerOrganisation(projectId, organisationId)).thenReturn(restSuccess(partnerOrganisation));
        when(projectService.userIsPartnerInOrganisationForProject(projectId, organisationId, loggedInUser.getId())).thenReturn(true);
        when(projectService.getById(projectId)).thenReturn(projectResource);

        MvcResult result = mockMvc.perform(get("/project/{projectId}/organisation/{organisationId}/partner-project-location", projectId, organisationId))
                .andExpect(status().isOk())
                .andExpect(view().name("project/partner-project-location"))
                .andReturn();

        PartnerProjectLocationViewModel model = (PartnerProjectLocationViewModel) result.getModelAndView().getModel().get("model");
        assertEquals(projectId, model.getProjectId());
        assertEquals("Project 1", model.getProjectName());
        assertEquals(organisationId, model.getOrganisationId());

        PartnerProjectLocationForm form = (PartnerProjectLocationForm) result.getModelAndView().getModel().get(FORM_ATTR_NAME);
        assertEquals("TW14 9QG", form.getPostcode());

    }

    @Test
    public void testUpdatePartnerProjectLocationWhenUpdateFails() throws Exception {

        long projectId = 1L;
        long organisationId = 2L;
        String postcode = "UB7 8QF";

        ProjectResource projectResource = newProjectResource()
                .withId(projectId)
                .withName("Project 1")
                .build();

        when(projectDetailsService.updatePartnerProjectLocation(projectId, organisationId, postcode))
                .thenReturn(serviceFailure(PROJECT_SETUP_LOCATION_CANNOT_BE_UPDATED_IF_GOL_GENERATED));
        when(projectService.userIsPartnerInOrganisationForProject(projectId, organisationId, loggedInUser.getId())).thenReturn(true);
        when(projectService.getById(projectId)).thenReturn(projectResource);

        MvcResult result = mockMvc.perform(post("/project/{projectId}/organisation/{organisationId}/partner-project-location", projectId, organisationId).
                contentType(MediaType.APPLICATION_FORM_URLENCODED).
                param("postcode", postcode)).
                andExpect(status().isOk()).
                andExpect(view().name("project/partner-project-location")).
                andReturn();

        PartnerProjectLocationForm form = (PartnerProjectLocationForm) result.getModelAndView().getModel().get(FORM_ATTR_NAME);
        assertEquals(new PartnerProjectLocationForm(postcode), form);
    }

    @Test
    public void testUpdatePartnerProjectLocationSuccess() throws Exception {

        long projectId = 1L;
        long organisationId = 2L;
        String postcode = "UB7 8QF";

        when(projectDetailsService.updatePartnerProjectLocation(projectId, organisationId, postcode))
                .thenReturn(serviceSuccess());

        MvcResult result = mockMvc.perform(post("/project/{projectId}/organisation/{organisationId}/partner-project-location", projectId, organisationId).
                contentType(MediaType.APPLICATION_FORM_URLENCODED).
                param("postcode", postcode)).
                andExpect(status().is3xxRedirection()).
                andExpect(view().name("redirect:/project/" + projectId + "/details")).
                andReturn();

        PartnerProjectLocationForm form = (PartnerProjectLocationForm) result.getModelAndView().getModel().get(FORM_ATTR_NAME);
        assertEquals(new PartnerProjectLocationForm(postcode), form);

        verify(projectService, never()).userIsPartnerInOrganisationForProject(projectId, organisationId, loggedInUser.getId());
        verify(projectService, never()).getById(projectId);
    }
}

