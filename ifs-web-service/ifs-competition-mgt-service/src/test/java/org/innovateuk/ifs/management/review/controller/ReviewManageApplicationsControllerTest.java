package org.innovateuk.ifs.management.review.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationSummaryResource;
import org.innovateuk.ifs.application.service.ApplicationSummaryRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.navigation.Pagination;
import org.innovateuk.ifs.management.review.model.ManageReviewApplicationsModelPopulator;
import org.innovateuk.ifs.management.review.viewmodel.ManagePanelApplicationsViewModel;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
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

public class ReviewManageApplicationsControllerTest extends BaseControllerMockMVCTest<ReviewManageApplicationsController> {

    @InjectMocks
    @Spy
    private ManageReviewApplicationsModelPopulator managePanelApplicationsPopulator;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private ApplicationSummaryRestService applicationSummaryRestService;

    @Override
    protected ReviewManageApplicationsController supplyControllerUnderTest() {
        return new ReviewManageApplicationsController();
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

        List<ApplicationSummaryResource> inPanelSummaryResources = newApplicationSummaryResource()
                .withId(3L, 4L)
                .withName("three", "four")
                .withLead("Lead 3", "Lead 4")
                .withInnovationArea("Digital manufacturing")
                .build(2);

        ApplicationSummaryPageResource expectedPageResource = new ApplicationSummaryPageResource(41, 3, summaryResources, 1, 20);
        ApplicationSummaryPageResource expectedInPanelPageResource = new ApplicationSummaryPageResource(12, 1, inPanelSummaryResources, 1, 2);


        when(competitionRestService.getCompetitionById(competitionResource.getId()))
                .thenReturn(restSuccess(competitionResource));
        when(applicationSummaryRestService.getSubmittedApplicationsWithPanelStatus(competitionResource.getId(), "",1,20, Optional.of("filter"), Optional.empty(), Optional.of(false)))
                .thenReturn(restSuccess(expectedPageResource));
        when(applicationSummaryRestService.getSubmittedApplicationsWithPanelStatus(competitionResource.getId(), null, 0, Integer.MAX_VALUE, Optional.empty(), Optional.empty(), Optional.of(true)))
                .thenReturn(restSuccess(expectedInPanelPageResource));


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

        assertEquals(2, model.getAssignedApplications().size());
        assertEquals("Lead 3", model.getAssignedApplications().get(0).getLeadOrganisation());
        assertEquals("Lead 4", model.getAssignedApplications().get(1).getLeadOrganisation());
        assertEquals("three", model.getAssignedApplications().get(0).getTitle());
        assertEquals("four", model.getAssignedApplications().get(1).getTitle());

        Pagination actualPagination = model.getPagination();
        assertEquals(1, actualPagination.getCurrentPage());
        assertEquals(20,actualPagination.getPageSize());
        assertEquals(3, actualPagination.getTotalPages());
        assertEquals("1 to 20", actualPagination.getPageNames().get(0).getTitle());
        assertEquals("21 to 40", actualPagination.getPageNames().get(1).getTitle());
        assertEquals("41 to 41", actualPagination.getPageNames().get(2).getTitle());
        assertEquals("?filterSearch=filter&page=2", actualPagination.getPageNames().get(2).getPath());
    }
}
