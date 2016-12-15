package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.assessment.builder.CompetitionInviteResourceBuilder;
import org.innovateuk.ifs.invite.builder.AssessorInviteToSendResourceBuilder;
import org.innovateuk.ifs.invite.resource.AssessorInviteToSendResource;
import org.innovateuk.ifs.invite.resource.CompetitionRejectionResource;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.assessment.builder.CompetitionInviteResourceBuilder.newCompetitionInviteResource;
import static org.innovateuk.ifs.email.builders.EmailContentResourceBuilder.newEmailContentResource;
import static org.innovateuk.ifs.invite.builder.AssessorInviteToSendResourceBuilder.newAssessorInviteToSendResource;
import static org.innovateuk.ifs.invite.builder.RejectionReasonResourceBuilder.newRejectionReasonResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class CompetitionInviteDocs {

    public static final FieldDescriptor[] competitionInviteFields = {
            fieldWithPath("id").description("Id of the competition invite"),
            fieldWithPath("competitionName").description("Name of the competition"),
            fieldWithPath("email").description("Email of the competition invitee"),
            fieldWithPath("hash").description("Hash id of the competition invite"),
            fieldWithPath("status").description("Status of the competition invite"),
            fieldWithPath("acceptsDate").description("Date of assessor accepting"),
            fieldWithPath("deadlineDate").description("Date of assessor deadline"),
            fieldWithPath("briefingDate").description("Date of assessor briefing"),
            fieldWithPath("assessorPay").description("How much will assessors be paid per application they assess"),
            fieldWithPath("innovationArea").description("Innovation area of the invitee")
    };

    public static final FieldDescriptor[] competitionRejectionFields = {
            fieldWithPath("rejectReason").description("Information about why the invite was rejected"),
            fieldWithPath("rejectComment").description("Optional comments about why the invite was rejected"),
    };

    public static final FieldDescriptor[] assessorToSendFields = {
            fieldWithPath("recipient").description("Name of the recipient of the invite"),
            fieldWithPath("competitionId").description("The id of the competition"),
            fieldWithPath("competitionName").description("The name of the competition"),
            fieldWithPath("emailContent").description("Content of the invite email")
    };

    public static final CompetitionInviteResourceBuilder competitionInviteResourceBuilder = newCompetitionInviteResource()
            .withIds(1L)
            .withCompetitionName("Juggling Craziness")
            .withHash("abcdefghijkl")
            .withEmail("paul.plum@gmail.com");

    public static final CompetitionRejectionResource competitionInviteResource =
            new CompetitionRejectionResource(newRejectionReasonResource()
                    .withId(1L)
                    .build(),
                    "own company");

    public static final AssessorInviteToSendResourceBuilder assessorInviteToSendResourceBuilder = newAssessorInviteToSendResource()
            .withCompetitionId(1L)
            .withCompetitionName("Juggling Craziness")
            .withEmailContent(newEmailContentResource()
                .withSubject("subject")
                .withPlainText("plain text")
                .withHtmlText("<html>html text</htm>")
                .build())
            .withRecipient("Paul Plum");

}
