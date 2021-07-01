package org.innovateuk.ifs.management.assessment.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryPageResource;
import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource;
import org.innovateuk.ifs.application.service.ApplicationCountSummaryRestService;
import org.innovateuk.ifs.assessment.service.AssessmentPeriodService;
import org.innovateuk.ifs.competition.builder.AssessmentPeriodResourceBuilder;
import org.innovateuk.ifs.competition.resource.AssessmentPeriodResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.service.CompetitionRestService;
import org.innovateuk.ifs.management.application.list.populator.ManageApplicationsModelPopulator;
import org.innovateuk.ifs.management.application.list.viewmodel.ManageApplicationsViewModel;
import org.innovateuk.ifs.management.navigation.Pagination;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import java.util.List;

import static org.innovateuk.ifs.application.builder.ApplicationCountSummaryResourceBuilder.newApplicationCountSummaryResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.AssessmentPeriodResourceBuilder.newAssessmentPeriodResource;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.IN_ASSESSMENT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class AssessmentApplicationsControllerTest extends BaseControllerMockMVCTest<AssessmentApplicationsController> {

    @Mock
    private ApplicationCountSummaryRestService applicationCountSummaryRestService;

    @Mock
    private CompetitionRestService competitionRestService;

    @Mock
    private AssessmentPeriodService assessmentPeriodService;

    @InjectMocks
    @Spy
    private ManageApplicationsModelPopulator manageApplicationsPopulator;

    @Override
    protected AssessmentApplicationsController supplyControllerUnderTest() {
        return new AssessmentApplicationsController();
    }

    @Test
    public void manageApplications() throws Exception {
        AssessmentPeriodResource assessmentPeriodResource = newAssessmentPeriodResource().build();
        CompetitionResource competitionResource = newCompetitionResource()
                .withName("name")
                .withCompetitionStatus(IN_ASSESSMENT)
                .build();

        List<ApplicationCountSummaryResource> summaryResources = newApplicationCountSummaryResource()
                .withName("one", "two")
                .withLeadOrganisation("Lead Org 1", "Lead Org 2")
                .withAccepted(2L, 3L)
                .withAssessors(3L, 4L)
                .withSubmitted(1L, 2L).build(2);

        ApplicationCountSummaryPageResource expectedPageResource = new ApplicationCountSummaryPageResource(41, 3, summaryResources, 1, 20);

        when(competitionRestService.getCompetitionById(competitionResource.getId())).thenReturn(restSuccess(competitionResource));
        when(applicationCountSummaryRestService.getApplicationCountSummariesByCompetitionIdAndAssessmentPeriodId(competitionResource.getId(), assessmentPeriodResource.getId(), 1,20,"filter")).thenReturn(restSuccess(expectedPageResource));

        ManageApplicationsViewModel model = (ManageApplicationsViewModel) mockMvc.perform(get("/assessment/competition/{competitionId}/applications/period?assessmentPeriodId={assessmentPeriodId}&page=1&filterSearch=filter", competitionResource.getId(), assessmentPeriodResource.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/manage-applications"))
                .andExpect(model().attributeExists("model"))
                .andReturn().getModelAndView().getModel().get("model");

        assertEquals(competitionResource.getId(), model.getCompetitionId());
        assertEquals(competitionResource.getName(), model.getCompetitionName());
        assertTrue(model.getInAssessment());
        assertEquals(2, model.getApplications().size());
        assertEquals(2L, model.getApplications().get(0).getAccepted());
        assertEquals(3L, model.getApplications().get(0).getAssessors());
        assertEquals(1L, model.getApplications().get(0).getCompleted());
        assertEquals("Lead Org 1", model.getApplications().get(0).getLeadOrganisation());

        assertEquals(3L, model.getApplications().get(1).getAccepted());
        assertEquals(4L, model.getApplications().get(1).getAssessors());
        assertEquals(2L, model.getApplications().get(1).getCompleted());
        assertEquals("Lead Org 2", model.getApplications().get(1).getLeadOrganisation());

        Pagination actualPagination = model.getPagination();
        assertEquals(1, actualPagination.getCurrentPage());
        assertEquals(20,actualPagination.getPageSize());
        assertEquals(3, actualPagination.getTotalPages());
        assertEquals("1 to 20", actualPagination.getPageNames().get(0).getTitle());
        assertEquals("21 to 40", actualPagination.getPageNames().get(1).getTitle());
        assertEquals("41 to 41", actualPagination.getPageNames().get(2).getTitle());
        assertEquals("?assessmentPeriodId=1&filterSearch=filter&page=2", actualPagination.getPageNames().get(2).getPath());
    }

    @Test
    public void manageApplications_assessorManagementOrigin() throws Exception {
        AssessmentPeriodResource assessmentPeriod = newAssessmentPeriodResource().build();
        CompetitionResource competitionResource = newCompetitionResource()
                .withName("name")
                .withCompetitionStatus(IN_ASSESSMENT)
                .build();

        List<ApplicationCountSummaryResource> summaryResources = newApplicationCountSummaryResource()
                .withName("one", "two")
                .withLeadOrganisation("Lead Org 1", "Lead Org 2")
                .withAccepted(2L, 3L)
                .withAssessors(3L, 4L)
                .withSubmitted(1L, 2L).build(2);

        ApplicationCountSummaryPageResource expectedPageResource = new ApplicationCountSummaryPageResource(41, 3, summaryResources, 1, 20);

        when(competitionRestService.getCompetitionById(competitionResource.getId())).thenReturn(restSuccess(competitionResource));
        when(applicationCountSummaryRestService.getApplicationCountSummariesByCompetitionIdAndAssessmentPeriodId(competitionResource.getId(), assessmentPeriod.getId(), 0,20,"")).thenReturn(restSuccess(expectedPageResource));
        when(assessmentPeriodService.assessmentPeriodName(assessmentPeriod.getId(), competitionResource.getId())).thenReturn("period 1");

        mockMvc.perform(get("/assessment/competition/{competitionId}/applications/period?assessmentPeriodId={assessmentPeriodId}", competitionResource.getId(), assessmentPeriod.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/manage-applications"));
    }
}
