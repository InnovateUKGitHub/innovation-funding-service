package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.review.builder.ReviewInviteResourceBuilder;
import org.springframework.restdocs.payload.FieldDescriptor;

import java.time.ZonedDateTime;

import static org.innovateuk.ifs.invite.constant.InviteStatus.CREATED;
import static org.innovateuk.ifs.review.builder.ReviewInviteResourceBuilder.newReviewInviteResource;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class ReviewInviteDocs {

    public static final FieldDescriptor[] reviewInviteFields = {
            fieldWithPath("competitionId").description("Id of the competition"),
            fieldWithPath("competitionName").description("Name of the competition"),
            fieldWithPath("email").description("Email of the competition invitee"),
            fieldWithPath("hash").description("Hash id of the competition invite"),
            fieldWithPath("status").description("Status of the competition invite"),
            fieldWithPath("panelDate").description("Start date of the assessment review panel"),
            fieldWithPath("userId").description("Id of user invited"),
    };

    public static final ReviewInviteResourceBuilder REVIEW_INVITE_RESOURCE_BUILDER = newReviewInviteResource()
            .withCompetitionId(1L)
            .withCompetitionName("Connected digital additive manufacturing")
            .withInviteHash("0519d73a-f062-4784-ae86-7a933a7de4c3")
            .withEmail("paul.plum@gmail.com")
            .withPanelDate(ZonedDateTime.now())
            .withUserId(2L)
            .withStatus(CREATED);
}
