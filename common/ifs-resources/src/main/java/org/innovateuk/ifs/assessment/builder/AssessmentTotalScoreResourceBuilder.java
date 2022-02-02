package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.assessment.resource.AssessmentTotalScoreResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class AssessmentTotalScoreResourceBuilder extends BaseBuilder<AssessmentTotalScoreResource, AssessmentTotalScoreResourceBuilder> {

    private AssessmentTotalScoreResourceBuilder(List<BiConsumer<Integer, AssessmentTotalScoreResource>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected AssessmentTotalScoreResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AssessmentTotalScoreResource>> actions) {
        return new AssessmentTotalScoreResourceBuilder(actions);
    }

    @Override
    protected AssessmentTotalScoreResource createInitial() {
        return new AssessmentTotalScoreResource(0, 0);
    }

    public static AssessmentTotalScoreResourceBuilder newAssessmentTotalScoreResource() {
        return new AssessmentTotalScoreResourceBuilder(emptyList());
    }

    public AssessmentTotalScoreResourceBuilder withTotalScoreGiven(Integer... value) {
        return withArraySetFieldByReflection("totalScoreGiven", value);
    }

    public AssessmentTotalScoreResourceBuilder withTotalScorePossible(Integer... value) {
        return withArraySetFieldByReflection("totalScorePossible", value);
    }
}
