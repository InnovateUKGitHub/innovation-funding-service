package org.innovateuk.ifs.documentation;

import org.innovateuk.ifs.review.builder.ReviewInviteResourceBuilder;

import java.time.ZonedDateTime;

import static org.innovateuk.ifs.invite.constant.InviteStatus.CREATED;
import static org.innovateuk.ifs.review.builder.ReviewInviteResourceBuilder.newReviewInviteResource;

public class ReviewInviteDocs {

    public static final ReviewInviteResourceBuilder REVIEW_INVITE_RESOURCE_BUILDER = newReviewInviteResource()
            .withCompetitionId(1L)
            .withCompetitionName("Connected digital additive manufacturing")
            .withInviteHash("0519d73a-f062-4784-ae86-7a933a7de4c3")
            .withEmail("paul.plum@gmail.com")
            .withPanelDate(ZonedDateTime.now())
            .withUserId(2L)
            .withStatus(CREATED);
}
