package org.innovateuk.ifs.management.decision.form;

import org.innovateuk.ifs.application.resource.Decision;

import java.util.Optional;

public class FundingNotificationFilterForm {

    private int page = 0;
    private String stringFilter = "";
    private Optional<Boolean> sendFilter = Optional.empty();
    private Optional<Decision> fundingFilter = Optional.empty();
    private String sortField = "id";
    private boolean eoi;

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

    public Optional<Decision> getFundingFilter() {
        return fundingFilter;
    }

    public void setFundingFilter(Optional<Decision> fundingFilter) {
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

    public boolean isEoi() { return eoi; }

    public void setEoi(boolean eoi) { this.eoi = eoi; }

    public void setAllFilterOptions(String stringFilter, Optional<Boolean> sendFilter, Optional<Decision> fundingFilter, boolean eoi) {
        this.stringFilter = stringFilter;
        this.fundingFilter = fundingFilter;
        this.sendFilter = sendFilter;
        this.eoi = eoi;
    }

    public boolean anyFilterIsActive() {
        return (!this.stringFilter.isEmpty() ||
                this.sendFilter.isPresent() ||
                this.fundingFilter.isPresent());
    }
}
