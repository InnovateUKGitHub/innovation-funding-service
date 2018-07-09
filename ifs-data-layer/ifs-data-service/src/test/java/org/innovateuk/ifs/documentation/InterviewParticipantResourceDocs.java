package org.innovateuk.ifs.documentation;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class InterviewParticipantResourceDocs {
    public static final FieldDescriptor[] interviewParticipantResourceFields = {
            fieldWithPath("id").description("Id of the interview participant"),
            fieldWithPath("invite").description("Invite of the interview participant"),
            fieldWithPath("rejectionReason").description("Rejection reason of the interview participant"),
            fieldWithPath("rejectionReasonComment").description("Rejection reason comment of the interview participant"),
            fieldWithPath("role").description("Role of the interview participant"),
            fieldWithPath("assessorAcceptsDate").description("Assessor accepts date of the interview participant"),
            fieldWithPath("assessorDeadlineDate").description("Assessor deadline date of the interview participant"),
            fieldWithPath("pendingAssessments").description("Pending assessments of the interview participant"),
            fieldWithPath("submittedAssessments").description("Submitted assessments of the interview participant"),
            fieldWithPath("totalAssessments").description("Total assessments of the interview participant"),
            fieldWithPath("competitionStatus").description("Competition status of the interview participant"),
            fieldWithPath("awaitingApplications").description("Awaiting applications of the interview participant"),
            fieldWithPath("competitionId").description("Competition Id of the interview participant"),
            fieldWithPath("userId").description("User Id of the interview participant"),
            fieldWithPath("status").description("Status of the interview participant"),
            fieldWithPath("competitionName").description("Competition name of the interview participant"),
    };
}
