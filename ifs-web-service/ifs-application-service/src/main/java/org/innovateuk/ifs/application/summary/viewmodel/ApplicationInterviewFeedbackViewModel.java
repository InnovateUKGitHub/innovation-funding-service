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

public class ApplicationInterviewFeedbackViewModel {

    private final ApplicationResource currentApplication;
    private final CompetitionResource currentCompetition;
    private final OrganisationResource leadOrganisation;
    private final List<OrganisationResource> applicationOrganisations;
    private final BigDecimal totalFundingSought;
    private final Map<Long, SectionResource> sections;
    private final Map<Long, List<QuestionResource>> sectionQuestions;
    private final ApplicationAssessmentAggregateResource scores;
    private final List<String> feedback;
    private final boolean hasFinanceSection;
    private final ApplicationFinanceSummaryViewModel applicationFinanceSummaryViewModel;
    private final ApplicationFundingBreakdownViewModel applicationFundingBreakdownViewModel;

    public ApplicationInterviewFeedbackViewModel(ApplicationResource currentApplication,
                                                 CompetitionResource currentCompetition,
                                                 OrganisationResource leadOrganisation,
                                                 List<OrganisationResource> applicationOrganisations,
                                                 BigDecimal totalFundingSought,
                                                 Map<Long, SectionResource> sections,
                                                 Map<Long, List<QuestionResource>> sectionQuestions,
                                                 ApplicationAssessmentAggregateResource scores,
                                                 List<String> feedback,
                                                 boolean hasFinanceSection,
                                                 ApplicationFinanceSummaryViewModel applicationFinanceSummaryViewModel,
                                                 ApplicationFundingBreakdownViewModel applicationFundingBreakdownViewModel) {
        this.currentApplication = currentApplication;
        this.currentCompetition = currentCompetition;
        this.leadOrganisation = leadOrganisation;
        this.applicationOrganisations = applicationOrganisations;
        this.totalFundingSought = totalFundingSought;
        this.sections = sections;
        this.sectionQuestions = sectionQuestions;
        this.scores = scores;
        this.feedback = feedback;
        this.hasFinanceSection = hasFinanceSection;
        this.applicationFinanceSummaryViewModel = applicationFinanceSummaryViewModel;
        this.applicationFundingBreakdownViewModel = applicationFundingBreakdownViewModel;
    }

    public ApplicationResource getCurrentApplication() {
        return currentApplication;
    }

    public CompetitionResource getCurrentCompetition() {
        return currentCompetition;
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

    public Map<Long, SectionResource> getSections() {
        return sections;
    }

    public Map<Long, List<QuestionResource>> getSectionQuestions() {
        return sectionQuestions;
    }

    public ApplicationAssessmentAggregateResource getScores() {
        return scores;
    }

    public List<String> getFeedback() {
        return feedback;
    }

    public boolean isHasFinanceSection() {
        return hasFinanceSection;
    }

    public ApplicationFinanceSummaryViewModel getApplicationFinanceSummaryViewModel() {
        return applicationFinanceSummaryViewModel;
    }

    public ApplicationFundingBreakdownViewModel getApplicationFundingBreakdownViewModel() {
        return applicationFundingBreakdownViewModel;
    }
}
