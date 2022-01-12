package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.assessment.resource.ApplicationAssessmentFeedbackResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class ApplicationAssessmentFeedbackResourceBuilder extends BaseBuilder<ApplicationAssessmentFeedbackResource, ApplicationAssessmentFeedbackResourceBuilder> {

    public static ApplicationAssessmentFeedbackResourceBuilder newApplicationAssessmentFeedbackResource() {
        return new ApplicationAssessmentFeedbackResourceBuilder(emptyList());
    }

    public ApplicationAssessmentFeedbackResourceBuilder(List<BiConsumer<Integer, ApplicationAssessmentFeedbackResource>> newActions) {
        super(newActions);
    }

    @Override
    protected ApplicationAssessmentFeedbackResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ApplicationAssessmentFeedbackResource>> actions) {
        return new ApplicationAssessmentFeedbackResourceBuilder(actions);
    }

    @Override
    protected ApplicationAssessmentFeedbackResource createInitial() {
        return new ApplicationAssessmentFeedbackResource();
    }

    public ApplicationAssessmentFeedbackResourceBuilder withFeedback(List<String>... feedback) {
        return withArraySetFieldByReflection("feedback", feedback);
    }
}
