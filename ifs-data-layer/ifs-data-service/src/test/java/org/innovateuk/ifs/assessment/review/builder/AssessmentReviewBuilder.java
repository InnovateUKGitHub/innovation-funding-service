package org.innovateuk.ifs.assessment.review.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.review.domain.Review;
import org.innovateuk.ifs.review.domain.ReviewRejectOutcome;
import org.innovateuk.ifs.review.resource.ReviewState;
import org.innovateuk.ifs.user.domain.ProcessRole;
import org.innovateuk.ifs.workflow.domain.ActivityState;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.uniqueIds;
import static org.innovateuk.ifs.workflow.domain.ActivityType.ASSESSMENT_REVIEW;

public class AssessmentReviewBuilder extends BaseBuilder<Review, AssessmentReviewBuilder> {

    private AssessmentReviewBuilder(List<BiConsumer<Integer, Review>> multiActions) {
        super(multiActions);
    }

    public static AssessmentReviewBuilder newAssessmentReview() {
        return new AssessmentReviewBuilder(emptyList()).with(uniqueIds());
    }

    @Override
    protected AssessmentReviewBuilder createNewBuilderWithActions(List<BiConsumer<Integer, Review>> actions) {
        return new AssessmentReviewBuilder(actions);
    }

    @Override
    protected Review createInitial() {
        return new Review();
    }

    public AssessmentReviewBuilder withId(Long... ids) {
        return withArray((id, invite) -> setField("id", id, invite), ids);
    }

    public AssessmentReviewBuilder withRejection(ReviewRejectOutcome... rejections) {
        return withArray((rejection, invite) -> invite.setRejection(rejection), rejections);
    }

    public AssessmentReviewBuilder withTarget(Application... applications) {
        return withArray((application, invite) -> invite.setTarget(application), applications);
    }

    public AssessmentReviewBuilder withParticipant(ProcessRole... participants) {
        return withArray((participant, invite) -> invite.setParticipant(participant), participants);
    }

    public AssessmentReviewBuilder withState(ReviewState... states) {
        return withArray((state, invite) -> invite.setActivityState(new ActivityState(ASSESSMENT_REVIEW, state.getBackingState())), states);
    }
}