package org.innovateuk.ifs.project.projectdetails.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.builder.PartnerOrganisationResourceBuilder;
import org.innovateuk.ifs.project.projectdetails.form.ProjectDetailsStartDateForm;
import org.innovateuk.ifs.project.projectdetails.form.ProjectDurationForm;
import org.innovateuk.ifs.project.projectdetails.viewmodel.ProjectDetailsStartDateViewModel;
import org.innovateuk.ifs.project.projectdetails.viewmodel.ProjectDetailsViewModel;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.service.PartnerOrganisationRestService;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.projectdetails.ProjectDetailsService;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.innovateuk.ifs.util.NavigationUtils;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.name;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_PROJECT_DURATION_CANNOT_BE_CHANGED_ONCE_SPEND_PROFILE_HAS_BEEN_GENERATED;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class ProjectDetailsControllerTest extends BaseControllerMockMVCTest<ProjectDetailsController> {

    @Mock
    private ProjectService projectService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private PartnerOrganisationRestService partnerOrganisationRestService;

    @Mock
    private OrganisationRestService organisationRestService;

    @Mock
    private ProjectDetailsService projectDetailsService;

    @Mock
    private ProjectRestService projectRestService;

    @Mock
    private ApplicationRestService applicationRestService;

    @Spy
    @SuppressWarnings("unused")
    private NavigationUtils navigationUtils;

    private static final String FORM_ATTR_NAME = "form";

    @Test
    public void viewProjectDetails() throws Exception {
        Long competitionId = 1L;
        Long projectId = 1L;
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(PROJECT_FINANCE)).build());

        CompetitionResource competition = newCompetitionResource()
                .withId(competitionId)
                .withName("Comp 1")
                .withFundingType(FundingType.GRANT)
                .withLocationPerPartner(true)
                .build();

        ProjectResource project = newProjectResource()
                .withId(projectId)
                .withName("Project 1")
                .build();

        OrganisationResource leadOrganisation = newOrganisationResource()
                .withId(1L)
                .withName("Lead Org 1")
                .build();

        OrganisationResource partnerOrganisation = newOrganisationResource()
                .withId(2L)
                .withName("Partner Org 1")
                .build();

        List<ProjectUserResource> projectUsers = buildProjectUsers(leadOrganisation, partnerOrganisation);

        ProjectUserResource projectManagerProjectUser = newProjectUserResource().
                withUser(loggedInUser.getId()).
                withOrganisation(leadOrganisation.getId()).
                withRole(PROJECT_MANAGER).
                build();

        ProjectUserResource leadFinanceContactProjectUser = newProjectUserResource().
                withUser(loggedInUser.getId()).
                withOrganisation(leadOrganisation.getId()).
                withRole(FINANCE_CONTACT).
                build();

        ProjectUserResource partnerFinanceContactProjectUser = newProjectUserResource().
                withUser(2L).
                withOrganisation(partnerOrganisation.getId()).
                withRole(FINANCE_CONTACT).
                build();

        projectUsers.add(projectManagerProjectUser);
        projectUsers.add(leadFinanceContactProjectUser);
        projectUsers.add(partnerFinanceContactProjectUser);

        List<PartnerOrganisationResource> partnerOrganisations = PartnerOrganisationResourceBuilder.newPartnerOrganisationResource()
                .withOrganisation(1L, 2L)
                .withPostcode("TW14 9QG", "UB7 8QF")
                .build(2);

        when(projectService.getById(project.getId())).thenReturn(project);
        when(projectService.getProjectUsersForProject(project.getId())).thenReturn(projectUsers);
        when(projectService.getLeadOrganisation(project.getId())).thenReturn(leadOrganisation);
        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competition));
        when(partnerOrganisationRestService.getProjectPartnerOrganisations(projectId)).thenReturn(restSuccess(partnerOrganisations));
        when(organisationRestService.getOrganisationById(1L)).thenReturn(restSuccess(leadOrganisation));
        when(organisationRestService.getOrganisationById(2L)).thenReturn(restSuccess(partnerOrganisation));

        MvcResult result = mockMvc.perform(get("/competition/" + competitionId + "/project/" + projectId + "/details"))
                .andExpect(view().name("project/detail"))
                .andReturn();

        ProjectDetailsViewModel model = (ProjectDetailsViewModel) result.getModelAndView().getModel().get("model");

        // Assert that the model has the correct values
        assertEquals(project, model.getProject());
        assertEquals(competitionId, model.getCompetitionId());
        assertEquals("Comp 1", model.getCompetitionName());
        assertTrue(model.isAbleToManageProjectState());
        assertEquals("Lead Org 1", model.getLeadOrganisation().getName());
        assertTrue(model.isLocationPerPartnerRequired());
        assertEquals("TW14 9QG", model.getPostcodeForPartnerOrganisation(1L));
        assertEquals("UB7 8QF", model.getPostcodeForPartnerOrganisation(2L));
        assertFalse(model.isKtpCompetition());
    }

    @Test
    public void viewProjectDetailsKtpCompetition() throws Exception {
        Long competitionId = 1L;
        Long projectId = 1L;
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(PROJECT_FINANCE)).build());

        CompetitionResource competition = newCompetitionResource()
                .withId(competitionId)
                .withName("Comp 1")
                .withFundingType(FundingType.KTP)
                .withLocationPerPartner(true)
                .build();

        ProjectResource project = newProjectResource()
                .withId(projectId)
                .withName("Project 1")
                .build();

        OrganisationResource leadOrganisation = newOrganisationResource()
                .withId(1L)
                .withName("Lead Org 1")
                .build();

        OrganisationResource partnerOrganisation = newOrganisationResource()
                .withId(2L)
                .withName("Partner Org 1")
                .build();

        List<ProjectUserResource> projectUsers = buildProjectUsers(leadOrganisation, partnerOrganisation);

        ProjectUserResource projectManagerProjectUser = newProjectUserResource().
                withUser(loggedInUser.getId()).
                withOrganisation(leadOrganisation.getId()).
                withRole(PROJECT_MANAGER).
                build();

        ProjectUserResource leadFinanceContactProjectUser = newProjectUserResource().
                withUser(loggedInUser.getId()).
                withOrganisation(leadOrganisation.getId()).
                withRole(FINANCE_CONTACT).
                build();

        ProjectUserResource partnerFinanceContactProjectUser = newProjectUserResource().
                withUser(2L).
                withOrganisation(partnerOrganisation.getId()).
                withRole(FINANCE_CONTACT).
                build();

        projectUsers.add(projectManagerProjectUser);
        projectUsers.add(leadFinanceContactProjectUser);
        projectUsers.add(partnerFinanceContactProjectUser);

        List<PartnerOrganisationResource> partnerOrganisations = PartnerOrganisationResourceBuilder.newPartnerOrganisationResource()
                .withOrganisation(1L, 2L)
                .withPostcode("TW14 9QG", "UB7 8QF")
                .build(2);

        when(projectService.getById(project.getId())).thenReturn(project);
        when(projectService.getProjectUsersForProject(project.getId())).thenReturn(projectUsers);
        when(projectService.getLeadOrganisation(project.getId())).thenReturn(leadOrganisation);
        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competition));
        when(partnerOrganisationRestService.getProjectPartnerOrganisations(projectId)).thenReturn(restSuccess(partnerOrganisations));
        when(organisationRestService.getOrganisationById(1L)).thenReturn(restSuccess(leadOrganisation));
        when(organisationRestService.getOrganisationById(2L)).thenReturn(restSuccess(partnerOrganisation));

        MvcResult result = mockMvc.perform(get("/competition/" + competitionId + "/project/" + projectId + "/details"))
                .andExpect(view().name("project/detail"))
                .andReturn();

        ProjectDetailsViewModel model = (ProjectDetailsViewModel) result.getModelAndView().getModel().get("model");

        // Assert that the model has the correct values
        assertEquals(project, model.getProject());
        assertEquals(competitionId, model.getCompetitionId());
        assertEquals("Comp 1", model.getCompetitionName());
        assertTrue(model.isAbleToManageProjectState());
        assertEquals("Lead Org 1", model.getLeadOrganisation().getName());
        assertTrue(model.isLocationPerPartnerRequired());
        assertEquals("TW14 9QG", model.getPostcodeForPartnerOrganisation(1L));
        assertEquals("UB7 8QF", model.getPostcodeForPartnerOrganisation(2L));
        assertTrue(model.isKtpCompetition());
    }

    @Test
    public void viewStartDate() throws Exception {
        Long competitionId = 1L;
        ApplicationResource applicationResource = newApplicationResource().build();

        CompetitionResource competition = newCompetitionResource()
                .withId(competitionId)
                .withName("Comp 1")
                .withFundingType(FundingType.GRANT)
                .withLocationPerPartner(true)
                .build();

        ProjectResource project = newProjectResource().
                withCompetition(competition.getId()).
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
        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competition));

        MvcResult result = mockMvc.perform(get("/competition/{competitionId}/project/{projectId}/details/start-date", project.getCompetition(), project.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("project/details-start-date"))
                .andReturn();

        Map<String, Object> model = result.getModelAndView().getModel();
        ProjectDetailsStartDateViewModel viewModel = (ProjectDetailsStartDateViewModel) model.get("model");

        assertEquals(project.getId(), viewModel.getProjectId());
        assertEquals(project.getApplication(), (long) viewModel.getApplicationId());
        assertEquals(project.getName(), viewModel.getProjectName());
        assertEquals(project.getDurationInMonths(), Long.valueOf(viewModel.getProjectDurationInMonths()));
        assertFalse(viewModel.isKtpCompetition());

        ProjectDetailsStartDateForm form = (ProjectDetailsStartDateForm) model.get(FORM_ATTR_NAME);
        assertEquals(project.getTargetStartDate().withDayOfMonth(1), form.getProjectStartDate());
    }

    @Test
    public void viewStartDateForKtpCompetition() throws Exception {
        Long competitionId = 1L;
        ZonedDateTime competitionEndDate = ZonedDateTime.now();
        LocalDate targetStartDate = competitionEndDate.plusMonths(12).toLocalDate();

        ApplicationResource applicationResource = newApplicationResource().build();

        CompetitionResource competition = newCompetitionResource()
                .withId(competitionId)
                .withName("Comp 1")
                .withFundingType(FundingType.KTP)
                .withEndDate(competitionEndDate)
                .withLocationPerPartner(true)
                .build();

        ProjectResource project = newProjectResource().
                withCompetition(competition.getId()).
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
        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competition));

        MvcResult result = mockMvc.perform(get("/competition/{competitionId}/project/{projectId}/details/start-date", project.getCompetition(), project.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("project/details-start-date"))
                .andReturn();

        Map<String, Object> model = result.getModelAndView().getModel();
        ProjectDetailsStartDateViewModel viewModel = (ProjectDetailsStartDateViewModel) model.get("model");

        assertEquals(project.getId(), viewModel.getProjectId());
        assertEquals(project.getApplication(), (long) viewModel.getApplicationId());
        assertEquals(project.getName(), viewModel.getProjectName());
        assertEquals(project.getDurationInMonths(), Long.valueOf(viewModel.getProjectDurationInMonths()));
        assertTrue(viewModel.isKtpCompetition());

        ProjectDetailsStartDateForm form = (ProjectDetailsStartDateForm) model.get(FORM_ATTR_NAME);
        assertEquals(targetStartDate, form.getProjectStartDate());
    }

    @Test
    public void updateStartDate() throws Exception {
        Long competitionId = 1L;

        ApplicationResource applicationResource = newApplicationResource().build();

        CompetitionResource competitionResource = newCompetitionResource()
                .withId(competitionId)
                .withFundingType(FundingType.GRANT)
                .build();

        ProjectResource project = newProjectResource()
                .withApplication(applicationResource)
                .withCompetition(competitionResource.getId()).build();

        when(projectService.getById(project.getId())).thenReturn(project);
        when(projectDetailsService.updateProjectStartDate(project.getId(), LocalDate.of(2017, 6, 3))).thenReturn(serviceSuccess());
        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competitionResource));

        mockMvc.perform(post("/competition/{competitionId}/project/{projectId}/details/start-date", project.getCompetition(), project.getId()).
                contentType(MediaType.APPLICATION_FORM_URLENCODED).
                param("projectStartDate", "projectStartDate").
                param("projectStartDate.dayOfMonth", "3").
                param("projectStartDate.monthValue", "6").
                param("projectStartDate.year", "2017"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/competition/" + project.getCompetition() + "/project/" + project.getId() + "/details"))
                .andReturn();
    }

    @Test
    public void viewEditProjectDuration() throws Exception {

        long competitionId = 1L;
        String competitionName = "Comp 1";
        long projectId = 11L;

        CompetitionResource competition = newCompetitionResource()
                .withId(competitionId)
                .withFundingType(FundingType.GRANT)
                .withName(competitionName)
                .build();

        ProjectResource project = newProjectResource()
                .withId(projectId)
                .withName("Project 1")
                .withTargetStartDate(LocalDate.of(2018, 3, 1))
                .withDuration(36L)
                .withCompetition(competition.getId())
                .build();

        when(projectService.getById(projectId)).thenReturn(project);
        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competition));

        MvcResult result = mockMvc.perform(get("/competition/" + competitionId + "/project/" + projectId + "/duration"))
                .andExpect(status().isOk())
                .andExpect(view().name("project/edit-duration"))
                .andReturn();

        ProjectDetailsViewModel viewModel = (ProjectDetailsViewModel) result.getModelAndView().getModel().get("model");

        // Assert that the model has the correct values
        assertEquals(project, viewModel.getProject());
        assertEquals(project.getCompetition(), (long) viewModel.getCompetitionId());
        assertEquals(project.getCompetitionName(), viewModel.getCompetitionName());
        assertNull(viewModel.getLeadOrganisation());
        assertFalse(viewModel.isLocationPerPartnerRequired());
        assertFalse(viewModel.isKtpCompetition());

        ProjectDurationForm form = (ProjectDurationForm) result.getModelAndView().getModel().get("form");
        assertEquals(new ProjectDurationForm(), form);

        verify(projectService).getById(projectId);
        verify(competitionRestService).getCompetitionById(competitionId);
    }

    @Test
    public void viewEditProjectDurationForKtpCompetition() throws Exception {

        long competitionId = 1L;
        String competitionName = "Comp 1";
        long projectId = 11L;

        CompetitionResource competition = newCompetitionResource()
                .withId(competitionId)
                .withFundingType(FundingType.KTP)
                .withName(competitionName)
                .build();

        ProjectResource project = newProjectResource()
                .withId(projectId)
                .withName("Project 1")
                .withTargetStartDate(LocalDate.of(2018, 3, 1))
                .withDuration(36L)
                .withCompetition(competition.getId())
                .build();

        when(projectService.getById(projectId)).thenReturn(project);
        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competition));

        MvcResult result = mockMvc.perform(get("/competition/" + competitionId + "/project/" + projectId + "/duration"))
                .andExpect(status().isOk())
                .andExpect(view().name("project/edit-duration"))
                .andReturn();

        ProjectDetailsViewModel viewModel = (ProjectDetailsViewModel) result.getModelAndView().getModel().get("model");

        // Assert that the model has the correct values
        assertEquals(project, viewModel.getProject());
        assertEquals(project.getCompetition(), (long) viewModel.getCompetitionId());
        assertEquals(project.getCompetitionName(), viewModel.getCompetitionName());
        assertNull(viewModel.getLeadOrganisation());
        assertFalse(viewModel.isLocationPerPartnerRequired());
        assertTrue(viewModel.isKtpCompetition());

        ProjectDurationForm form = (ProjectDurationForm) result.getModelAndView().getModel().get("form");
        assertEquals(new ProjectDurationForm(), form);

        verify(projectService).getById(projectId);
        verify(competitionRestService).getCompetitionById(competitionId);
    }

    @Test
    public void updateProjectDurationWhenDurationIsInvalid() throws Exception {

        long competitionId = 1L;
        long projectId = 11L;
        String durationInMonths = null;

        CompetitionResource competition = newCompetitionResource()
                .withId(competitionId)
                .withFundingType(FundingType.GRANT).build();
        ProjectResource project = newProjectResource()
                .withCompetition(competition.getId()).build();

        when(projectService.getById(projectId)).thenReturn(project);
        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competition));

        performUpdateProjectDurationFailurePost(competitionId, projectId, durationInMonths);

        durationInMonths = "";
        performUpdateProjectDurationFailurePost(competitionId, projectId, durationInMonths);

        durationInMonths = "  ";
        performUpdateProjectDurationFailurePost(competitionId, projectId, durationInMonths);

        durationInMonths = "dddd";
        performUpdateProjectDurationFailurePost(competitionId, projectId, durationInMonths);

        durationInMonths = "0";
        performUpdateProjectDurationFailurePost(competitionId, projectId, durationInMonths);
    }

    private void performUpdateProjectDurationFailurePost(long competitionId, long projectId, String durationInMonths) throws Exception {

        mockMvc.perform(post("/competition/" + competitionId + "/project/" + projectId + "/duration")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("durationInMonths", durationInMonths))
                .andExpect(status().isOk())
                .andExpect(view().name("project/edit-duration")) // failure view
                .andReturn();

        verify(projectDetailsService, never()).updateProjectDuration(anyLong(), anyLong());
    }

    @Test
    public void updateProjectDurationFailure() throws Exception {

        long competitionId = 1L;
        long projectId = 11L;
        String durationInMonths = "18";

        when(projectDetailsService.updateProjectDuration(projectId, 18L))
                .thenReturn(serviceFailure(PROJECT_SETUP_PROJECT_DURATION_CANNOT_BE_CHANGED_ONCE_SPEND_PROFILE_HAS_BEEN_GENERATED));

        CompetitionResource competition = newCompetitionResource()
                .withId(competitionId)
                .withFundingType(FundingType.GRANT).build();
        ProjectResource project = newProjectResource()
                .withCompetition(competition.getId()).build();

        when(projectService.getById(projectId)).thenReturn(project);
        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competition));

        mockMvc.perform(post("/competition/" + competitionId + "/project/" + projectId + "/duration")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("durationInMonths", durationInMonths))
                .andExpect(status().isOk())
                .andExpect(view().name("project/edit-duration"))
                .andReturn();

        verify(projectDetailsService).updateProjectDuration(projectId, 18L);
        verify(projectService).getById(projectId);
        verify(competitionRestService).getCompetitionById(competitionId);
    }

    @Test
    public void updateProjectDurationSuccess() throws Exception {

        long competitionId = 1L;
        long projectId = 11L;
        String durationInMonths = "18";

        when(projectDetailsService.updateProjectDuration(projectId, 18L)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/competition/" + competitionId + "/project/" + projectId + "/duration")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("durationInMonths", durationInMonths))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/project/" + projectId + "/finance-check"))
                .andReturn();

        verify(projectDetailsService).updateProjectDuration(projectId, 18L);
    }

    private List<ProjectUserResource> buildProjectUsers(OrganisationResource leadOrganisation, OrganisationResource partnerOrganisation) {

        ProjectUserResource leadPartnerProjectUser = newProjectUserResource().
                withUser(loggedInUser.getId()).
                withOrganisation(leadOrganisation.getId()).
                withRole(PARTNER).
                build();

        ProjectUserResource partnerProjectUser = newProjectUserResource().
                withUser(2L).
                withOrganisation(partnerOrganisation.getId()).
                withRole(PARTNER).
                build();

        List<ProjectUserResource> projectUsers = new ArrayList<>();
        projectUsers.add(leadPartnerProjectUser);
        projectUsers.add(partnerProjectUser);

        return projectUsers;
    }

    private Map<OrganisationResource, ProjectUserResource>
        buildExpectedOrganisationFinanceContactMap(OrganisationResource leadOrganisation,
                                               OrganisationResource partnerOrganisation,
                                               ProjectUserResource leadFinanceContactProjectUser,
                                               ProjectUserResource partnerFinanceContactProjectUser) {

        Map<OrganisationResource, ProjectUserResource> expectedOrganisationFinanceContactMap = new LinkedHashMap<>();

        expectedOrganisationFinanceContactMap.put(leadOrganisation, leadFinanceContactProjectUser);
        expectedOrganisationFinanceContactMap.put(partnerOrganisation, partnerFinanceContactProjectUser);

        return expectedOrganisationFinanceContactMap;
    }

    @Override
    protected ProjectDetailsController supplyControllerUnderTest() {
        return new ProjectDetailsController();
    }
}

