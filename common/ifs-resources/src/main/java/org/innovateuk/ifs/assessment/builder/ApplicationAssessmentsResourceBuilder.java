package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.assessment.resource.ApplicationAssessmentResource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class ApplicationAssessmentsResourceBuilder extends BaseBuilder<ApplicationAssessmentResource, ApplicationAssessmentsResourceBuilder> {

    public static ApplicationAssessmentsResourceBuilder newApplicationAssessmentResource() {
        return new ApplicationAssessmentsResourceBuilder(emptyList());
    }

    protected ApplicationAssessmentsResourceBuilder(List<BiConsumer<Integer, ApplicationAssessmentResource>> newActions) {
        super(newActions);
    }

    @Override
    protected ApplicationAssessmentsResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ApplicationAssessmentResource>> actions) {
        return new ApplicationAssessmentsResourceBuilder(actions);
    }

    @Override
    protected ApplicationAssessmentResource createInitial() {
        return new ApplicationAssessmentResource();
    }

    public ApplicationAssessmentsResourceBuilder withApplicationId(Long... applicationIds) {
        return withArraySetFieldByReflection("applicationId", applicationIds);
    }

    public ApplicationAssessmentsResourceBuilder withAssessmentId(Long... assessmentIds) {
        return withArraySetFieldByReflection("assessmentId", assessmentIds);
    }

    public ApplicationAssessmentsResourceBuilder withScores(Map<Long, BigDecimal>... scores) {
        return withArraySetFieldByReflection("scores", scores);
    }

    public ApplicationAssessmentsResourceBuilder withFeedback(Map<Long, String>... feedback) {
        return withArraySetFieldByReflection("feedback", feedback);
    }
}
