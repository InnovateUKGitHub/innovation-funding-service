package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.review.resource.ReviewRejectOutcomeResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class AssessmentReviewRejectOutcomeResourceBuilder
        extends BaseBuilder<ReviewRejectOutcomeResource, AssessmentReviewRejectOutcomeResourceBuilder> {

    private AssessmentReviewRejectOutcomeResourceBuilder(List<BiConsumer<Integer, ReviewRejectOutcomeResource>> multiActions) {
        super(multiActions);
    }

    public static AssessmentReviewRejectOutcomeResourceBuilder newAssessmentReviewRejectOutcomeResource() {
        return new AssessmentReviewRejectOutcomeResourceBuilder(emptyList());
    }

    @Override
    protected AssessmentReviewRejectOutcomeResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer,
            ReviewRejectOutcomeResource>> actions) {
        return new AssessmentReviewRejectOutcomeResourceBuilder(actions);
    }

    @Override
    protected ReviewRejectOutcomeResource createInitial() {
        return new ReviewRejectOutcomeResource();
    }

    public AssessmentReviewRejectOutcomeResourceBuilder withReason(String... reasons) {
        return withArray((value, assessmentRejectOutcomeResource) -> assessmentRejectOutcomeResource.setReason(value), reasons);
    }
}