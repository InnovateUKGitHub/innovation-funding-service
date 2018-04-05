package org.innovateuk.ifs.project.projectdetails.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.project.builder.PartnerOrganisationResourceBuilder;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.projectdetails.viewmodel.ProjectDetailsViewModel;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.junit.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.innovateuk.ifs.user.builder.OrganisationResourceBuilder.newOrganisationResource;
import static org.innovateuk.ifs.user.resource.Role.FINANCE_CONTACT;
import static org.innovateuk.ifs.user.resource.Role.PARTNER;
import static org.innovateuk.ifs.user.resource.Role.PROJECT_MANAGER;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class ProjectDetailsControllerTest extends BaseControllerMockMVCTest<ProjectDetailsController> {

    @Test
    public void viewProjectDetails() throws Exception {
        Long competitionId = 1L;
        Long projectId = 1L;

        CompetitionResource competition = CompetitionResourceBuilder.newCompetitionResource()
                .withId(competitionId)
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
                .withPostCode("TW14 9QG", "UB7 8QF")
                .build(2);

        when(projectService.getById(project.getId())).thenReturn(project);
        when(projectService.getProjectUsersForProject(project.getId())).thenReturn(projectUsers);
        when(organisationService.getOrganisationById(leadOrganisation.getId())).thenReturn(leadOrganisation);
        when(organisationService.getOrganisationById(partnerOrganisation.getId())).thenReturn(partnerOrganisation);
        when(projectService.getLeadOrganisation(project.getId())).thenReturn(leadOrganisation);
        when(competitionService.getById(competitionId)).thenReturn(competition);
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
        assertEquals("Lead Org 1", model.getLeadOrganisation());
        assertEquals(projectManagerProjectUser, model.getProjectManager());
        assertEquals(expectedOrganisationFinanceContactMap, model.getOrganisationFinanceContactMap());
        assertEquals(true, model.isLocationPerPartnerRequired());
        assertEquals("UB7 8QF", model.getPostCodeForPartnerOrganisation(2L));

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

