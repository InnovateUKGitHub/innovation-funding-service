package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.assessment.domain.AssessmentRejectOutcome;
import org.innovateuk.ifs.assessment.resource.AssessmentRejectOutcomeValue;
import org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class AssessmentRejectOutcomeBuilder extends BaseBuilder<AssessmentRejectOutcome, AssessmentRejectOutcomeBuilder> {

    private AssessmentRejectOutcomeBuilder(List<BiConsumer<Integer, AssessmentRejectOutcome>> multiActions) {
        super(multiActions);
    }

    public static AssessmentRejectOutcomeBuilder newAssessmentRejectOutcome() {
        return new AssessmentRejectOutcomeBuilder(emptyList());
    }

    @Override
    protected AssessmentRejectOutcomeBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AssessmentRejectOutcome>> actions) {
        return new AssessmentRejectOutcomeBuilder(actions);
    }

    @Override
    protected AssessmentRejectOutcome createInitial() {
        return new AssessmentRejectOutcome();
    }

    public AssessmentRejectOutcomeBuilder withId(Long... ids) {
        return withArray(BaseBuilderAmendFunctions::setId, ids);
    }

    public AssessmentRejectOutcomeBuilder withRejectReason(AssessmentRejectOutcomeValue... rejectReasons) {
        return withArray((value, assessmentRejectOutcome) -> assessmentRejectOutcome.setRejectReason(value), rejectReasons);
    }

    public AssessmentRejectOutcomeBuilder withRejectComment(String... rejectComments) {
        return withArray((value, assessmentRejectOutcome) -> assessmentRejectOutcome.setRejectComment(value), rejectComments);
    }
}