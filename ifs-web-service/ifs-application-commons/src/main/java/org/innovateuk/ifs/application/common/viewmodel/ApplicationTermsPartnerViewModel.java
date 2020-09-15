package org.innovateuk.ifs.application.common.viewmodel;

import org.innovateuk.ifs.analytics.BaseAnalyticsViewModel;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Model attributes for the application terms partner view.
 */
public class ApplicationTermsPartnerViewModel implements BaseAnalyticsViewModel {
    private final long applicationId;
    private final String competitionName;
    private final long questionId;
    private final List<ApplicationTermsPartnerRowViewModel> partners;

    public ApplicationTermsPartnerViewModel(long applicationId, String competitionName, long questionId, List<ApplicationTermsPartnerRowViewModel> partnerStatus) {
        this.applicationId = applicationId;
        this.competitionName = competitionName;
        this.partners = partnerStatus;
        this.questionId = questionId;
    }

    @Override
    public Long getApplicationId() {
        return applicationId;
    }

    @Override
    public String getCompetitionName() {
        return competitionName;
    }

    public long getQuestionId() {
        return questionId;
    }

    public List<ApplicationTermsPartnerRowViewModel> getPartners() {
        return partners;
    }

    public List<ApplicationTermsPartnerRowViewModel> getNonAcceptedPartners() {
        return getPartners()
                .stream()
                .filter(p -> !p.isTermsAccepted())
                .collect(toList());
    }
}