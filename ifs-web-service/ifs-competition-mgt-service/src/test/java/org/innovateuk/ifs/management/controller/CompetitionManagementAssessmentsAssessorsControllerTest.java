package org.innovateuk.ifs.management.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.resource.AssessorCountSummaryPageResource;
import org.innovateuk.ifs.application.resource.AssessorCountSummaryResource;
import org.innovateuk.ifs.application.service.AssessorCountSummaryRestService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.management.model.ManageAssessorsModelPopulator;
import org.innovateuk.ifs.management.viewmodel.ManageAssessorsViewModel;
import org.innovateuk.ifs.management.viewmodel.PaginationViewModel;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

import java.util.List;

import static org.innovateuk.ifs.application.builder.AssessorCountSummaryResourceBuilder.newAssessorCountSummaryResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.IN_ASSESSMENT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class CompetitionManagementAssessmentsAssessorsControllerTest extends BaseControllerMockMVCTest<CompetitionManagementAssessmentsAssessorsController> {

    @Mock
    private AssessorCountSummaryRestService assessorCountSummaryRestService;

    @InjectMocks
    @Spy
    private ManageAssessorsModelPopulator manageAssessorsModelPopulator;

    @Override
    protected CompetitionManagementAssessmentsAssessorsController supplyControllerUnderTest() {
        return new CompetitionManagementAssessmentsAssessorsController();
    }

    @Test
    public void manageAssessors() throws Exception {
        final int pageNumber = 1;
        final int pageSize = 20;

        CompetitionResource competitionResource = newCompetitionResource()
                .withName("name")
                .withCompetitionStatus(IN_ASSESSMENT)
                .build();

        List<AssessorCountSummaryResource> summaryResources = newAssessorCountSummaryResource()
                .withName("one", "two")
                .withSkillAreas("skill1", "skill2")
                .withTotalAssigned(2L, 3L)
                .withAssigned(5L, 7L)
                .withAccepted(11L, 13L)
                .withSubmitted(17L, 19L)
                .build(2);

        AssessorCountSummaryPageResource expectedPageResource = new AssessorCountSummaryPageResource(41, 3, summaryResources, pageNumber, pageSize);

        when(competitionRestService.getCompetitionById(competitionResource.getId())).thenReturn(restSuccess(competitionResource));
        when(assessorCountSummaryRestService.getAssessorCountSummariesByCompetitionId(competitionResource.getId(), pageNumber, pageSize)).thenReturn(restSuccess(expectedPageResource));

        ManageAssessorsViewModel model = (ManageAssessorsViewModel) mockMvc.perform(get("/assessment/competition/{competitionId}/assessors?page=1", competitionResource.getId()))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/manage-assessors"))
                .andExpect(model().attributeExists("model"))
                .andReturn().getModelAndView().getModel().get("model");

        assertEquals(competitionResource.getId().longValue(), model.getCompetitionId());
        assertEquals(competitionResource.getName(), model.getCompetitionName());

        assertTrue(model.isInAssessment());
        assertEquals(2, model.getAssessors().size());
        assertEquals(2L, model.getAssessors().get(0).getTotal());
        assertEquals(5L, model.getAssessors().get(0).getAssigned());
        assertEquals(11L, model.getAssessors().get(0).getAccepted());
        assertEquals(17L, model.getAssessors().get(0).getSubmitted());
        assertEquals("one", model.getAssessors().get(0).getName());
        assertEquals("skill1", model.getAssessors().get(0).getSkillAreas());

        assertEquals(3L, model.getAssessors().get(1).getTotal());
        assertEquals(7L, model.getAssessors().get(1).getAssigned());
        assertEquals(13L, model.getAssessors().get(1).getAccepted());
        assertEquals(19L, model.getAssessors().get(1).getSubmitted());
        assertEquals("two", model.getAssessors().get(1).getName());
        assertEquals("skill2", model.getAssessors().get(1).getSkillAreas());

        PaginationViewModel actualPagination = model.getPagination();
        assertEquals(1, actualPagination.getCurrentPage());
        assertEquals(20,actualPagination.getPageSize());
        assertEquals(3, actualPagination.getTotalPages());
        assertEquals("1 to 20", actualPagination.getPageNames().get(0).getTitle());
        assertEquals("21 to 40", actualPagination.getPageNames().get(1).getTitle());
        assertEquals("41 to 41", actualPagination.getPageNames().get(2).getTitle());
        assertEquals("?origin=MANAGE_ASSESSORS&page=2", actualPagination.getPageNames().get(2).getPath());
    }
}
