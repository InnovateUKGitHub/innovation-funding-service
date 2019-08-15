package org.innovateuk.ifs.application.forms.sections.yourprojectfinances.viewmodel;

public class YourFinancesRowViewModel {

    private final String title;
    private final String url;
    private final boolean complete;

    public YourFinancesRowViewModel(String title, String url, boolean complete) {
        this.title = title;
        this.url = url;
        this.complete = complete;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public boolean isComplete() {
        return complete;
    }
}
