package org.innovateuk.ifs.project.pendingpartner.controller;

import static java.lang.String.format;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.project.builder.ProjectResourceBuilder.newProjectResource;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlTemplate;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.service.ProjectRestService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ProjectYourOrganisationControllerTest extends BaseControllerMockMVCTest<ProjectYourOrganisationController> {

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private ProjectRestService projectRestService;

    private CompetitionResource competition;
    private ProjectResource project;
    private String baseUrl;
    private static final long projectId = 3L;
    private static final long organisationId = 5L;
    private static final long competitionId = 7L;

    @Override
    protected ProjectYourOrganisationController supplyControllerUnderTest() {
        return new ProjectYourOrganisationController();
    }

    @Before
    public void setup() {
        baseUrl = format("/project/%d/organisation/%d/your-organisation", projectId, organisationId);
        competition = newCompetitionResource()
            .withId(competitionId)
            .build();
        project = newProjectResource()
            .withId(projectId)
            .withCompetition(competition.getId())
            .build();
    }

    @Test
    public void viewPage_redirectToWithGrowthTable() throws Exception {
        when(projectRestService.getProjectById(projectId)).thenReturn(restSuccess(project));
        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competition));
        competition.setIncludeProjectGrowthTable(true);

        mockMvc.perform(get(baseUrl))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrlTemplate(baseUrl +"/with-growth-table"));
    }

    @Test
    public void viewPage_redirectToWithoutGrowthTable() throws Exception {
        when(projectRestService.getProjectById(projectId)).thenReturn(restSuccess(project));
        when(competitionRestService.getCompetitionById(competitionId)).thenReturn(restSuccess(competition));
        competition.setIncludeProjectGrowthTable(false);

        mockMvc.perform(get(baseUrl))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrlTemplate(baseUrl +"/without-growth-table"));
    }
}
