package org.innovateuk.ifs.application.common.viewmodel;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Model attributes for the application terms partner view.
 */
public class ApplicationTermsPartnerViewModel {
    private final long applicationId;
    private final long questionId;
    private final List<ApplicationTermsPartnerRowViewModel> partners;

    public ApplicationTermsPartnerViewModel(long applicationId, long questionId, List<ApplicationTermsPartnerRowViewModel> partnerStatus) {
        this.applicationId = applicationId;
        this.partners = partnerStatus;
        this.questionId = questionId;
    }

    public long getApplicationId() {
        return applicationId;
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