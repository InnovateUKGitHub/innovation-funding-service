package org.innovateuk.ifs.competition.form;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.innovateuk.ifs.application.resource.FundingDecision;

import java.util.Optional;

/**
 * Contains the Funding Decision filter values.
 */

public class FundingDecisionFilterForm {
    private Optional<String>stringFilter  = Optional.empty();
    private Optional<FundingDecision> fundingFilter = Optional.empty();

    public Optional<String> getStringFilter() {
        return stringFilter;
    }

    public void setStringFilter(Optional<String> stringFilter) {
        this.stringFilter = stringFilter;
    }

    public Optional<FundingDecision> getFundingFilter() {
        return fundingFilter;
    }

    public void setFundingFilter(Optional<FundingDecision> fundingFilter) {
        this.fundingFilter = fundingFilter;
    }

    public boolean anyFilterIsActive() {
        return this.fundingFilter.isPresent() || this.stringFilter.isPresent();
    }

    public void updateAllFilters(FundingDecisionFilterForm updatedFilterForm) {
        this.stringFilter = updatedFilterForm.stringFilter;
        this.fundingFilter = updatedFilterForm.getFundingFilter();
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
