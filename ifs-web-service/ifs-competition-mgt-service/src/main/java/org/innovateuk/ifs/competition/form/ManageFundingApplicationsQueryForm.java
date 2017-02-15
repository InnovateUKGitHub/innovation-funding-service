package org.innovateuk.ifs.competition.form;

public class ManageFundingApplicationsQueryForm {

    private int page = 1;
    private String filter = "";
    private String sortField = "id";

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getSortField() {
        return sortField;
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
}
