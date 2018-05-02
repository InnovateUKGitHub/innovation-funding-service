package org.innovateuk.ifs.review.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.review.domain.Review;
import org.innovateuk.ifs.review.domain.ReviewRejectOutcome;
import org.innovateuk.ifs.review.resource.ReviewState;
import org.innovateuk.ifs.user.domain.ProcessRole;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;

public class ReviewBuilder extends BaseBuilder<Review, ReviewBuilder> {

    private ReviewBuilder(List<BiConsumer<Integer, Review>> multiActions) {
        super(multiActions);
    }

    public static ReviewBuilder newReview() {
        return new ReviewBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected ReviewBuilder createNewBuilderWithActions(List<BiConsumer<Integer, Review>> actions) {
        return new ReviewBuilder(actions);
    }

    @Override
    protected Review createInitial() {
        return new Review();
    }

    public ReviewBuilder withId(Long... ids) {
        return withArray((id, invite) -> setField("id", id, invite), ids);
    }

    public ReviewBuilder withRejection(ReviewRejectOutcome... rejections) {
        return withArray((rejection, invite) -> invite.setRejection(rejection), rejections);
    }

    public ReviewBuilder withTarget(Application... applications) {
        return withArray((application, invite) -> invite.setTarget(application), applications);
    }

    public ReviewBuilder withParticipant(ProcessRole... participants) {
        return withArray((participant, invite) -> invite.setParticipant(participant), participants);
    }

    public ReviewBuilder withState(ReviewState... states) {
        return withArray((state, invite) -> invite.setProcessState(state), states);
    }
}