package org.innovateuk.ifs.project.pendingpartner.controller;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlTemplate;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.junit.Test;
import org.mockito.Mock;

public class ProjectYourOrganisationControllerTest extends BaseControllerMockMVCTest<ProjectYourOrganisationController> {

    @Mock
    private ProjectResource project;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private ProjectRestService projectRestService;

    @Mock
    private CompetitionResource competition;

    @Override
    protected ProjectYourOrganisationController supplyControllerUnderTest() {
        return new ProjectYourOrganisationController();
    }

    @Test
    public void viewPage() throws Exception {
        final long projectId = 3L;
        final long organisationId = 5L;
        final String urlSuffix = "with-growth-table";

        when(project.getId()).thenReturn(projectId);
        when(project.getCompetition()).thenReturn(1L);
        when(projectRestService.getProjectById(project.getId())).thenReturn(restSuccess(project));
        when(competitionRestService.getCompetitionById(1L)).thenReturn(restSuccess(competition));
        when(competition.getIncludeProjectGrowthTable()).thenReturn(true);

        mockMvc.perform(get("/project/{projectId}/organisation/{organisationId}/your-organisation", projectId, organisationId))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrlTemplate(format("/project/%d/organisation/%d/your-organisation/%s",
                projectId, organisationId, urlSuffix)));
    }
}
