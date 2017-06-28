package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.assessment.domain.AssessmentFundingDecisionOutcome;
import org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class AssessmentFundingDecisionOutcomeBuilder
        extends BaseBuilder<AssessmentFundingDecisionOutcome, AssessmentFundingDecisionOutcomeBuilder> {

    private AssessmentFundingDecisionOutcomeBuilder(List<BiConsumer<Integer, AssessmentFundingDecisionOutcome>> multiActions) {
        super(multiActions);
    }

    public static AssessmentFundingDecisionOutcomeBuilder newAssessmentFundingDecisionOutcome() {
        return new AssessmentFundingDecisionOutcomeBuilder(emptyList());
    }

    @Override
    protected AssessmentFundingDecisionOutcomeBuilder createNewBuilderWithActions(List<BiConsumer<Integer,
            AssessmentFundingDecisionOutcome>> actions) {
        return new AssessmentFundingDecisionOutcomeBuilder(actions);
    }

    @Override
    protected AssessmentFundingDecisionOutcome createInitial() {
        return new AssessmentFundingDecisionOutcome();
    }

    public AssessmentFundingDecisionOutcomeBuilder withId(Long... ids) {
        return withArray(BaseBuilderAmendFunctions::setId, ids);
    }

    public AssessmentFundingDecisionOutcomeBuilder withFundingConfirmation(Boolean... fundingConfirmations) {
        return withArray((value, assessmentFundingDecisionOutcome) -> assessmentFundingDecisionOutcome.setFundingConfirmation(value), fundingConfirmations);
    }

    public AssessmentFundingDecisionOutcomeBuilder withComment(String... comments) {
        return withArray((value, assessmentFundingDecisionOutcome) -> assessmentFundingDecisionOutcome.setComment(value), comments);
    }

    public AssessmentFundingDecisionOutcomeBuilder withFeedback(String... feedback) {
        return withArray((value, assessmentFundingDecisionOutcome) -> assessmentFundingDecisionOutcome.setFeedback(value), feedback);
    }

}