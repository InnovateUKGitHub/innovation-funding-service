package org.innovateuk.ifs.project.projectteam.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.core.ProjectParticipantRole;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.projectdetails.ProjectDetailsService;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.innovateuk.ifs.project.builder.ProjectUserResourceBuilder.newProjectUserResource;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class FinanceContactControllerTest extends BaseControllerMockMVCTest<FinanceContactController> {

    @Override
    protected FinanceContactController supplyControllerUnderTest() {
        return new FinanceContactController(projectService, projectDetailsService);
    }

    @Mock
    private ProjectService projectService;

    @Mock
    private ProjectDetailsService projectDetailsService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Test
    public void viewFinanceContactPage() throws Exception {

        long organisationId = 123L;
        ProjectResource projectResource = newProjectResource().withName("Project Name").build();
        List<ProjectUserResource> projectUsers = newProjectUserResource()
                .withUser(111L, 222L)
                .withOrganisation(organisationId, organisationId)
                .withRole(ProjectParticipantRole.PROJECT_MANAGER, ProjectParticipantRole.PROJECT_PARTNER)
                .build(2);
        CompetitionResource competitionResource = newCompetitionResource().build();
        setLoggedInUser(newUserResource().withId(222L).build());

        when(projectService.getProjectUsersForProject(projectResource.getId())).thenReturn(projectUsers);
        when(projectService.getProjectUsersWithPartnerRole(projectResource.getId())).thenReturn(singletonList(projectUsers.get(1)));
        when(projectService.getById(projectResource.getId())).thenReturn(projectResource);
        when(competitionRestService.getCompetitionById(projectResource.getCompetition())).thenReturn(restSuccess(competitionResource));

        mockMvc.perform(get("/project/{projectId}/team/finance-contact/organisation/{orgId}", projectResource.getId(), organisationId))
                .andExpect(status().isOk())
                .andExpect(view().name("project/team/finance-contact"))
                .andReturn();

        verify(projectService).getProjectUsersForProject(projectResource.getId());
        verify(projectService).getProjectUsersWithPartnerRole(projectResource.getId());
        verify(projectService).getById(projectResource.getId());
    }

    @Test
    public void updateFinanceContact() throws Exception {

        long projectId = 123L;
        long financeContactUserId = 456L;
        long organisationId = 789L;
        ProjectOrganisationCompositeId composite = new ProjectOrganisationCompositeId(projectId, organisationId);

        when(projectDetailsService.updateFinanceContact(composite, financeContactUserId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/project/{projectId}/team/finance-contact/organisation/{organisationId}", projectId, organisationId)
                .param("financeContact", String.valueOf(financeContactUserId)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/project/" + projectId + "/team"));

        verify(projectDetailsService).updateFinanceContact(composite, financeContactUserId);
    }
}
