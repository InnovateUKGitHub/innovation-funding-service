package com.worth.ifs.project.controller;

import com.worth.ifs.BaseControllerMockMVCTest;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.ProjectUserResource;
import com.worth.ifs.project.viewmodel.ProjectDetailsViewModel;
import com.worth.ifs.user.resource.OrganisationResource;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.worth.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static com.worth.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static com.worth.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static com.worth.ifs.user.resource.UserRoleType.FINANCE_CONTACT;
import static com.worth.ifs.user.resource.UserRoleType.PARTNER;
import static com.worth.ifs.user.resource.UserRoleType.PROJECT_MANAGER;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
        assertEquals("Lead Org 1", model.getLeadOrganisation());
        assertEquals(projectManagerProjectUser, model.getProjectManager());
        assertEquals(expectedOrganisationFinanceContactMap, model.getOrganisationFinanceContactMap());

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

