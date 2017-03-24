package org.innovateuk.ifs.competition.form;

import org.innovateuk.ifs.application.resource.FundingDecision;

import javax.validation.constraints.Min;
import java.util.Optional;

public class ApplicationSummaryQueryForm {

    @Min(value = 0, message = "{validation.applicationsummaryqueryform.page.min}")
    private Integer page = 0;

    private String sort;

    private String tab;

    private String stringFilter = "";

    private Optional<FundingDecision> fundingFilter = Optional.empty();

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getTab() {
        return tab;
    }

    public void setTab(String tab) {
        this.tab = tab;
    }

    public String getStringFilter() {
        return stringFilter;
    }

    public void setStringFilter(String stringFilter) {
        this.stringFilter = stringFilter;
    }

    public Optional<FundingDecision> getFundingFilter() {
        return fundingFilter;
    }

    public void setFundingFilter(Optional<FundingDecision> fundingFilter) {
        this.fundingFilter = fundingFilter;
    }
}
