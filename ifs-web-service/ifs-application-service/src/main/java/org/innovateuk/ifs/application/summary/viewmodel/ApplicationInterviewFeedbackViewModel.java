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

    final static String LEAD_WITH_RESPONSE_BANNER =  "Your response has been uploaded." +
            " This response will be noted by the interview panel.";
    final static String LEAD_WITHOUT_RESPONSE_BANNER =  "As the lead applicant you can respond to feedback." +
            " This response will be noted by the interview panel.";
    final static String COLLAB_WITH_RESPONSE_BANNER =  "The lead applicant has responded to feedback." +
            " This response will be noted by the interview panel.";
    final static String COLLAB_WITHOUT_RESPONSE_BANNER =  "The lead applicant can respond to feedback." +
            " This response will be noted by the interview panel.";

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
    private final String feedbackFilename;
    private final String responseFilename;
    private final boolean isLeadApplicant;
    private final boolean feedbackReleased;
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
                                                 String feedbackFilename,
                                                 String responseFilename,
                                                 boolean isLeadApplicant,
                                                 boolean feedbackReleased,
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
        this.feedbackFilename = feedbackFilename;
        this.responseFilename = responseFilename;
        this.isLeadApplicant = isLeadApplicant;
        this.feedbackReleased = feedbackReleased;
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

    public String getFeedbackFilename() {
        return feedbackFilename;
    }

    public String getResponseFilename() {
        return responseFilename;
    }

    public boolean isLeadApplicant() {
        return isLeadApplicant;
    }

    public boolean isFeedbackReleased() {
        return feedbackReleased;
    }

    public ApplicationFinanceSummaryViewModel getApplicationFinanceSummaryViewModel() {
        return applicationFinanceSummaryViewModel;
    }

    public ApplicationFundingBreakdownViewModel getApplicationFundingBreakdownViewModel() {
        return applicationFundingBreakdownViewModel;
    }

    /* View logic methods. */

    public boolean hasResponse() {
        return responseFilename != null;
    }

    public boolean hasFeedback() {
        return feedbackFilename != null;
    }

    public boolean isResponseSectionEnabled() {
        return !feedbackReleased || hasResponse();
    }

    public String getBannerText() {
            if (isLeadApplicant()) {
                if (hasResponse()) {
                    return LEAD_WITH_RESPONSE_BANNER;
                } else {
                    return LEAD_WITHOUT_RESPONSE_BANNER;
                }
            } else {
                if (hasResponse()) {
                    return COLLAB_WITH_RESPONSE_BANNER;
                } else {
                    return COLLAB_WITHOUT_RESPONSE_BANNER;
                }
            }
    }
}
