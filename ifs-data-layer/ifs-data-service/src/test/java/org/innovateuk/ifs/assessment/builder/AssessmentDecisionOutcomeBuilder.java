package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.assessment.domain.AssessmentDecisionOutcome;
import org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class AssessmentDecisionOutcomeBuilder
        extends BaseBuilder<AssessmentDecisionOutcome, AssessmentDecisionOutcomeBuilder> {

    private AssessmentDecisionOutcomeBuilder(List<BiConsumer<Integer, AssessmentDecisionOutcome>> multiActions) {
        super(multiActions);
    }

    public static AssessmentDecisionOutcomeBuilder newAssessmentDecisionOutcome() {
        return new AssessmentDecisionOutcomeBuilder(emptyList());
    }

    @Override
    protected AssessmentDecisionOutcomeBuilder createNewBuilderWithActions(List<BiConsumer<Integer,
            AssessmentDecisionOutcome>> actions) {
        return new AssessmentDecisionOutcomeBuilder(actions);
    }

    @Override
    protected AssessmentDecisionOutcome createInitial() {
        return new AssessmentDecisionOutcome();
    }

    public AssessmentDecisionOutcomeBuilder withId(Long... ids) {
        return withArray(BaseBuilderAmendFunctions::setId, ids);
    }

    public AssessmentDecisionOutcomeBuilder withFundingConfirmation(Boolean... fundingConfirmations) {
        return withArray((value, assessmentDecisionOutcome) -> assessmentDecisionOutcome.setFundingConfirmation(value), fundingConfirmations);
    }

    public AssessmentDecisionOutcomeBuilder withComment(String... comments) {
        return withArray((value, assessmentDecisionOutcome) -> assessmentDecisionOutcome.setComment(value), comments);
    }

    public AssessmentDecisionOutcomeBuilder withFeedback(String... feedback) {
        return withArray((value, assessmentDecisionOutcome) -> assessmentDecisionOutcome.setFeedback(value), feedback);
    }

}