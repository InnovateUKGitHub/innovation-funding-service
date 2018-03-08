package org.innovateuk.ifs.project.projectdetails.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.projectdetails.viewmodel.ProjectDetailsViewModel;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.PROJECT_SETUP_PROJECT_DURATION_CANNOT_BE_CHANGED_ONCE_SPEND_PROFILE_HAS_BEEN_GENERATED;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.FINANCE_CONTACT;
import static org.innovateuk.ifs.user.resource.UserRoleType.PARTNER;
import static org.innovateuk.ifs.user.resource.UserRoleType.PROJECT_MANAGER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class ProjectDetailsControllerTest extends BaseControllerMockMVCTest<ProjectDetailsController> {

    @Test
    public void viewProjectDetails() throws Exception {
        Long competitionId = 1L;
        Long projectId = 1L;

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
                withRoleName(PROJECT_MANAGER.getName()).
                build();

        ProjectUserResource leadFinanceContactProjectUser = newProjectUserResource().
                withUser(loggedInUser.getId()).
                withOrganisation(leadOrganisation.getId()).
                withRoleName(FINANCE_CONTACT.getName()).
                build();

        ProjectUserResource partnerFinanceContactProjectUser = newProjectUserResource().
                withUser(2L).
                withOrganisation(partnerOrganisation.getId()).
                withRoleName(FINANCE_CONTACT.getName()).
                build();

        projectUsers.add(projectManagerProjectUser);
        projectUsers.add(leadFinanceContactProjectUser);
        projectUsers.add(partnerFinanceContactProjectUser);


        when(projectService.getById(project.getId())).thenReturn(project);
        when(projectService.getProjectUsersForProject(project.getId())).thenReturn(projectUsers);
        when(organisationService.getOrganisationById(leadOrganisation.getId())).thenReturn(leadOrganisation);
        when(organisationService.getOrganisationById(partnerOrganisation.getId())).thenReturn(partnerOrganisation);
        when(projectService.getLeadOrganisation(project.getId())).thenReturn(leadOrganisation);

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
        assertNull(model.getCompetitionName());
        assertEquals("Lead Org 1", model.getLeadOrganisation());
        assertEquals(projectManagerProjectUser, model.getProjectManager());
        assertEquals(expectedOrganisationFinanceContactMap, model.getOrganisationFinanceContactMap());

    }

    @Test
    public void editProjectDuration() throws Exception {

        Long competitionId = 1L;
        String competitionName = "Comp 1";
        Long projectId = 11L;

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
        when(competitionService.getById(competitionId)).thenReturn(competition);

        MvcResult result = mockMvc.perform(get("/competition/" + competitionId + "/project/" + projectId + "/edit-duration"))
                .andExpect(status().isOk())
                .andExpect(view().name("project/edit-duration"))
                .andReturn();

        ProjectDetailsViewModel viewModel = (ProjectDetailsViewModel) result.getModelAndView().getModel().get("model");

        // Assert that the model has the correct values
        assertEquals(project, viewModel.getProject());
        assertEquals(competitionId, viewModel.getCompetitionId());
        assertEquals(competitionName, viewModel.getCompetitionName());
        assertNull(viewModel.getLeadOrganisation());
        assertNull(viewModel.getProjectManager());
        assertNull(viewModel.getOrganisationFinanceContactMap());
    }

    @Test
    public void updateProjectDurationFailure() throws Exception {

        Long competitionId = 1L;
        Long projectId = 11L;
        Long durationInMonths = 18L;

        when(projectDetailsService.updateProjectDuration(projectId, durationInMonths))
                .thenReturn(serviceFailure(PROJECT_SETUP_PROJECT_DURATION_CANNOT_BE_CHANGED_ONCE_SPEND_PROFILE_HAS_BEEN_GENERATED));

        mockMvc.perform(post("/competition/" + competitionId + "/project/" + projectId + "/update-duration/" + durationInMonths))
                .andExpect(status().isOk())
                .andExpect(view().name("project/edit-duration"))
                .andReturn();
    }

    @Test
    public void updateProjectDurationSuccess() throws Exception {

        Long competitionId = 1L;
        Long projectId = 11L;
        Long durationInMonths = 18L;

        when(projectDetailsService.updateProjectDuration(projectId, durationInMonths)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/competition/" + competitionId + "/project/" + projectId + "/update-duration/" + durationInMonths))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/project/" + projectId + "/finance-check"))
                .andReturn();
    }

    private  List<ProjectUserResource> buildProjectUsers(OrganisationResource leadOrganisation, OrganisationResource partnerOrganisation) {

        ProjectUserResource leadPartnerProjectUser = newProjectUserResource().
                withUser(loggedInUser.getId()).
                withOrganisation(leadOrganisation.getId()).
                withRoleName(PARTNER.getName()).
                build();

        ProjectUserResource partnerProjectUser = newProjectUserResource().
                withUser(2L).
                withOrganisation(partnerOrganisation.getId()).
                withRoleName(PARTNER.getName()).
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

