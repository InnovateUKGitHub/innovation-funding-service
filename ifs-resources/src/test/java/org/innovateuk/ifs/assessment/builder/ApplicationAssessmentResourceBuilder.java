package org.innovateuk.ifs.assessment.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.assessment.resource.AssessmentState;
import org.innovateuk.ifs.assessment.resource.dashboard.ApplicationAssessmentResource;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class ApplicationAssessmentResourceBuilder extends BaseBuilder<ApplicationAssessmentResource, ApplicationAssessmentResourceBuilder> {

    public static ApplicationAssessmentResourceBuilder newApplicationAssessmentResource() {
        return new ApplicationAssessmentResourceBuilder(emptyList());
    }

    protected ApplicationAssessmentResourceBuilder(List<BiConsumer<Integer, ApplicationAssessmentResource>> newActions) {
        super(newActions);
    }

    @Override
    protected ApplicationAssessmentResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ApplicationAssessmentResource>> actions) {
        return new ApplicationAssessmentResourceBuilder(actions);
    }

    @Override
    protected ApplicationAssessmentResource createInitial() {
        return new ApplicationAssessmentResource();
    }

    public ApplicationAssessmentResourceBuilder withApplicationId(Long... applicationIds) {
        return withArraySetFieldByReflection("applicationId", applicationIds);
    }

    public ApplicationAssessmentResourceBuilder withAssessmentId(Long... assessmentIds) {
        return withArraySetFieldByReflection("assessmentId", assessmentIds);
    }

    public ApplicationAssessmentResourceBuilder withCompetitionName(String... competitionNames) {
        return withArraySetFieldByReflection("competitionName", competitionNames);
    }

    public ApplicationAssessmentResourceBuilder withLeadOrganisation(String... leadOrganisations) {
        return withArraySetFieldByReflection("leadOrganisation", leadOrganisations);
    }

    public ApplicationAssessmentResourceBuilder withState(AssessmentState... states) {
        return withArraySetFieldByReflection("state", states);
    }

    public ApplicationAssessmentResourceBuilder withOverallScore(Integer... overallScore) {
        return withArraySetFieldByReflection("overallScore", overallScore);
    }

    public ApplicationAssessmentResourceBuilder withRecommended(Boolean... recommended) {
        return withArraySetFieldByReflection("recommended", recommended);
    }
}
