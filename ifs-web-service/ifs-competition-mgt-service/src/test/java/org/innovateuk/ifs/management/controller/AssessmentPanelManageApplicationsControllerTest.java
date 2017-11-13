package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationSummaryResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.model.ManagePanelApplicationsModelPopulator;
import org.innovateuk.ifs.management.viewmodel.ManagePanelApplicationsViewModel;
import org.innovateuk.ifs.management.viewmodel.PaginationViewModel;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Spy;

import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.application.builder.ApplicationSummaryResourceBuilder.newApplicationSummaryResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.IN_ASSESSMENT;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AssessmentPanelManageApplicationsControllerTest extends BaseControllerMockMVCTest<AssessmentPanelManageApplicationsController> {

    @InjectMocks
    @Spy
    private ManagePanelApplicationsModelPopulator managePanelApplicationsPopulator;

    @Override
    protected AssessmentPanelManageApplicationsController supplyControllerUnderTest() {
        return new AssessmentPanelManageApplicationsController();
    }

    @Test
    public void manageApplications() throws Exception {
        CompetitionResource competitionResource = newCompetitionResource()
                .withId(1L)
                .withName("name")
                .withCompetitionStatus(IN_ASSESSMENT)
                .build();

        List<ApplicationSummaryResource> summaryResources = newApplicationSummaryResource()
                .withId(1L, 2L)
                .withName("one", "two")
                .withLead("Lead 1", "Lead 2")
                .withInnovationArea("Digital manufacturing")
                .build(2);

        ApplicationSummaryPageResource expectedPageResource = new ApplicationSummaryPageResource(41, 3, summaryResources, 1, 20);

        when(competitionRestService.getCompetitionById(competitionResource.getId()))
                .thenReturn(restSuccess(competitionResource));
        when(applicationSummaryRestService.getSubmittedApplications(competitionResource.getId(), "",1,20, Optional.of("filter"), Optional.empty()))
                .thenReturn(restSuccess(expectedPageResource));

        ManagePanelApplicationsViewModel model = (ManagePanelApplicationsViewModel) mockMvc
                .perform(get("/assessment/panel/competition/{competitionId}/manage-applications?page=1&filterSearch=filter", competitionResource.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/manage-applications-panel"))
                .andExpect(model().attributeExists("model"))
                .andReturn().getModelAndView().getModel().get("model");

        assertEquals((long) competitionResource.getId(), model.getCompetitionId());
        assertEquals(competitionResource.getName(), model.getCompetitionName());
        assertEquals(competitionResource.getCompetitionStatus().getDisplayName(), model.getCompetitionStatus());
        assertEquals(2, model.getApplications().size());
        assertEquals("Lead 1", model.getApplications().get(0).getLeadOrganisation());
        assertEquals("Lead 2", model.getApplications().get(1).getLeadOrganisation());

        PaginationViewModel actualPagination = model.getPagination();
        assertEquals(1, actualPagination.getCurrentPage());
        assertEquals(20,actualPagination.getPageSize());
        assertEquals(3, actualPagination.getTotalPages());
        assertEquals("1 to 20", actualPagination.getPageNames().get(0).getTitle());
        assertEquals("21 to 40", actualPagination.getPageNames().get(1).getTitle());
        assertEquals("41 to 41", actualPagination.getPageNames().get(2).getTitle());
        assertEquals("?origin=MANAGE_APPLICATIONS_PANEL&filterSearch=filter&page=2", actualPagination.getPageNames().get(2).getPath());
    }
}
