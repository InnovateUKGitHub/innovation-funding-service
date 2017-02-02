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

    public AssessmentFundingDecisionOutcomeBuilder withId(Long... values) {
        return withArray(BaseBuilderAmendFunctions::setId, values);
    }

    public AssessmentFundingDecisionOutcomeBuilder withFundingConfirmation(Boolean... values) {
        return withArray((value, object) -> object.setFundingConfirmation(value), values);
    }

    public AssessmentFundingDecisionOutcomeBuilder withComment(String... values) {
        return withArray((value, object) -> object.setComment(value), values);
    }

    public AssessmentFundingDecisionOutcomeBuilder withFeedback(String... values) {
        return withArray((value, object) -> object.setFeedback(value), values);
    }

}