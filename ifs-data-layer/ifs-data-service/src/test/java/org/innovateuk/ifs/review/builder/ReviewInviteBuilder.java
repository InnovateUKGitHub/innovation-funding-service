package org.innovateuk.ifs.review.builder;

import org.innovateuk.ifs.Builder;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.invite.builder.BaseInviteBuilder;
import org.innovateuk.ifs.review.domain.ReviewInvite;
import org.innovateuk.ifs.user.domain.User;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class ReviewInviteBuilder extends BaseInviteBuilder<Competition, ReviewInvite, ReviewInviteBuilder> {
    private ReviewInviteBuilder(List<BiConsumer<Integer, ReviewInvite>> multiActions) {
        super(multiActions);
    }

    public static ReviewInviteBuilder newReviewInvite() {
        return new ReviewInviteBuilder(emptyList()).with(uniqueIds());
    }

    public static ReviewInviteBuilder newReviewInviteWithoutId() {
        return new ReviewInviteBuilder(emptyList());
    }

    @Override
    protected ReviewInviteBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ReviewInvite>> actions) {
        return new ReviewInviteBuilder(actions);
    }

    public ReviewInviteBuilder withCompetition(Competition... competitions) {
        return withTarget(competitions);
    }

    public ReviewInviteBuilder withCompetition(Builder<Competition, ?> competition) {
        return withCompetition(competition.build());
    }

    public ReviewInviteBuilder withName(String... names) {
        return withArraySetFieldByReflection("name", names);
    }

    public ReviewInviteBuilder withSentBy(User... users) {
        return  withArraySetFieldByReflection("sentBy", users);
    }

    public ReviewInviteBuilder withSentOn(ZonedDateTime... dates) {
        return  withArraySetFieldByReflection("sentOn", dates);
    }
    @Override
    protected ReviewInvite createInitial() {
        return new ReviewInvite();
    }
}
