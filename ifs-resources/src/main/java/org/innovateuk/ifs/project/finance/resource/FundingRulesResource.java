package org.innovateuk.ifs.project.finance.resource;

import org.innovateuk.ifs.competition.resource.FundingRules;

import java.time.LocalDate;

public class FundingRulesResource {

    private FundingRulesState fundingRulesState;
    private FundingRules fundingRules;

    private String fundingRulesInternalUserFirstName;
    private String fundingRulesInternalUserLastName;
    private LocalDate fundingRulesLastModifiedDate;

    public FundingRulesResource() {
        // no-arg constructor
    }

    public FundingRulesResource(FundingRulesState fundingRulesState, FundingRules fundingRules) {
        this.fundingRulesState = fundingRulesState;
        this.fundingRules = fundingRules;
    }

    public FundingRulesState getFundingRulesState() {
        return fundingRulesState;
    }

    public void setFundingRulesState(FundingRulesState fundingRulesState) {
        this.fundingRulesState = fundingRulesState;
    }

    public FundingRules getFundingRules() {
        return fundingRules;
    }

    public void setFundingRules(FundingRules fundingRules) {
        this.fundingRules = fundingRules;
    }

    public String getFundingRulesInternalUserFirstName() {
        return fundingRulesInternalUserFirstName;
    }

    public void setFundingRulesInternalUserFirstName(String fundingRulesInternalUserFirstName) {
        this.fundingRulesInternalUserFirstName = fundingRulesInternalUserFirstName;
    }

    public String getFundingRulesInternalUserLastName() {
        return fundingRulesInternalUserLastName;
    }

    public void setFundingRulesInternalUserLastName(String fundingRulesInternalUserLastName) {
        this.fundingRulesInternalUserLastName = fundingRulesInternalUserLastName;
    }

    public LocalDate getFundingRulesLastModifiedDate() {
        return fundingRulesLastModifiedDate;
    }

    public void setFundingRulesLastModifiedDate(LocalDate fundingRulesLastModifiedDate) {
        this.fundingRulesLastModifiedDate = fundingRulesLastModifiedDate;
    }
}
