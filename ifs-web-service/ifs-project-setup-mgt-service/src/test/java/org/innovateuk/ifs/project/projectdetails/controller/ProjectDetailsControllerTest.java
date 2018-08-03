package org.innovateuk.ifs.project.projectdetails.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.service.ApplicationRestService;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.builder.PartnerOrganisationResourceBuilder;
import org.innovateuk.ifs.project.projectdetails.form.ProjectDurationForm;
import org.innovateuk.ifs.project.projectdetails.viewmodel.ProjectDetailsViewModel;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.service.PartnerOrganisationRestService;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.innovateuk.ifs.projectdetails.ProjectDetailsService;
import org.innovateuk.ifs.user.service.OrganisationService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_PROJECT_DURATION_CANNOT_BE_CHANGED_ONCE_SPEND_PROFILE_HAS_BEEN_GENERATED;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ProjectDetailsControllerTest extends BaseControllerMockMVCTest<ProjectDetailsController> {

    @Mock
    private ProjectService projectService;

    @Mock
    private OrganisationService organisationService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private PartnerOrganisationRestService partnerOrganisationRestService;

    @Mock
    private ProjectDetailsService projectDetailsService;

    @Mock
    private ProjectRestService projectRestService;

    @Mock
    private ApplicationRestService applicationRestService;

    @Test
    public void viewProjectDetails() throws Exception {
        Long competitionId = 1L;
        Long projectId = 1L;
        setLoggedInUser(newUserResource().withRolesGlobal(singletonList(IFS_ADMINISTRATOR)).build());

        CompetitionResource competition = CompetitionResourceBuilder.newCompetitionResource()
                .withId(competitionId)
                .withName("Comp 1")
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
        when(organisationService.getOrganisationById(leadOrganisation.getId())).thenReturn(leadOrganisation);
        when(organisationService.getOrganisationById(partnerOrganisation.getId())).thenReturn(partnerOrganisation);
        when(projectService.getLeadOrganisation(project.getId())).thenReturn(leadOrganisation);
        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competition));
        when(partnerOrganisationRestService.getProjectPartnerOrganisations(projectId)).thenReturn(RestResult.restSuccess(partnerOrganisations));

        MvcResult result = mockMvc.perform(get("/competition/" + competitionId + "/project/" + projectId + "/details"))
                .andExpect(view().name("project/detail"))
                .andReturn();

        ProjectDetailsViewModel model = (ProjectDetailsViewModel) result.getModelAndView().getModel().get("model");

        Map<OrganisationResource, ProjectUserResource> expectedOrganisationFinanceContactMap =
                buildExpectedOrganisationFinanceContactMap(leadOrganisation, partnerOrganisation,
                        leadFinanceContactProjectUser, partnerFinanceContactProjectUser);

        // Assert that the model has the correct values
        assertEquals(project, model.getProject());
        assertEquals(competitionId, model.getCompetitionId());
        assertEquals("Comp 1", model.getCompetitionName());
        assertTrue(model.isIfsAdministrator());
        assertEquals("Lead Org 1", model.getLeadOrganisation());
        assertEquals(projectManagerProjectUser, model.getProjectManager());
        assertEquals(expectedOrganisationFinanceContactMap, model.getOrganisationFinanceContactMap());
        assertEquals(true, model.isLocationPerPartnerRequired());
        assertEquals("TW14 9QG", model.getPostcodeForPartnerOrganisation(1L));
        assertEquals("UB7 8QF", model.getPostcodeForPartnerOrganisation(2L));

    }

    @Test
    public void viewEditProjectDuration() throws Exception {

        long competitionId = 1L;
        String competitionName = "Comp 1";
        long projectId = 11L;

        ProjectResource project = newProjectResource()
                .withId(projectId)
                .withName("Project 1")
                .withTargetStartDate(LocalDate.of(2018, 3, 1))
                .withDuration(36L)
                .build();

        CompetitionResource competition = CompetitionResourceBuilder.newCompetitionResource()
                .withId(competitionId)
                .withName(competitionName)
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
        assertEquals(competitionId, (long) viewModel.getCompetitionId());
        assertEquals(competitionName, viewModel.getCompetitionName());
        assertNull(viewModel.getLeadOrganisation());
        assertNull(viewModel.getProjectManager());
        assertNull(viewModel.getOrganisationFinanceContactMap());
        assertFalse(viewModel.isLocationPerPartnerRequired());

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

        ProjectResource project = newProjectResource().build();

        CompetitionResource competition = CompetitionResourceBuilder.newCompetitionResource().build();

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

        ProjectResource project = newProjectResource().build();

        CompetitionResource competition = CompetitionResourceBuilder.newCompetitionResource().build();

        when(projectService.getById(projectId)).thenReturn(project);
        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competition));

        mockMvc.perform(post("/competition/" + competitionId + "/project/" + projectId + "/duration")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("durationInMonths", durationInMonths))
                .andExpect(status().isOk())
                .andExpect(view().name("project/edit-duration"))
                .andReturn();

        verify(projectDetailsService).updateProjectDuration(projectId, 18L);
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


    @Test
    public void withdrawProject() throws Exception {
        long competitionId = 1L;
        long applicationId = 3L;
        ProjectResource project = newProjectResource()
                .withApplication(applicationId)
                .build();

        setLoggedInUser(newUserResource()
                                .withRolesGlobal(singletonList(IFS_ADMINISTRATOR))
                                .build());

        when(projectRestService.withdrawProject(project.getId())).thenReturn(restSuccess());
        when(projectRestService.getProjectById(project.getId())).thenReturn(restSuccess(project));
        when(applicationRestService.withdrawApplication(applicationId)).thenReturn(restSuccess());

        mockMvc.perform(post("/competition/" + competitionId + "/project/" + project.getId() + "/withdraw"))
                .andExpect(redirectedUrlPattern("**/management/competition/" + competitionId + "/applications/previous"))
                .andExpect(status().is3xxRedirection())
                .andReturn();

        verify(projectRestService).withdrawProject(project.getId());
        verify(projectRestService).getProjectById(project.getId());
        verify(applicationRestService).withdrawApplication(applicationId);
    }

    private  List<ProjectUserResource> buildProjectUsers(OrganisationResource leadOrganisation, OrganisationResource partnerOrganisation) {

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

