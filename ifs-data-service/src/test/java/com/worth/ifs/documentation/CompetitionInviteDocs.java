package com.worth.ifs.documentation;

import com.worth.ifs.assessment.builder.CompetitionInviteResourceBuilder;
import com.worth.ifs.invite.resource.CompetitionRejectionResource;
import org.springframework.restdocs.payload.FieldDescriptor;

import static com.worth.ifs.assessment.builder.CompetitionInviteResourceBuilder.newCompetitionInviteResource;
import static com.worth.ifs.invite.builder.RejectionReasonResourceBuilder.newRejectionReasonResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class CompetitionInviteDocs {

    public static final FieldDescriptor[] competitionInviteFields = {
            fieldWithPath("id").description("Id of the competition invite"),
            fieldWithPath("competitionName").description("Name of the competition"),
            fieldWithPath("email").description("Email of the competition invitee"),
            fieldWithPath("status").description("Status of the competition invite"),
            fieldWithPath("acceptsDate").description("Date of assessor accepting"),
            fieldWithPath("deadlineDate").description("Date of assessor deadline")
    };

    public static final FieldDescriptor[] competitionRejectionFields = {
            fieldWithPath("rejectReason").description("Information about why the invite was rejected"),
            fieldWithPath("rejectComment").description("Optional comments about why the invite was rejected"),
    };

    public static final CompetitionInviteResourceBuilder competitionInviteResourceBuilder = newCompetitionInviteResource()
            .withIds(1L)
            .withCompetitionName("Juggling Craziness")
            .withEmail("paul.plum@gmail.com");

    public static final CompetitionRejectionResource competitionInviteResource =
            new CompetitionRejectionResource(newRejectionReasonResource()
                    .withId(1L)
                    .build(),
                    "own company");

}
