package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.assessment.builder.CompetitionInviteResourceBuilder;
import org.innovateuk.ifs.invite.resource.CompetitionRejectionResource;
import org.springframework.restdocs.payload.FieldDescriptor;

import static org.innovateuk.ifs.assessment.builder.CompetitionInviteResourceBuilder.newCompetitionInviteResource;
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

    public static final CompetitionInviteResourceBuilder competitionInviteResourceBuilder = newCompetitionInviteResource()
            .withIds(1L)
            .withCompetitionName("Juggling Craziness")
            .withHash("0519d73a-f062-4784-ae86-7a933a7de4c3")
            .withEmail("paul.plum@gmail.com");

    public static final CompetitionRejectionResource competitionInviteResource =
            new CompetitionRejectionResource(newRejectionReasonResource()
                    .withId(1L)
                    .build(),
                    "own company");

}
