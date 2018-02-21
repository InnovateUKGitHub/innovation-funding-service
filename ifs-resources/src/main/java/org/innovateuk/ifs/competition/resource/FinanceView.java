package org.innovateuk.ifs.competition.resource;

public enum FinanceView {
    OVERVIEW("Overview"),
    DETAILED("Detailed");

    String viewType;

    FinanceView(String viewType) {
        this.viewType = viewType;
    }

    public String getViewType() {
        return viewType;
    }
}