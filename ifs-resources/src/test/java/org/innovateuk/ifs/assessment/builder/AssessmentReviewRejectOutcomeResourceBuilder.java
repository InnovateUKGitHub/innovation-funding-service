package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.assessment.panel.resource.AssessmentReviewRejectOutcomeResource;
import org.innovateuk.ifs.assessment.resource.AssessmentRejectOutcomeValue;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class AssessmentReviewRejectOutcomeResourceBuilder
        extends BaseBuilder<AssessmentReviewRejectOutcomeResource, AssessmentReviewRejectOutcomeResourceBuilder> {

    private AssessmentReviewRejectOutcomeResourceBuilder(List<BiConsumer<Integer, AssessmentReviewRejectOutcomeResource>> multiActions) {
        super(multiActions);
    }

    public static AssessmentReviewRejectOutcomeResourceBuilder newAssessmentReviewRejectOutcomeResource() {
        return new AssessmentReviewRejectOutcomeResourceBuilder(emptyList());
    }

    @Override
    protected AssessmentReviewRejectOutcomeResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer,
            AssessmentReviewRejectOutcomeResource>> actions) {
        return new AssessmentReviewRejectOutcomeResourceBuilder(actions);
    }

    @Override
    protected AssessmentReviewRejectOutcomeResource createInitial() {
        return new AssessmentReviewRejectOutcomeResource();
    }

    public AssessmentReviewRejectOutcomeResourceBuilder withRejectComment(String... reasons) {
        return withArray((value, assessmentRejectOutcomeResource) -> assessmentRejectOutcomeResource.setReason(value), reasons);
    }
}