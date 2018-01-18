package org.innovateuk.ifs.assessment.panel.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.assessment.panel.domain.AssessmentReviewRejectOutcome;
import org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class AssessmentReviewRejectOutcomeBuilder extends BaseBuilder<AssessmentReviewRejectOutcome, AssessmentReviewRejectOutcomeBuilder> {

    private AssessmentReviewRejectOutcomeBuilder(List<BiConsumer<Integer, AssessmentReviewRejectOutcome>> multiActions) {
        super(multiActions);
    }

    public static AssessmentReviewRejectOutcomeBuilder newAssessmentReviewRejectOutcome() {
        return new AssessmentReviewRejectOutcomeBuilder(emptyList());
    }

    @Override
    protected AssessmentReviewRejectOutcomeBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AssessmentReviewRejectOutcome>> actions) {
        return new AssessmentReviewRejectOutcomeBuilder(actions);
    }

    @Override
    protected AssessmentReviewRejectOutcome createInitial() {
        return new AssessmentReviewRejectOutcome();
    }

    public AssessmentReviewRejectOutcomeBuilder withId(Long... ids) {
        return withArray(BaseBuilderAmendFunctions::setId, ids);
    }

    public AssessmentReviewRejectOutcomeBuilder withRejectionComment(String... rejectionComments) {
        return withArray((rejectionComment, assessmentRejectOutcome) -> assessmentRejectOutcome.setRejectReason(rejectionComment), rejectionComments);
    }
}