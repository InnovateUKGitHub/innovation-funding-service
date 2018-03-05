package org.innovateuk.ifs.review.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions;
import org.innovateuk.ifs.review.domain.ReviewRejectOutcome;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class ReviewRejectOutcomeBuilder extends BaseBuilder<ReviewRejectOutcome, ReviewRejectOutcomeBuilder> {

    private ReviewRejectOutcomeBuilder(List<BiConsumer<Integer, ReviewRejectOutcome>> multiActions) {
        super(multiActions);
    }

    public static ReviewRejectOutcomeBuilder newReviewRejectOutcome() {
        return new ReviewRejectOutcomeBuilder(emptyList());
    }

    @Override
    protected ReviewRejectOutcomeBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ReviewRejectOutcome>> actions) {
        return new ReviewRejectOutcomeBuilder(actions);
    }

    @Override
    protected ReviewRejectOutcome createInitial() {
        return new ReviewRejectOutcome();
    }

    public ReviewRejectOutcomeBuilder withId(Long... ids) {
        return withArray(BaseBuilderAmendFunctions::setId, ids);
    }

    public ReviewRejectOutcomeBuilder withRejectionComment(String... rejectionComments) {
        return withArray((rejectionComment, assessmentRejectOutcome) -> assessmentRejectOutcome.setRejectReason(rejectionComment), rejectionComments);
    }
}