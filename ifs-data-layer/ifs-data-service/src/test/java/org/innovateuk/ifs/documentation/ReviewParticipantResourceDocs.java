package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class ReviewParticipantResourceDocs {

    public static final FieldDescriptor[] reviewParticipantFields = {
            fieldWithPath("id").description("Id of the review participant"),
            fieldWithPath("invite").description("Invite of the review participant"),
            fieldWithPath("rejectionReason").description("Rejection reason of the review participant"),
            fieldWithPath("rejectionReasonComment").description("Rejection reason comment of the review participant"),
            fieldWithPath("role").description("Role of the review participant"),
            fieldWithPath("assessorAcceptsDate").description("Assessor accepts date of the review participant"),
            fieldWithPath("assessorDeadlineDate").description("Assessor deadline date of the review participant"),
            fieldWithPath("pendingAssessments").description("Pending assessments of the review participant"),
            fieldWithPath("submittedAssessments").description("Submitted assessments of the review participant"),
            fieldWithPath("totalAssessments").description("Total assessments of the review participant"),
            fieldWithPath("competitionStatus").description("Competition status of the review participant"),
            fieldWithPath("awaitingApplications").description("Awaiting applications of the review participant"),
            fieldWithPath("competitionId").description("Competition Id of the review participant"),
            fieldWithPath("userId").description("User Id of the review participant"),
            fieldWithPath("status").description("Status of the review participant"),
            fieldWithPath("competitionName").description("Competition name of the review participant"),
    };


}
