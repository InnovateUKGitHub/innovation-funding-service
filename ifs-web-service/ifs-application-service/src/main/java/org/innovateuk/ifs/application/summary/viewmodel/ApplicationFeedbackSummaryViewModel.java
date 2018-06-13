package org.innovateuk.ifs.application.summary.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.assessment.resource.ApplicationAssessmentAggregateResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class ApplicationFeedbackSummaryViewModel {

    private final ApplicationResource application;
    private final CompetitionResource competition;
    private final OrganisationResource leadOrganisation;
    private final List<OrganisationResource> applicationOrganisations;
    private final BigDecimal totalFundingSought;
    private final List<String> feedback;
    private final boolean hasFinanceSection;
    private final Map<Long, SectionResource> sections;
    private final Map<Long, List<QuestionResource>> sectionQuestions;
    private final ApplicationAssessmentAggregateResource scores;
    private final ApplicationFinanceSummaryViewModel applicationFinanceSummaryViewModel;
    private final ApplicationFundingBreakdownViewModel applicationFundingBreakdownViewModel;
    private final String backUrl;
    private final String origin;


    public ApplicationFeedbackSummaryViewModel(ApplicationResource application,
                                               CompetitionResource competition,
                                               OrganisationResource leadOrganisation,
                                               List<OrganisationResource> applicationOrganisations,
                                               BigDecimal totalFundingSought,
                                               List<String> feedback,
                                               boolean hasFinanceSection,
                                               Map<Long, SectionResource> sections,
                                               Map<Long, List<QuestionResource>> sectionQuestions,
                                               ApplicationAssessmentAggregateResource scores,
                                               ApplicationFinanceSummaryViewModel applicationFinanceSummaryViewModel,
                                               ApplicationFundingBreakdownViewModel applicationFundingBreakdownViewModel,
                                               String backUrl,
                                               String origin) {
        this.application = application;
        this.competition = competition;
        this.leadOrganisation = leadOrganisation;
        this.applicationOrganisations = applicationOrganisations;
        this.totalFundingSought = totalFundingSought;
        this.feedback = feedback;
        this.hasFinanceSection = hasFinanceSection;
        this.sections = sections;
        this.sectionQuestions = sectionQuestions;
        this.scores = scores;
        this.applicationFinanceSummaryViewModel = applicationFinanceSummaryViewModel;
        this.applicationFundingBreakdownViewModel = applicationFundingBreakdownViewModel;
        this.backUrl = backUrl;
        this.origin = origin;
    }

    public ApplicationResource getApplication() {
        return application;
    }

    public CompetitionResource getCompetition() {
        return competition;
    }

    public OrganisationResource getLeadOrganisation() {
        return leadOrganisation;
    }

    public List<OrganisationResource> getApplicationOrganisations() {
        return applicationOrganisations;
    }

    public BigDecimal getTotalFundingSought() {
        return totalFundingSought;
    }

    public List<String> getFeedback() {
        return feedback;
    }

    public boolean isHasFinanceSection() {
        return hasFinanceSection;
    }

    public Map<Long, SectionResource> getSections() {
        return sections;
    }

    public Map<Long, List<QuestionResource>> getSectionQuestions() {
        return sectionQuestions;
    }

    public ApplicationAssessmentAggregateResource getScores() {
        return scores;
    }

    public ApplicationFinanceSummaryViewModel getApplicationFinanceSummaryViewModel() {
        return applicationFinanceSummaryViewModel;
    }

    public ApplicationFundingBreakdownViewModel getApplicationFundingBreakdownViewModel() {
        return applicationFundingBreakdownViewModel;
    }

    public String getBackUrl() {
        return backUrl;
    }

    public String getOrigin() {
        return origin;
    }
}
