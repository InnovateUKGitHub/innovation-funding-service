package org.innovateuk.ifs.application.readonly.viewmodel;

import org.innovateuk.ifs.competition.resource.FundingRules;

public class TermsAndConditionsRowReadOnlyViewModel {

    private final long partnerId;
    private final String partnerName;
    private final boolean lead;
    private final FundingRules fundingRules;
    private final String termsName;
    private final boolean accepted;

    public TermsAndConditionsRowReadOnlyViewModel(long partnerId, String partnerName, boolean lead, FundingRules fundingRules, String termsName, boolean accepted) {
        this.partnerId = partnerId;
        this.partnerName = partnerName;
        this.lead = lead;
        this.fundingRules = fundingRules;
        this.termsName = termsName;
        this.accepted = accepted;
    }

    public long getPartnerId() {
        return partnerId;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public boolean isLead() {
        return lead;
    }

    public FundingRules getFundingRules() {
        return fundingRules;
    }

    public String getTermsName() {
        return termsName;
    }

    public boolean isAccepted() {
        return accepted;
    }


    public static final class TermsAndConditionsRowReadOnlyViewModelBuilder {
        private long partnerId;
        private String partnerName;
        private boolean lead;
        private FundingRules fundingRules;
        private String termsName;
        private boolean accepted;

        private TermsAndConditionsRowReadOnlyViewModelBuilder() {
        }

        public static TermsAndConditionsRowReadOnlyViewModelBuilder aTermsAndConditionsRowReadOnlyViewModel() {
            return new TermsAndConditionsRowReadOnlyViewModelBuilder();
        }

        public TermsAndConditionsRowReadOnlyViewModelBuilder withPartnerId(long partnerId) {
            this.partnerId = partnerId;
            return this;
        }

        public TermsAndConditionsRowReadOnlyViewModelBuilder withPartnerName(String partnerName) {
            this.partnerName = partnerName;
            return this;
        }

        public TermsAndConditionsRowReadOnlyViewModelBuilder withLead(boolean lead) {
            this.lead = lead;
            return this;
        }

        public TermsAndConditionsRowReadOnlyViewModelBuilder withFundingRules(FundingRules fundingRules) {
            this.fundingRules = fundingRules;
            return this;
        }

        public TermsAndConditionsRowReadOnlyViewModelBuilder withTermsName(String termsName) {
            this.termsName = termsName;
            return this;
        }

        public TermsAndConditionsRowReadOnlyViewModelBuilder withAccepted(boolean accepted) {
            this.accepted = accepted;
            return this;
        }

        public TermsAndConditionsRowReadOnlyViewModel build() {
            return new TermsAndConditionsRowReadOnlyViewModel(partnerId, partnerName, lead, fundingRules, termsName, accepted);
        }
    }
}