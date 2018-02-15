package org.innovateuk.ifs.assessment.review.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions;
import org.innovateuk.ifs.review.domain.ReviewRejectOutcome;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class AssessmentReviewRejectOutcomeBuilder extends BaseBuilder<ReviewRejectOutcome, AssessmentReviewRejectOutcomeBuilder> {

    private AssessmentReviewRejectOutcomeBuilder(List<BiConsumer<Integer, ReviewRejectOutcome>> multiActions) {
        super(multiActions);
    }

    public static AssessmentReviewRejectOutcomeBuilder newAssessmentReviewRejectOutcome() {
        return new AssessmentReviewRejectOutcomeBuilder(emptyList());
    }

    @Override
    protected AssessmentReviewRejectOutcomeBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ReviewRejectOutcome>> actions) {
        return new AssessmentReviewRejectOutcomeBuilder(actions);
    }

    @Override
    protected ReviewRejectOutcome createInitial() {
        return new ReviewRejectOutcome();
    }

    public AssessmentReviewRejectOutcomeBuilder withId(Long... ids) {
        return withArray(BaseBuilderAmendFunctions::setId, ids);
    }

    public AssessmentReviewRejectOutcomeBuilder withRejectionComment(String... rejectionComments) {
        return withArray((rejectionComment, assessmentRejectOutcome) -> assessmentRejectOutcome.setRejectReason(rejectionComment), rejectionComments);
    }
}