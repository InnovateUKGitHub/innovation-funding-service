package org.innovateuk.ifs.application.common.viewmodel;

import java.util.List;

/**
 * Model attributes for the application terms view.
 */
public class ApplicationTermsPartnerViewModel {
    private final long applicationId;
    private final List<ApplicationTermsPartnerRowViewModel> partners;

    public ApplicationTermsPartnerViewModel(long applicationId, List<ApplicationTermsPartnerRowViewModel> partnerStatus) {
        this.applicationId = applicationId;
        this.partners = partnerStatus;
    }
}