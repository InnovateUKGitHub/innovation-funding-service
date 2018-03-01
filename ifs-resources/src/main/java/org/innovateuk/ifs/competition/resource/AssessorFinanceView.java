package org.innovateuk.ifs.competition.resource;

public enum AssessorFinanceView {
    OVERVIEW("Overview"),
    DETAILED("Detailed");

    String viewType;

    AssessorFinanceView(String viewType) {
        this.viewType = viewType;
    }

    public String getViewType() {
        return viewType;
    }
}