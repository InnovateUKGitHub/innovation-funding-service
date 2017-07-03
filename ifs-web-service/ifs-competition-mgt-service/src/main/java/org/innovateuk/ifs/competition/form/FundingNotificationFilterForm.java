package org.innovateuk.ifs.competition.form;

import org.innovateuk.ifs.application.resource.FundingDecision;

import java.util.Optional;

public class FundingNotificationFilterForm {

    private int page = 0;
    private String stringFilter = "";
    private Optional<Boolean> sendFilter = Optional.empty();
    private Optional<FundingDecision> fundingFilter = Optional.empty();
    private String sortField = "id";

    public String getStringFilter() {
        return stringFilter;
    }

    public void setStringFilter(String stringFilter) {
        this.stringFilter = stringFilter;
    }

    public String getSortField() {
        return sortField;
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

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setAllFilterOptions(String stringFilter, Optional<Boolean> sendFilter, Optional<FundingDecision> fundingFilter) {
        this.stringFilter = stringFilter;
        this.fundingFilter = fundingFilter;
        this.sendFilter = sendFilter;

    }

    public boolean anyFilterIsActive() {
        return (!this.stringFilter.isEmpty() ||
                this.sendFilter.isPresent() ||
                this.fundingFilter.isPresent());
    }
}
