package org.innovateuk.ifs.review.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.Builder;
import org.innovateuk.ifs.competition.domain.Competition;
import org.innovateuk.ifs.competition.domain.CompetitionParticipantRole;
import org.innovateuk.ifs.invite.domain.ParticipantStatus;
import org.innovateuk.ifs.invite.domain.RejectionReason;
import org.innovateuk.ifs.review.domain.ReviewInvite;
import org.innovateuk.ifs.review.domain.ReviewParticipant;
import org.innovateuk.ifs.user.domain.User;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

/**
 * Builder for {@link ReviewParticipant}s.
 */
public class ReviewParticipantBuilder extends BaseBuilder<ReviewParticipant, ReviewParticipantBuilder> {

    public static ReviewParticipantBuilder newReviewParticipant() {
        return new ReviewParticipantBuilder(emptyList()).with(uniqueIds());
    }

    private ReviewParticipantBuilder(List<BiConsumer<Integer, ReviewParticipant>> multiActions) {
        super(multiActions);
    }

    @Override
    protected ReviewParticipantBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ReviewParticipant>> actions) {
        return new ReviewParticipantBuilder(actions);
    }

    @Override
    protected ReviewParticipant createInitial() {
        try {
            Constructor c = ReviewParticipant.class.getDeclaredConstructor();
            c.setAccessible(true);
            ReviewParticipant instance = (ReviewParticipant) c.newInstance();
            setField("status", ParticipantStatus.PENDING, instance);
            return instance;
        } catch (NoSuchMethodException | IllegalAccessException |InstantiationException | InvocationTargetException e) {
            throw new IllegalStateException("Missing default constructor for ReviewParticipant", e);
        }
    }

    public ReviewParticipantBuilder withId(Long... ids) {
        return withArray((id, i) -> setField("id", id, i), ids);
    }

    public ReviewParticipantBuilder withStatus(ParticipantStatus... states) {
        return withArray((status, s) -> setField("status", status, s), states);
    }

    public ReviewParticipantBuilder withRole(CompetitionParticipantRole... roles) {
        return withArray((role, r) -> setField("role", role, r), roles);
    }

    public ReviewParticipantBuilder withCompetition(Competition... competitions) {
        return withArray((competition, p) -> setField("competition", competition, p), competitions);
    }

    public ReviewParticipantBuilder withCompetition(Builder<Competition, ?> competitions) {
        return withCompetition(competitions.build());
    }

    public ReviewParticipantBuilder withInvite(ReviewInvite... invites) {
        return withArray((invite, i) -> setField("invite", invite, i), invites);
    }

    public ReviewParticipantBuilder withInvite(Builder<ReviewInvite, ?> invite) {
        return withInvite(invite.build());
    }

    public ReviewParticipantBuilder withUser(User... users) {
        return withArray((user, u) -> setField("user", user, u), users);
    }

    public ReviewParticipantBuilder withUser(Builder<User, ?> user) {
        return withUser(user.build());
    }

    public ReviewParticipantBuilder withRejectionReason(RejectionReason... rejectionReasons) {
        return withArray((rejectionReason, r) -> setField("rejectionReason", rejectionReason, r), rejectionReasons);
    }

    public ReviewParticipantBuilder withRejectionReason(Builder<RejectionReason, ?> rejectionReason) {
        return withRejectionReason(rejectionReason.build());
    }

    public ReviewParticipantBuilder withRejectionComment(String... rejectionReasonComments) {
        return withArray((rejectionComment, r) -> setField("rejectionReasonComment", rejectionComment, r), rejectionReasonComments);
    }
}
