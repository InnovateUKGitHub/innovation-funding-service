package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.assessment.resource.ApplicationAssessmentAggregateResource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class ApplicationAssessmentAggregateResourceBuilder extends BaseBuilder<ApplicationAssessmentAggregateResource, ApplicationAssessmentAggregateResourceBuilder> {

    public static ApplicationAssessmentAggregateResourceBuilder newApplicationAssessmentAggregateResource() {
        return new ApplicationAssessmentAggregateResourceBuilder(emptyList());
    }

    public ApplicationAssessmentAggregateResourceBuilder(List<BiConsumer<Integer, ApplicationAssessmentAggregateResource>> newActions) {
        super(newActions);
    }

    @Override
    protected ApplicationAssessmentAggregateResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ApplicationAssessmentAggregateResource>> actions) {
        return new ApplicationAssessmentAggregateResourceBuilder(actions);
    }

    @Override
    protected ApplicationAssessmentAggregateResource createInitial() {
        return new ApplicationAssessmentAggregateResource();
    }

    public ApplicationAssessmentAggregateResourceBuilder withScopeAssessed(boolean... scopeAssessed) {
        return withArraySetFieldByReflection("scopeAssessed", scopeAssessed);
    }

    public ApplicationAssessmentAggregateResourceBuilder withTotalScope(Integer... totalScope) {
        return withArraySetFieldByReflection("totalScope", totalScope);
    }

    public ApplicationAssessmentAggregateResourceBuilder withInScope(Integer... inScope) {
        return withArraySetFieldByReflection("inScope", inScope);
    }

    public ApplicationAssessmentAggregateResourceBuilder withScores(Map<Long, BigDecimal>... scores) {
        return withArraySetFieldByReflection("scores", scores);
    }

    public ApplicationAssessmentAggregateResourceBuilder withAveragePercentage(BigDecimal... averagePercentage) {
        return withArraySetFieldByReflection("averagePercentage", averagePercentage);
    }
}
