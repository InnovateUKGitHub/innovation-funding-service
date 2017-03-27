package org.innovateuk.ifs.application.builder;

import org.innovateuk.ifs.BaseBuilder;
import org.innovateuk.ifs.application.resource.ApplicationAssessmentSummaryResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;

import java.util.List;
import java.util.function.BiConsumer;

import static java.util.Collections.emptyList;

public class ApplicationAssessmentSummaryResourceBuilder extends BaseBuilder<ApplicationAssessmentSummaryResource, ApplicationAssessmentSummaryResourceBuilder> {

    private ApplicationAssessmentSummaryResourceBuilder(List<BiConsumer<Integer, ApplicationAssessmentSummaryResource>> newMultiActions) {
        super(newMultiActions);
    }

    @Override
    protected ApplicationAssessmentSummaryResourceBuilder createNewBuilderWithActions(List<BiConsumer<Integer, ApplicationAssessmentSummaryResource>> actions) {
        return new ApplicationAssessmentSummaryResourceBuilder(actions);
    }

    @Override
    protected ApplicationAssessmentSummaryResource createInitial() {
        return new ApplicationAssessmentSummaryResource();
    }

    public static ApplicationAssessmentSummaryResourceBuilder newApplicationAssessmentSummaryResource() {
        return new ApplicationAssessmentSummaryResourceBuilder(emptyList());
    }

    public ApplicationAssessmentSummaryResourceBuilder withId(Long... value) {
        return withArraySetFieldByReflection("id", value);
    }

    public ApplicationAssessmentSummaryResourceBuilder withName(String... value) {
        return withArraySetFieldByReflection("name", value);
    }

    public ApplicationAssessmentSummaryResourceBuilder withInnovationArea(String... value) {
        return withArraySetFieldByReflection("innovationArea", value);
    }

    public ApplicationAssessmentSummaryResourceBuilder withCompetitionId(Long... value) {
        return withArraySetFieldByReflection("competitionId", value);
    }

    public ApplicationAssessmentSummaryResourceBuilder withCompetitionName(String... value) {
        return withArraySetFieldByReflection("competitionName", value);
    }

    public ApplicationAssessmentSummaryResourceBuilder withLeadOrganisation(String... value) {
        return withArraySetFieldByReflection("leadOrganisation", value);
    }

    public ApplicationAssessmentSummaryResourceBuilder withPartnerOrganisations(List<String>... value) {
        return withArraySetFieldByReflection("partnerOrganisations", value);
    }

    public ApplicationAssessmentSummaryResourceBuilder withCompetitionStatus(CompetitionStatus... value) {
        return withArraySetFieldByReflection("competitionStatus", value);
    }
}