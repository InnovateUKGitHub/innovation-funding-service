package org.innovateuk.ifs.review.controller;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.review.resource.ReviewRejectOutcomeResource;
import org.innovateuk.ifs.review.resource.ReviewResource;
import org.innovateuk.ifs.review.transactional.ReviewService;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static java.util.Collections.nCopies;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.review.builder.ReviewRejectOutcomeResourceBuilder.newReviewRejectOutcomeResource;
import static org.innovateuk.ifs.review.builder.ReviewResourceBuilder.newReviewResource;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ReviewControllerTest extends BaseControllerMockMVCTest<ReviewController> {

    private static final long applicationId = 1L;
    private static final long competitionId = 5L;
    private static final long userId = 2L;

    @Mock
    private ReviewService reviewServiceMock;

    @Override
    public ReviewController supplyControllerUnderTest() {
        return new ReviewController();
    }

    @Test
    public void assignApplication() throws Exception {
        when(reviewServiceMock.assignApplicationToPanel(applicationId)).thenReturn(serviceSuccess());
        mockMvc.perform(post("/assessmentpanel/assign-application/{applicationId}", applicationId))
                .andExpect(status().isOk());

        verify(reviewServiceMock, only()).assignApplicationToPanel(applicationId);
    }

    @Test
    public void unAssignApplication() throws Exception {
        when(reviewServiceMock.unassignApplicationFromPanel(applicationId)).thenReturn(serviceSuccess());
        mockMvc.perform(post("/assessmentpanel/unassign-application/{applicationId}", applicationId))
                .andExpect(status().isOk());

        verify(reviewServiceMock, only()).unassignApplicationFromPanel(applicationId);
    }

    @Test
    public void notifyAssessors() throws Exception {
        when(reviewServiceMock.createAndNotifyReviews(competitionId)).thenReturn(serviceSuccess());

        mockMvc.perform(post("/assessmentpanel/notify-assessors/{competitionId}", competitionId))
                .andExpect(status().isOk());

        verify(reviewServiceMock, only()).createAndNotifyReviews(competitionId);
    }

    @Test
    public void isPendingReviewNotifications() throws Exception {
        Boolean expected = true;
        when(reviewServiceMock.isPendingReviewNotifications(competitionId)).thenReturn(serviceSuccess(expected));

        mockMvc.perform(get("/assessmentpanel/notify-assessors/{competitionId}", competitionId))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(expected)));

        verify(reviewServiceMock, only()).isPendingReviewNotifications(competitionId);
    }

    @Test
    public void getAssessmentReviews() throws Exception {
        List<ReviewResource> assessmentReviews = newReviewResource().build(2);

        when(reviewServiceMock.getReviews(userId, competitionId)).thenReturn(serviceSuccess(assessmentReviews));

        mockMvc.perform(get("/assessmentpanel/user/{userId}/competition/{competitionId}", userId, competitionId))
                .andExpect(status().isOk())
                .andExpect(content().json(toJson(assessmentReviews)));

        verify(reviewServiceMock, only()).getReviews(userId, competitionId);
    }

    @Test
    public void acceptInvitation() throws Exception {
        long assessmentReviewId = 1L;

        when(reviewServiceMock.acceptReview(assessmentReviewId)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/assessmentpanel/review/{id}/accept", assessmentReviewId))
                .andExpect(status().isOk());

        verify(reviewServiceMock, only()).acceptReview(assessmentReviewId);
    }

    @Test
    public void rejectInvitation() throws Exception {
        long assessmentReviewId = 1L;
        String rejectComment = String.join(" ", nCopies(100, "comment"));
        ReviewRejectOutcomeResource reviewRejectOutcomeResource = newReviewRejectOutcomeResource()
                .withReason(rejectComment)
                .build();

        when(reviewServiceMock.rejectReview(assessmentReviewId, reviewRejectOutcomeResource)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/assessmentpanel/review/{id}/reject", assessmentReviewId)
                .contentType(APPLICATION_JSON)
                .content(toJson(reviewRejectOutcomeResource)))
                .andExpect(status().isOk());

        verify(reviewServiceMock, only()).rejectReview(assessmentReviewId, reviewRejectOutcomeResource);
    }

    @Test
    public void getReview() throws Exception {
        ReviewResource reviewResource = newReviewResource().build();

        when(reviewServiceMock.getReview(reviewResource.getId())).thenReturn(serviceSuccess(reviewResource));

        mockMvc.perform(get("/assessmentpanel/review/{id}", reviewResource.getId()))
                .andExpect(status().isOk());

        verify(reviewServiceMock, only()).getReview(reviewResource.getId());
    }
}