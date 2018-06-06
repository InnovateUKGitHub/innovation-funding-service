package org.innovateuk.ifs.application.summary.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.FormInputResponseResource;
import org.innovateuk.ifs.assessment.resource.ApplicationAssessmentAggregateResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class ApplicationInterviewSummaryViewModel {
    final static String ASSESSOR_WITH_RESPONSE_BANNER =  "The lead applicant has responded to feedback." +
            " Download and review all attachments before the interview panel.";
    final static String ASSESSOR_WITHOUT_RESPONSE_BANNER =  "The lead applicant can respond to feedback." +
            " This response will be noted by the interview panel.";

    private final ApplicationResource application;
    private final CompetitionResource competition;
    private final String responseFilename;
    private final String feedbackFilename;
    private final OrganisationResource leadOrganisation;
    private final List<OrganisationResource> partners;
    private final Map<Long, FormInputResponseResource> responses;
    private final Map<Long, SectionResource> sections;
    private final Map<Long, List<QuestionResource>> sectionQuestions;
    private final ApplicationAssessmentAggregateResource scores;
    private final List<String> feedback;
    private final boolean hasFinanceSection;
    private final Long financeSectionId;
    private final Long eachCollaboratorFinanceSectionId;
    private final BigDecimal totalFundingSought;
    private final ApplicationFinanceSummaryViewModel applicationFinanceSummaryViewModel;

    public ApplicationInterviewSummaryViewModel(ApplicationResource application,
                                                CompetitionResource competition,
                                                String responseFilename,
                                                String feedbackFilename,
                                                OrganisationResource leadOrganisation,
                                                List<OrganisationResource> partners,
                                                Map<Long, FormInputResponseResource> responses,
                                                Map<Long, SectionResource> sections,
                                                Map<Long, List<QuestionResource>> sectionQuestions,
                                                ApplicationAssessmentAggregateResource scores,
                                                List<String> feedback,
                                                boolean hasFinanceSection,
                                                Long financeSectionId,
                                                Long eachCollaboratorFinanceSectionId,
                                                BigDecimal totalFundingSought,
                                                ApplicationFinanceSummaryViewModel applicationFinanceSummaryViewModel) {
        this.application = application;
        this.competition = competition;
        this.responseFilename = responseFilename;
        this.feedbackFilename = feedbackFilename;
        this.leadOrganisation = leadOrganisation;
        this.partners = partners;
        this.responses = responses;
        this.sections = sections;
        this.sectionQuestions = sectionQuestions;
        this.scores = scores;
        this.feedback = feedback;
        this.hasFinanceSection = hasFinanceSection;
        this.financeSectionId = financeSectionId;
        this.eachCollaboratorFinanceSectionId = eachCollaboratorFinanceSectionId;
        this.totalFundingSought = totalFundingSought;
        this.applicationFinanceSummaryViewModel = applicationFinanceSummaryViewModel;
    }

    public ApplicationResource getApplication() {
        return application;
    }

    public CompetitionResource getCompetition() {
        return competition;
    }

    public String getResponseFilename() {
        return responseFilename;
    }

    public String getFeedbackFilename() {
        return feedbackFilename;
    }

    public OrganisationResource getLeadOrganisation() {
        return leadOrganisation;
    }

    public List<OrganisationResource> getPartners() {
        return partners;
    }

    public Map<Long, FormInputResponseResource> getResponses() {
        return responses;
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

    public Long getFinanceSectionId() {
        return financeSectionId;
    }

    public Long getEachCollaboratorFinanceSectionId() {
        return eachCollaboratorFinanceSectionId;
    }

    public BigDecimal getTotalFundingSought() {
        return totalFundingSought;
    }

    public ApplicationFinanceSummaryViewModel getApplicationFinanceSummaryViewModel() {
        return applicationFinanceSummaryViewModel;
    }

    /* View logic methods. */
    public boolean hasResponse() {
        return responseFilename != null;
    }

    public boolean hasFeedback() {
        return feedbackFilename != null;
    }

    public String getBannerText() {
        if (hasResponse()) {
            return ASSESSOR_WITH_RESPONSE_BANNER;
        } else {
            return ASSESSOR_WITHOUT_RESPONSE_BANNER;
        }
    }
}
