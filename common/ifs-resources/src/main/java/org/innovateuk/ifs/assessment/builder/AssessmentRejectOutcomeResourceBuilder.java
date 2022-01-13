package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.assessment.resource.AssessmentRejectOutcomeResource;
import org.innovateuk.ifs.assessment.resource.AssessmentRejectOutcomeValue;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class AssessmentRejectOutcomeResourceBuilder
        extends BaseBuilder<AssessmentRejectOutcomeResource, AssessmentRejectOutcomeResourceBuilder> {

    private AssessmentRejectOutcomeResourceBuilder(List<BiConsumer<Integer, AssessmentRejectOutcomeResource>> multiActions) {
        super(multiActions);
    }

    public static AssessmentRejectOutcomeResourceBuilder newAssessmentRejectOutcomeResource() {
        return new AssessmentRejectOutcomeResourceBuilder(emptyList());
    }

    @Override
    protected AssessmentRejectOutcomeResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer,
            AssessmentRejectOutcomeResource>> actions) {
        return new AssessmentRejectOutcomeResourceBuilder(actions);
    }

    @Override
    protected AssessmentRejectOutcomeResource createInitial() {
        return new AssessmentRejectOutcomeResource();
    }

    public AssessmentRejectOutcomeResourceBuilder withRejectReason(AssessmentRejectOutcomeValue... rejectReasons) {
        return withArray((value, assessmentRejectOutcomeResource) -> assessmentRejectOutcomeResource.setRejectReason(value), rejectReasons);
    }

    public AssessmentRejectOutcomeResourceBuilder withRejectComment(String... rejectComments) {
        return withArray((value, assessmentRejectOutcomeResource) -> assessmentRejectOutcomeResource.setRejectComment(value), rejectComments);
    }
}
