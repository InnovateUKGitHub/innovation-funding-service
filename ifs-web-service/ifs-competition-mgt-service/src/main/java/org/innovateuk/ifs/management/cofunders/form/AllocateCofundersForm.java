package org.innovateuk.ifs.management.cofunders.form;

public class AllocateCofundersForm {
    private int page = 1;
    private String filter;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }
}
