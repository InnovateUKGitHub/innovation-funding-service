package org.innovateuk.ifs.management.funding.form;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.innovateuk.ifs.application.resource.FundingDecision;

import java.util.Optional;

/**
 * Contains the Funding Decision filter values.
 */

public class FundingDecisionFilterForm {
    private Optional<String>stringFilter  = Optional.empty();
    private Optional<Boolean> sendFilter = Optional.empty();
    private Optional<FundingDecision> fundingFilter = Optional.empty();
    private boolean eoi = false;

    public Optional<String> getStringFilter() {
        return stringFilter;
    }

    public void setStringFilter(Optional<String> stringFilter) {
        this.stringFilter = stringFilter;
    }

    public Optional<Boolean> getSendFilter() {
        return sendFilter;
    }

    public void setSendFilter(Optional<Boolean> sendFilter) {
        this.sendFilter = sendFilter;
    }

    public Optional<FundingDecision> getFundingFilter() {
        return fundingFilter;
    }

    public void setFundingFilter(Optional<FundingDecision> fundingFilter) {
        this.fundingFilter = fundingFilter;
    }

    public boolean isEoi() {
        return eoi;
    }

    public void setEoi(boolean eoi) {
        this.eoi = eoi;
    }

    public boolean anyFilterIsActive() {
        return this.fundingFilter.isPresent() || this.sendFilter.isPresent() || this.stringFilter.isPresent();
    }

    public void updateAllFilters(FundingDecisionFilterForm updatedFilterForm) {
        this.stringFilter = updatedFilterForm.stringFilter;
        this.sendFilter = updatedFilterForm.getSendFilter();
        this.fundingFilter = updatedFilterForm.getFundingFilter();
        this.eoi = updatedFilterForm.isEoi();
    }

    @JsonIgnore
    public String getStringFilterValue() {
        return stringFilter.orElse(null);
    }

    @JsonIgnore
    public FundingDecision getFundingFilterValue() {
        return fundingFilter.orElse(null);
    }
}
