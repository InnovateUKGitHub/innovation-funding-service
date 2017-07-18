package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.assessment.domain.AssessmentApplicationAssessorCount;
import org.innovateuk.ifs.assessment.domain.Assessment;

import java.util.List;
import java.util.function.BiConsumer;

public class AssessmentApplicationAssessorCountBuilder extends BaseBuilder<AssessmentApplicationAssessorCount, AssessmentApplicationAssessorCountBuilder> {

    protected AssessmentApplicationAssessorCountBuilder() {
        super();
    }

    protected AssessmentApplicationAssessorCountBuilder(List<BiConsumer<Integer, AssessmentApplicationAssessorCount>> newActions) {
        super(newActions);
    }

    public static AssessmentApplicationAssessorCountBuilder newApplicationAssessmentCount() {
        return new AssessmentApplicationAssessorCountBuilder();
    }

    @Override
    protected AssessmentApplicationAssessorCountBuilder createNewBuilderWithActions(List<BiConsumer<Integer, AssessmentApplicationAssessorCount>> actions) {
        return new AssessmentApplicationAssessorCountBuilder(actions);
    }

    @Override
    protected AssessmentApplicationAssessorCount createInitial() {
        return new AssessmentApplicationAssessorCount();
    }

    public AssessmentApplicationAssessorCountBuilder withApplication(Application ...applications) {
        return withArraySetFieldByReflection("application", applications);
    }

    public AssessmentApplicationAssessorCountBuilder withAssessment(Assessment...assessments) {
        return withArraySetFieldByReflection("assessment", assessments);
    }

    public AssessmentApplicationAssessorCountBuilder withAssessorCount(Integer ...assessorCounts) {
        return withArraySetFieldByReflection("assessorCount", assessorCounts);
    }
}
