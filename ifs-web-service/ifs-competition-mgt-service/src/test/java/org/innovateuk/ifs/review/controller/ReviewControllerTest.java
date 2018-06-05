package org.innovateuk.ifs.review.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.application.service.CompetitionService;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;
import org.innovateuk.ifs.competition.service.CompetitionKeyApplicationStatisticsRestService;
import org.innovateuk.ifs.review.model.ReviewModelPopulator;
import org.innovateuk.ifs.review.resource.ReviewKeyStatisticsResource;
import org.innovateuk.ifs.review.service.ReviewRestService;
import org.innovateuk.ifs.review.viewmodel.ReviewViewModel;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static java.lang.String.format;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.name;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.commons.rest.RestResult.toGetResponse;
import static org.innovateuk.ifs.competition.builder.CompetitionResourceBuilder.newCompetitionResource;
import static org.innovateuk.ifs.competition.resource.CompetitionStatus.CLOSED;
import static org.innovateuk.ifs.review.builder.ReviewKeyStatisticsResourceBuilder.newReviewKeyStatisticsResource;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class ReviewControllerTest extends BaseControllerMockMVCTest<ReviewController> {

    @Mock
    private CompetitionService competitionService;

    @Mock
    private CompetitionKeyApplicationStatisticsRestService competitionKeyApplicationStatisticsRestService;

    @Mock
    private ReviewRestService reviewRestService;

    @Spy
    @InjectMocks
    private ReviewModelPopulator reviewModelPopulator;

    private ReviewKeyStatisticsResource reviewKeyStatisticsResource;
    private CompetitionResource competitionResource;

    @Override
    protected ReviewController supplyControllerUnderTest() {
        return new ReviewController();
    }

    @Test
    public void assessmentPanel() throws Exception {
        long competitionId = 1L;
        String competitionName = "Competition x";
        CompetitionStatus competitionStatus = CLOSED;
        int applicationsInPanel = 5;
        int assessorsAccepted = 2;
        int assessorsPending = 3;
        boolean reviewsPending = true;

        competitionResource = newCompetitionResource()
                .with(id(competitionId))
                .with(name(competitionName))
                .withCompetitionStatus(competitionStatus)
                .build();

        reviewKeyStatisticsResource = newReviewKeyStatisticsResource()
                .withApplicationsInPanel(applicationsInPanel)
                .withAssessorsAccepted(assessorsAccepted)
                .withAssessorsPending(assessorsPending)
                .build();


        when(competitionService.getById(competitionId)).thenReturn(competitionResource);
        when(competitionKeyApplicationStatisticsRestService.getReviewKeyStatisticsByCompetition(competitionId))
                .thenReturn(toGetResponse(reviewKeyStatisticsResource));
        when(reviewRestService.isPendingReviewNotifications(competitionId))
                .thenReturn(toGetResponse(reviewsPending));

        MvcResult result = mockMvc.perform(get("/assessment/panel/competition/{competitionId}", competitionId))
                .andExpect(status().isOk())
                .andExpect(view().name("competition/manage-assessment-panel"))
                .andReturn();

        ReviewViewModel model = (ReviewViewModel) result.getModelAndView().getModel().get("model");

        verify(competitionService, only()).getById(competitionId);

        assertEquals(competitionId, model.getCompetitionId());
        assertEquals(competitionName, model.getCompetitionName());
        assertEquals(competitionStatus, model.getCompetitionStatus());
        assertEquals(applicationsInPanel, model.getApplicationsInPanel());
        assertEquals(assessorsAccepted, model.getAssessorsAccepted());
        assertEquals(assessorsPending, model.getAssessorsInvited());
        assertEquals(reviewsPending, model.isPendingReviewNotifications());
    }

    @Test
    public void notifyAssessors() throws Exception {
        long competitionId = 1L;

        when(reviewRestService.notifyAssessors(competitionId)).thenReturn(restSuccess());

        mockMvc.perform(post("/assessment/panel/competition/{competitionId}/notify-assessors", competitionId))
                .andExpect(status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl(format("/assessment/panel/competition/%d", competitionId)))
                .andReturn();

        verify(reviewRestService, only()).notifyAssessors(competitionId);
    }
}