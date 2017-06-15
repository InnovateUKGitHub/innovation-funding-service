package org.innovateuk.ifs.competition.form;

import org.hibernate.validator.constraints.NotEmpty;

import java.util.List;

/**
 * Form bean used to encapsulate information needed to make funding decisions.
 */
public class FundingDecisionForm {
    @NotEmpty
    private String fundingDecision;

    private List<Long> applicationIds;

    private boolean allSelected;

    public String getFundingDecision() {
        return fundingDecision;
    }

    public void setFundingDecision(String fundingDecision) {
        this.fundingDecision = fundingDecision;
    }

    public List<Long> getApplicationIds() {
        return applicationIds;
    }

    public void setApplicationIds(List<Long> applicationIds) {
        this.applicationIds = applicationIds;
    }

    public boolean isAllSelected() {
        return allSelected;
    }

    public void setAllSelected(boolean allSelected) {
        this.allSelected = allSelected;
    }
}
