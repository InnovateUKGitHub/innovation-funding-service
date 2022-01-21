package org.innovateuk.ifs.project.finance.resource;

import org.innovateuk.ifs.competition.resource.FundingRules;

/**
 * A resource object to return finance check status for a partner organisation
 */
public class FinanceCheckPartnerStatusResource {
    private Long id;
    private String name;

    private boolean isLead;
    private ViabilityState viability;
    private ViabilityRagStatus viabilityRagStatus;
    private EligibilityState eligibility;
    private EligibilityRagStatus eligibilityRagStatus;
    private PaymentMilestoneState paymentMilestoneState;
    private FundingRulesState fundingRulesState;
    private FundingRules fundingRules;
    private boolean awaitingResponse;
    private boolean financeContactProvided;

    public FinanceCheckPartnerStatusResource() {
    }

    public FinanceCheckPartnerStatusResource(Long id, String name, boolean isLead, ViabilityState viability,
                                             ViabilityRagStatus viabilityRagStatus, EligibilityState eligibility,
                                             EligibilityRagStatus eligibilityRagStatus,
                                             PaymentMilestoneState paymentMilestoneState,
                                             FundingRulesState fundingRulesState,
                                             FundingRules fundingRules,
                                             boolean awaitingResponse,
                                             boolean financeContactProvided) {
        this.id = id;
        this.name = name;
        this.isLead = isLead;
        this.viability = viability;
        this.viabilityRagStatus = viabilityRagStatus;
        this.eligibility = eligibility;
        this.eligibilityRagStatus = eligibilityRagStatus;
        this.paymentMilestoneState = paymentMilestoneState;
        this.fundingRulesState = fundingRulesState;
        this.fundingRules = fundingRules;
        this.awaitingResponse = awaitingResponse;
        this.financeContactProvided = financeContactProvided;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isLead() {
        return isLead;
    }

    public void setLead(boolean lead) {
        isLead = lead;
    }

    public EligibilityState getEligibility() {
        return eligibility;
    }

    public void setEligibility(EligibilityState eligibility) {
        this.eligibility = eligibility;
    }

    public EligibilityRagStatus getEligibilityRagStatus() {
        return eligibilityRagStatus;
    }

    public void setEligibilityRagStatus(EligibilityRagStatus eligibilityRagStatus) {
        this.eligibilityRagStatus = eligibilityRagStatus;
    }

    public ViabilityState getViability() {
        return viability;
    }

    public void setViability(ViabilityState viability) {
        this.viability = viability;
    }

    public ViabilityRagStatus getViabilityRagStatus() {
        return viabilityRagStatus;
    }

    public void setViabilityRagStatus(ViabilityRagStatus viabilityRagStatus) {
        this.viabilityRagStatus = viabilityRagStatus;
    }

    public boolean isAwaitingResponse() {
        return awaitingResponse;
    }

    public void setAwaitingResponse(boolean awaitingResponse) {
        this.awaitingResponse = awaitingResponse;
    }

    public boolean isFinanceContactProvided() {
        return financeContactProvided;
    }

    public void setFinanceContactProvided(boolean financeContactProvided) {
        this.financeContactProvided = financeContactProvided;
    }

    public FundingRulesState getFundingRulesState() {
        return fundingRulesState;
    }

    public FundingRules getFundingRules() {
        return fundingRules;
    }

    public PaymentMilestoneState getPaymentMilestoneState() {
        return paymentMilestoneState;
    }

    public void setPaymentMilestoneState(PaymentMilestoneState paymentMilestoneState) {
        this.paymentMilestoneState = paymentMilestoneState;
    }

    public boolean isPaymentMilestoneApproved() {
        if (this.getPaymentMilestoneState() != null) {
            return this.getPaymentMilestoneState().isApproved();
        }
        return false;
    }

    public boolean isFinanceChecksApprovedNonProcurement() {
        return this.getEligibility().isApprovedOrNotApplicable()
                && this.getViability().isApprovedOrNotApplicable();
    }

    public boolean isFinanceChecksApprovedProcurement() {
        return this.getEligibility().isApprovedOrNotApplicable()
                && this.getViability().isApprovedOrNotApplicable()
                && this.isPaymentMilestoneApproved();
    }
}