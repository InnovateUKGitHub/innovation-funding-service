package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.review.builder.ReviewResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import java.time.LocalDate;

import static org.innovateuk.ifs.assessment.documentation.AssessmentReviewRejectOutcomeDocs.reviewRejectOutcomeResourceBuilder;
import static org.innovateuk.ifs.review.builder.ReviewResourceBuilder.newReviewResource;
import static org.innovateuk.ifs.review.resource.ReviewState.PENDING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class ReviewDocs {
    public static final FieldDescriptor[] reviewFields = {
            fieldWithPath("id").description("Id of the assessment review"),
            fieldWithPath("event").description("currently not used"),
            fieldWithPath("startDate").description("start date of the assessment review"),
            fieldWithPath("endDate").description("end date of the assessment review"),
            fieldWithPath("rejection").description("The reason for rejecting the application"),
            fieldWithPath("processRole").description("process role of the assigned assessor"),
            fieldWithPath("application").description("the id of the application being assessed"),
            fieldWithPath("applicationName").description("the name of the application being assessed"),
            fieldWithPath("competition").description("the competition id of the application being assessed"),
            fieldWithPath("reviewState").description("the current workflow state of the assessment review process"),
            fieldWithPath("internalParticipant").description("the user id of an internal user who is working on the process"),
    };

    public static final ReviewResourceBuilder reviewResourceBuilder = newReviewResource()
            .withId(1L)
            .withStartDate(LocalDate.now())
            .withEndDate(LocalDate.now().plusDays(14))
            .withRejection(reviewRejectOutcomeResourceBuilder)
            .withActivityState(PENDING)
            .withProcessRole(1L)
            .withApplication(2L);
}
