package org.innovateuk.ifs.competition.form;

import org.innovateuk.ifs.application.resource.FundingDecision;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

public class SelectApplicationsForEmailForm {

    @NotNull
    private List<String> ids;
    private boolean allSelected;
    private String stringFilter;
    private Boolean sendFilter;
    private FundingDecision fundingFilter;

    public SelectApplicationsForEmailForm() {
        this.ids = new ArrayList<>();
    }

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }

    public boolean isAllSelected() {
        return allSelected;
    }

    public void setAllSelected(boolean allSelected) {
        this.allSelected = allSelected;
    }

    public String getStringFilter() {
        return stringFilter;
    }

    public void setStringFilter(String stringFilter) {
        this.stringFilter = stringFilter;
    }

    public Boolean getSendFilter() {
        return sendFilter;
    }

    public void setSendFilter(Boolean sendFilter) {
        this.sendFilter = sendFilter;
    }

    public FundingDecision getFundingFilter() {
        return fundingFilter;
    }

    public void setFundingFilter(FundingDecision fundingFilter) {
        this.fundingFilter = fundingFilter;
    }
}
