package com.worth.ifs.documentation;

import com.worth.ifs.invite.builder.CompetitionParticipantResourceBuilder;
import com.worth.ifs.invite.resource.CompetitionParticipantRoleResource;
import com.worth.ifs.invite.resource.ParticipantStatusResource;
import com.worth.ifs.invite.resource.RejectionReasonResource;
import org.springframework.restdocs.payload.FieldDescriptor;

import java.time.LocalDateTime;

import static com.worth.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static com.worth.ifs.assessment.builder.CompetitionInviteResourceBuilder.newCompetitionInviteResource;
import static com.worth.ifs.invite.builder.CompetitionParticipantResourceBuilder.newCompetitionParticipantResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class CompetitionParticipantResourceDocs {

    public static final FieldDescriptor[] competitionParticipantResourceFields = {
            fieldWithPath("id").description("Id of the competitionParticipantResource"),
            fieldWithPath("competitionId").description("Id of the competition"),
            fieldWithPath("competitionName").description("Name of the competition"),
            fieldWithPath("userId").description("Id of the associated user"),
            fieldWithPath("invite").description("Invitation to participate in the competition"),
            fieldWithPath("rejectionReason").description("Reason for rejecting the competition invitation"),
            fieldWithPath("rejectionReasonComment").description("Additional feedback for rejection of invitation"),
            fieldWithPath("role").description("Role of the associated user"),
            fieldWithPath("status").description("Status of invitation to competition"),
            fieldWithPath("assessorAcceptsDate").description("Date of acceptance by the Assessor to the competition"),
            fieldWithPath("assessorDeadlineDate").description("Date of deadline for assessor to accept invitation to competition"),
            fieldWithPath("submittedAssessments").description("Number of submitted assessments for the competition"),
            fieldWithPath("totalAssessments").description("Total number of assessments accepted by the assessor for the competition")
   };

    public static final CompetitionParticipantResourceBuilder competitionParticipantResourceBuilder = newCompetitionParticipantResource()
            .withId(1L)
            .withCompetition(2L)
            .withUser(3L)
            .withInvite(newCompetitionInviteResource().with(id(4L)).build())
            .withCompetitionName("Juggling Craziness")
            .withCompetitionParticipantRole(CompetitionParticipantRoleResource.ASSESSOR)
            .withStatus(ParticipantStatusResource.ACCEPTED)
            .withSubmittedAssessments(1L)
            .withTotalAssessments(4L)
            .withRejectionReason(new RejectionReasonResource("conflict", true, 1))
            .withRejectionReasonComment("Reason comment")
            .withAssessorAcceptsDate(LocalDateTime.now().plusDays(35))
            .withAssessorDeadlineDate(LocalDateTime.now().plusDays(40));
}
