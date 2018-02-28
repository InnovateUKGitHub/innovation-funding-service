package org.innovateuk.ifs.review.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.review.resource.ReviewRejectOutcomeResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class ReviewRejectOutcomeResourceBuilder
        extends BaseBuilder<ReviewRejectOutcomeResource, ReviewRejectOutcomeResourceBuilder> {

    private ReviewRejectOutcomeResourceBuilder(List<BiConsumer<Integer, ReviewRejectOutcomeResource>> multiActions) {
        super(multiActions);
    }

    public static ReviewRejectOutcomeResourceBuilder newReviewRejectOutcomeResource() {
        return new ReviewRejectOutcomeResourceBuilder(emptyList());
    }

    @Override
    protected ReviewRejectOutcomeResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer,
            ReviewRejectOutcomeResource>> actions) {
        return new ReviewRejectOutcomeResourceBuilder(actions);
    }

    @Override
    protected ReviewRejectOutcomeResource createInitial() {
        return new ReviewRejectOutcomeResource();
    }

    public ReviewRejectOutcomeResourceBuilder withReason(String... reasons) {
        return withArray((value, assessmentRejectOutcomeResource) -> assessmentRejectOutcomeResource.setReason(value), reasons);
    }
}