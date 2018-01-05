package org.innovateuk.ifs.assessment.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.assessment.panel.resource.AssessmentReviewRejectOutcomeResource;
import org.innovateuk.ifs.assessment.panel.resource.AssessmentReviewResource;
import org.junit.Test;

import java.util.List;

import static java.util.Collections.nCopies;
import static org.innovateuk.ifs.assessment.builder.AssessmentReviewRejectOutcomeResourceBuilder.newAssessmentReviewRejectOutcomeResource;
import static org.innovateuk.ifs.assessment.builder.AssessmentReviewResourceBuilder.newAssessmentReviewResource;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AssessmentPanelControllerTest extends BaseControllerMockMVCTest<AssessmentPanelController> {

    private static final long applicationId = 1L;
    private static final long competitionId = 5L;
    private static final long userId = 2L;

    @Override
    public AssessmentPanelController supplyControllerUnderTest() {
        return new AssessmentPanelController();
    }

    @Test
    public void assignApplication() throws Exception {
        when(assessmentPanelServiceMock.assignApplicationToPanel(applicationId)).thenReturn(serviceSuccess());
        mockMvc.perform(post("/assessmentpanel/assignApplication/{applicationId}", applicationId))
                .andExpect(status().isOk());

        verify(assessmentPanelServiceMock, only()).assignApplicationToPanel(applicationId);
    }

    @Test
    public void unAssignApplication() throws Exception {
        when(assessmentPanelServiceMock.unassignApplicationFromPanel(applicationId)).thenReturn(serviceSuccess());
        mockMvc.perform(post("/assessmentpanel/unassignApplication/{applicationId}", applicationId))
                .andExpect(status().isOk());

        verify(assessmentPanelServiceMock, only()).unassignApplicationFromPanel(applicationId);
    }

    @Test
    public void notifyAssessors() throws Exception {
        when(assessmentPanelServiceMock.createAndNotifyReviews(competitionId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/assessmentpanel/notify-assessors/{competitionId}", competitionId))
                .andExpect(status().isOk());

        verify(assessmentPanelServiceMock, only()).createAndNotifyReviews(competitionId);
    }

    @Test
    public void isPendingReviewNotifications() throws Exception {
        Boolean expected = true;
        when(assessmentPanelServiceMock.isPendingReviewNotifications(competitionId)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/assessmentpanel/notify-assessors/{competitionId}", competitionId))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(expected)));

        verify(assessmentPanelServiceMock, only()).isPendingReviewNotifications(competitionId);
    }

    @Test
    public void getAssessmentReviews() throws Exception {
        List<AssessmentReviewResource> assessmentReviews = newAssessmentReviewResource().build(2);

        when(assessmentPanelServiceMock.getAssessmentReviews(userId, competitionId)).thenReturn(serviceSuccess(assessmentReviews));

        mockMvc.perform(get("/assessmentpanel/user/{userId}/competition/{competitionId}", userId, competitionId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(assessmentReviews)));

        verify(assessmentPanelServiceMock, only()).getAssessmentReviews(userId, competitionId);
    }

    @Test
    public void acceptInvitation() throws Exception {
        long assessmentReviewId = 1L;

        when(assessmentPanelServiceMock.acceptAssessmentReview(assessmentReviewId)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/assessmentpanel/review/{id}/accept", assessmentReviewId))
                .andExpect(status().isOk());

        verify(assessmentPanelServiceMock, only()).acceptAssessmentReview(assessmentReviewId);
    }

    @Test
    public void rejectInvitation() throws Exception {
        long assessmentReviewId = 1L;
        String rejectComment = String.join(" ", nCopies(100, "comment"));
        AssessmentReviewRejectOutcomeResource assessmentReviewRejectOutcomeResource = newAssessmentReviewRejectOutcomeResource()
                .withRejectComment(rejectComment)
                .build();

        when(assessmentPanelServiceMock.rejectAssessmentReview(assessmentReviewId, assessmentReviewRejectOutcomeResource)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/assessmentpanel/review/{id}/reject", assessmentReviewId)
                .contentType(APPLICATION_JSON)
                .content(toJson(assessmentReviewRejectOutcomeResource)))
                .andExpect(status().isOk());

        verify(assessmentPanelServiceMock, only()).rejectAssessmentReview(assessmentReviewId, assessmentReviewRejectOutcomeResource);
    }
}