package org.innovateuk.ifs.application.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.assessment.resource.ApplicationAssessmentResource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.setField;

/**
 * Builder for {@link org.innovateuk.ifs.assessment.resource.ApplicationAssessmentResource}
 */
public class ApplicationAssessmentResourceBuilder extends BaseBuilder<ApplicationAssessmentResource, ApplicationAssessmentResourceBuilder> {

    private ApplicationAssessmentResourceBuilder(List<BiConsumer<Integer, ApplicationAssessmentResource>> newActions) {
        super(newActions);
    }

    public static ApplicationAssessmentResourceBuilder newApplicationAssessmentResource() {
        return new ApplicationAssessmentResourceBuilder(emptyList());
    }

    @Override
    protected ApplicationAssessmentResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ApplicationAssessmentResource>> actions) {
        return new ApplicationAssessmentResourceBuilder(actions);
    }

    @Override
    protected ApplicationAssessmentResource createInitial() {
        return new ApplicationAssessmentResource();
    }

    public ApplicationAssessmentResourceBuilder withTestId(Long id) {
        return with(applicationAssessmentResource -> setField("assessmentId", id, applicationAssessmentResource));
    }

    public ApplicationAssessmentResourceBuilder withApplicationId(Long id) {
        return with(applicationAssessmentResource -> setField("applicationId", id, applicationAssessmentResource));
    }

    public ApplicationAssessmentResourceBuilder withInScope(Boolean inScope) {
        return with(applicationAssessmentResource -> setField("inScope", inScope, applicationAssessmentResource));
    }

    public ApplicationAssessmentResourceBuilder withScores(Map<Long, BigDecimal> scores) {
        return with(applicationAssessmentResource -> setField("scores", scores, applicationAssessmentResource));
    }

    public ApplicationAssessmentResourceBuilder withFeedback(Map<Long, String> feedback) {
        return with(applicationAssessmentResource -> setField("feedback", feedback, applicationAssessmentResource));
    }

    public ApplicationAssessmentResourceBuilder withAveragePercentage(BigDecimal averagePercentage) {
        return with(applicationAssessmentResource -> setField("averagePercentage", averagePercentage, applicationAssessmentResource));
    }
}
