package org.innovateuk.ifs.review.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.documentation.RejectionReasonResourceDocs;
import org.innovateuk.ifs.documentation.ReviewRejectOutcomeResourceDocs;
import org.innovateuk.ifs.review.controller.ReviewController;
import org.innovateuk.ifs.review.resource.ReviewRejectOutcomeResource;
import org.innovateuk.ifs.review.resource.ReviewResource;
import org.innovateuk.ifs.review.transactional.ReviewService;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;

import java.util.List;

import static org.innovateuk.ifs.assessment.documentation.AssessmentReviewRejectOutcomeDocs.reviewRejectOutcomeResourceBuilder;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.ReviewDocs.reviewFields;
import static org.innovateuk.ifs.documentation.ReviewDocs.reviewResourceBuilder;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ReviewControllerDocumentation extends BaseControllerMockMVCTest<ReviewController>{

    private static final long applicationId = 1L;
    private static final long competitionId = 2L;
    private static final long userId = 3L;
    private static final long reviewId = 4L;

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
                .andExpect(status().isOk())
                .andDo(document("assessmentpanel/{method-name}",
                        pathParameters(
                                parameterWithName("applicationId").description("Id of the application to assign to assessment panel")
                        )));

        verify(reviewServiceMock, only()).assignApplicationToPanel(applicationId);
    }

    @Test
    public void unassignApplication() throws Exception {
        when(reviewServiceMock.unassignApplicationFromPanel(applicationId)).thenReturn(serviceSuccess());
        mockMvc.perform(post("/assessmentpanel/unassign-application/{applicationId}", applicationId))
                .andExpect(status().isOk())
                .andDo(document("assessmentpanel/{method-name}",
                        pathParameters(
                                parameterWithName("applicationId").description("Id of the application to unassign from assessment panel")
                        )));

        verify(reviewServiceMock, only()).unassignApplicationFromPanel(applicationId);
    }

    @Test
    public void getReviews() throws Exception {
        List<ReviewResource> assessmentResources = reviewResourceBuilder.build(2);

        when(reviewServiceMock.getReviews(userId, competitionId)).thenReturn(serviceSuccess(assessmentResources));
        mockMvc.perform(get("/assessmentpanel/user/{userId}/competition/{competitionId}", userId, competitionId))
                .andExpect(status().isOk())
                .andDo(document("assessmentpanel/{method-name}",
                        pathParameters(
                                parameterWithName("userId").description("Id of the user to receive assessment reviews for"),
                                parameterWithName("competitionId").description("Id of the competition to receive assessment reviews for")
                        ),
                        responseFields(fieldWithPath("[]").description("List of reviews"))
                                .andWithPrefix("[].", reviewFields)
                                .andWithPrefix("[].rejection.", ReviewRejectOutcomeResourceDocs.reviewRejectOutcomeResourceFields)

                ));

        verify(reviewServiceMock, only()).getReviews(userId, competitionId);
    }

    @Test
    public void getReview() throws Exception {
        ReviewResource reviewResource = reviewResourceBuilder.build();

        when(reviewServiceMock.getReview(reviewId)).thenReturn(serviceSuccess(reviewResource));

        mockMvc.perform(get("/assessmentpanel/review/{reviewId}", reviewId))
                .andExpect(status().isOk())
                .andDo(document("assessmentpanel/{method-name}",
                        pathParameters(
                                parameterWithName("reviewId").description("Id of the review to receive")
                        ),
                        responseFields(reviewFields)
                        .andWithPrefix("rejection.", ReviewRejectOutcomeResourceDocs.reviewRejectOutcomeResourceFields)
                ));

        verify(reviewServiceMock, only()).getReview(reviewId);
    }

    @Test
    public void acceptInvitation() throws Exception {
        when(reviewServiceMock.acceptReview(reviewId)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/assessmentpanel/review/{reviewId}/accept", reviewId))
                .andExpect(status().isOk())
                .andDo(document("assessmentpanel/{method-name}",
                        pathParameters(
                                parameterWithName("reviewId").description("Id of the review to accept")
                        )));

        verify(reviewServiceMock, only()).acceptReview(reviewId);
    }

    @Test
    public void rejectInvitation() throws Exception {
        ReviewRejectOutcomeResource rejectOutcomeResource = reviewRejectOutcomeResourceBuilder.build();
        when(reviewServiceMock.rejectReview(reviewId, rejectOutcomeResource)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/assessmentpanel/review/{reviewId}/reject", reviewId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(rejectOutcomeResource)))
                .andExpect(status().isOk())
                .andDo(document("assessmentpanel/{method-name}",
                        pathParameters(
                                parameterWithName("reviewId").description("Id of the review to reject")
                        )));

        verify(reviewServiceMock, only()).rejectReview(reviewId, rejectOutcomeResource);
    }
}