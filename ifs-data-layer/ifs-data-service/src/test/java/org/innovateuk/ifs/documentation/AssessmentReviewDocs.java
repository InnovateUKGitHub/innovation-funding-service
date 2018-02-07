package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.assessment.builder.AssessmentReviewResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import java.time.LocalDate;

import static org.innovateuk.ifs.assessment.builder.AssessmentReviewResourceBuilder.newAssessmentReviewResource;
import static org.innovateuk.ifs.assessment.documentation.AssessmentReviewRejectOutcomeDocs.assessmentReviewRejectOutcomeResourceBuilder;
import static org.innovateuk.ifs.assessment.review.resource.AssessmentReviewState.PENDING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class AssessmentReviewDocs {
    public static final FieldDescriptor[] assessmentReviewFields = {
            fieldWithPath("id").description("Id of the assessment review"),
            fieldWithPath("event").description("currently not used"),
            fieldWithPath("startDate").description("start date of the assessment review"),
            fieldWithPath("endDate").description("end date of the assessment review"),
            fieldWithPath("rejection").description("The reason for rejecting the application"),
            fieldWithPath("processRole").description("process role of the assigned assessor"),
            fieldWithPath("application").description("the id of the application being assessed"),
            fieldWithPath("applicationName").description("the name of the application being assessed"),
            fieldWithPath("competition").description("the competition id of the application being assessed"),
            fieldWithPath("assessmentReviewState").description("the current workflow state of the assessment review process"),
            fieldWithPath("internalParticipant").description("the user id of an internal user who is working on the process"),
    };

    public static final AssessmentReviewResourceBuilder assessmentReviewResourceBuilder = newAssessmentReviewResource()
            .withId(1L)
            .withStartDate(LocalDate.now())
            .withEndDate(LocalDate.now().plusDays(14))
            .withRejection(assessmentReviewRejectOutcomeResourceBuilder)
            .withActivityState(PENDING)
            .withProcessRole(1L)
            .withApplication(2L);
}
