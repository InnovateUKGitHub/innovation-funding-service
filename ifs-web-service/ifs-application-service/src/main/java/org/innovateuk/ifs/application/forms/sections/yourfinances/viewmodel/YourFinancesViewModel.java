package org.innovateuk.ifs.application.forms.sections.yourfinances.viewmodel;

public class YourFinancesViewModel {

    private final String title;
    private final String url;
    private final boolean complete;

    public YourFinancesViewModel(String title, String url, boolean complete) {
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
