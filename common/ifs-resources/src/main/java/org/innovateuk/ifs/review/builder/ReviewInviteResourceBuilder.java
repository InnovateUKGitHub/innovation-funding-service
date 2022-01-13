package org.innovateuk.ifs.review.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.ReviewInviteResource;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

/**
 * Builder for {@link ReviewInviteResource}
 */
public class ReviewInviteResourceBuilder extends BaseBuilder<ReviewInviteResource, ReviewInviteResourceBuilder> {

    private ReviewInviteResourceBuilder(List<BiConsumer<Integer, ReviewInviteResource>> multiActions) {
        super(multiActions);
    }

    public static ReviewInviteResourceBuilder newReviewInviteResource() {
        return new ReviewInviteResourceBuilder(emptyList());
    }

    public ReviewInviteResourceBuilder withIds(Long... ids) {
        return withArray(BaseBuilderAmendFunctions::setId, ids);
    }

    public ReviewInviteResourceBuilder withCompetitionName(String... competitionNames) {
        return withArraySetFieldByReflection("competitionName", competitionNames);
    }

    public ReviewInviteResourceBuilder withCompetitionId(Long... ids) {
        return withArraySetFieldByReflection("competitionId", ids);
    }

    public ReviewInviteResourceBuilder withUserId(Long... ids) {
        return withArraySetFieldByReflection("userId", ids);
    }

    public ReviewInviteResourceBuilder withEmail(String... emails) {
        return withArraySetFieldByReflection("email", emails);
    }

    public ReviewInviteResourceBuilder withInviteHash(String... hashes) {
        return withArraySetFieldByReflection("hash", hashes);
    }

    public ReviewInviteResourceBuilder withStatus(InviteStatus... statuses) {
        return withArraySetFieldByReflection("status", statuses);
    }

    public ReviewInviteResourceBuilder withPanelDate(ZonedDateTime... panelDates) {
        return withArraySetFieldByReflection("panelDate", panelDates);
    }

    @Override
    protected ReviewInviteResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ReviewInviteResource>> actions) {
        return new ReviewInviteResourceBuilder(actions);
    }

    @Override
    protected ReviewInviteResource createInitial() {
        return new ReviewInviteResource();
    }
}
