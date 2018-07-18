package org.innovateuk.ifs.management.application.view.viewmodel;

import org.innovateuk.ifs.application.common.viewmodel.SummaryViewModel;
import org.innovateuk.ifs.application.resource.AppendixResource;

import java.util.List;

public class ManageApplicationViewModel {

    private final SummaryViewModel summaryViewModel;
    private final String backUrl;
    private final String queryParams;
    private final boolean readOnly;
    private final boolean canReinstate;
    private final ApplicationOverviewIneligibilityViewModel ineligibility;
    private final List<AppendixResource> appendices;

    public ManageApplicationViewModel(SummaryViewModel summaryViewModel, String backUrl, String queryParams, boolean readOnly, boolean canReinstate, ApplicationOverviewIneligibilityViewModel ineligibility, List<AppendixResource> appendices) {
        this.summaryViewModel = summaryViewModel;
        this.backUrl = backUrl;
        this.queryParams = queryParams;
        this.readOnly = readOnly;
        this.canReinstate = canReinstate;
        this.ineligibility = ineligibility;
        this.appendices = appendices;
    }

    public SummaryViewModel getSummaryViewModel() {
        return summaryViewModel;
    }

    public String getBackUrl() {
        return backUrl;
    }

    public String getQueryParams() {
        return queryParams;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public boolean isCanReinstate() {
        return canReinstate;
    }

    public ApplicationOverviewIneligibilityViewModel getIneligibility() {
        return ineligibility;
    }

    public List<AppendixResource> getAppendices() {
        return appendices;
    }
}
