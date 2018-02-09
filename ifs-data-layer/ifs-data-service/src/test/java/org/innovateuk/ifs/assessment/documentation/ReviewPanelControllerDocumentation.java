package org.innovateuk.ifs.assessment.documentation;

import org.innovateuk.ifs.BaseControllerMockMVCTest;
import org.innovateuk.ifs.assessment.review.controller.ReviewPanelController;
import org.innovateuk.ifs.assessment.review.resource.AssessmentReviewRejectOutcomeResource;
import org.innovateuk.ifs.assessment.review.resource.AssessmentReviewResource;
import org.junit.Test;
import org.springframework.http.MediaType;

import java.util.List;

import static org.innovateuk.ifs.assessment.documentation.AssessmentReviewRejectOutcomeDocs.assessmentReviewRejectOutcomeResourceBuilder;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.documentation.AssessmentReviewDocs.assessmentReviewFields;
import static org.innovateuk.ifs.documentation.AssessmentReviewDocs.assessmentReviewResourceBuilder;
import static org.innovateuk.ifs.util.JsonMappingUtil.toJson;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ReviewPanelControllerDocumentation extends BaseControllerMockMVCTest<ReviewPanelController>{

    private static final long applicationId = 1L;
    private static final long competitionId = 2L;
    private static final long userId = 3L;
    private static final long reviewId = 4L;

    @Override
    public ReviewPanelController supplyControllerUnderTest() {
        return new ReviewPanelController();
    }

    @Test
    public void assignApplication() throws Exception {
        when(assessmentPanelServiceMock.assignApplicationToPanel(applicationId)).thenReturn(serviceSuccess());
        mockMvc.perform(post("/assessmentpanel/assign-application/{applicationId}", applicationId))
                .andExpect(status().isOk())
                .andDo(document("assessmentpanel/{method-name}",
                        pathParameters(
                                parameterWithName("applicationId").description("Id of the application to assign to assessment panel")
                        )));

        verify(assessmentPanelServiceMock, only()).assignApplicationToPanel(applicationId);
    }

    @Test
    public void unassignApplication() throws Exception {
        when(assessmentPanelServiceMock.unassignApplicationFromPanel(applicationId)).thenReturn(serviceSuccess());
        mockMvc.perform(post("/assessmentpanel/unassign-application/{applicationId}", applicationId))
                .andExpect(status().isOk())
                .andDo(document("assessmentpanel/{method-name}",
                        pathParameters(
                                parameterWithName("applicationId").description("Id of the application to unassign from assessment panel")
                        )));

        verify(assessmentPanelServiceMock, only()).unassignApplicationFromPanel(applicationId);
    }

    @Test
    public void getAssessmentReviews() throws Exception {
        List<AssessmentReviewResource> assessmentResources = assessmentReviewResourceBuilder.build(2);

        when(assessmentPanelServiceMock.getAssessmentReviews(userId, competitionId)).thenReturn(serviceSuccess(assessmentResources));
        mockMvc.perform(get("/assessmentpanel/user/{userId}/competition/{competitionId}", userId, competitionId))
                .andExpect(status().isOk())
                .andDo(document("assessmentpanel/{method-name}",
                        pathParameters(
                                parameterWithName("userId").description("Id of the user to receive assessment reviews for"),
                                parameterWithName("competitionId").description("Id of the competition to receive assessment reviews for")
                        ),
                        responseFields(fieldWithPath("[]").description("List of reviews"))
                                .andWithPrefix("[].", assessmentReviewFields)
                ));

        verify(assessmentPanelServiceMock, only()).getAssessmentReviews(userId, competitionId);
    }

    @Test
    public void getAssessmentReview() throws Exception {
        AssessmentReviewResource assessmentReviewResource = assessmentReviewResourceBuilder.build();

        when(assessmentPanelServiceMock.getAssessmentReview(reviewId)).thenReturn(serviceSuccess(assessmentReviewResource));

        mockMvc.perform(get("/assessmentpanel/review/{reviewId}", reviewId))
                .andExpect(status().isOk())
                .andDo(document("assessmentpanel/{method-name}",
                        pathParameters(
                                parameterWithName("reviewId").description("Id of the review to receive")
                        ),
                        responseFields(assessmentReviewFields)
                ));

        verify(assessmentPanelServiceMock, only()).getAssessmentReview(reviewId);
    }

    @Test
    public void acceptInvitation() throws Exception {
        when(assessmentPanelServiceMock.acceptAssessmentReview(reviewId)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/assessmentpanel/review/{reviewId}/accept", reviewId))
                .andExpect(status().isOk())
                .andDo(document("assessmentpanel/{method-name}",
                        pathParameters(
                                parameterWithName("reviewId").description("Id of the review to accept")
                        )));

        verify(assessmentPanelServiceMock, only()).acceptAssessmentReview(reviewId);
    }

    @Test
    public void rejectInvitation() throws Exception {
        AssessmentReviewRejectOutcomeResource rejectOutcomeResource = assessmentReviewRejectOutcomeResourceBuilder.build();
        when(assessmentPanelServiceMock.rejectAssessmentReview(reviewId, rejectOutcomeResource)).thenReturn(serviceSuccess());

        mockMvc.perform(put("/assessmentpanel/review/{reviewId}/reject", reviewId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(rejectOutcomeResource)))
                .andExpect(status().isOk())
                .andDo(document("assessmentpanel/{method-name}",
                        pathParameters(
                                parameterWithName("reviewId").description("Id of the review to reject")
                        )));

        verify(assessmentPanelServiceMock, only()).rejectAssessmentReview(reviewId, rejectOutcomeResource);
    }
}