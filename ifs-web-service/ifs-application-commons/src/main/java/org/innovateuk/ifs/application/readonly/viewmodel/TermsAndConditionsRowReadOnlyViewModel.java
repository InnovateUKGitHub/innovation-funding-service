package org.innovateuk.ifs.application.readonly.viewmodel;

import org.innovateuk.ifs.competition.resource.FundingRules;

public class TermsAndConditionsRowReadOnlyViewModel {

    private final String partnerName;
    private final boolean lead;
    private final FundingRules fundingRules;
    private final long termsId;
    private final String termsName;
    private final boolean accepted;

    private TermsAndConditionsRowReadOnlyViewModel(String partnerName, boolean lead, FundingRules fundingRules, long termsId, String termsName, boolean accepted) {
        this.partnerName = partnerName;
        this.lead = lead;
        this.fundingRules = fundingRules;
        this.termsId = termsId;
        this.termsName = termsName;
        this.accepted = accepted;
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

    public long getTermsId() {
        return termsId;
    }

    public String getTermsName() {
        return termsName;
    }

    public boolean isAccepted() {
        return accepted;
    }


    public static final class TermsAndConditionsRowReadOnlyViewModelBuilder {
        private String partnerName;
        private boolean lead;
        private FundingRules fundingRules;
        private long termsId;
        private String termsName;
        private boolean accepted;

        private TermsAndConditionsRowReadOnlyViewModelBuilder() {
        }

        public static TermsAndConditionsRowReadOnlyViewModelBuilder aTermsAndConditionsRowReadOnlyViewModel() {
            return new TermsAndConditionsRowReadOnlyViewModelBuilder();
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

        public TermsAndConditionsRowReadOnlyViewModelBuilder withTermsId(long termsId) {
            this.termsId = termsId;
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
            return new TermsAndConditionsRowReadOnlyViewModel(partnerName, lead, fundingRules, termsId, termsName, accepted);
        }
    }
}