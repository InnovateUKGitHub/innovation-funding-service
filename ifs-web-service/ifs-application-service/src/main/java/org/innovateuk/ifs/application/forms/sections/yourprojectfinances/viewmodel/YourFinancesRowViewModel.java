package org.innovateuk.ifs.application.forms.sections.yourprojectfinances.viewmodel;

public class YourFinancesRowViewModel {

    private final String title;
    private final String url;
    private final boolean complete;
    private final boolean enabledForPreRegistration;

    public YourFinancesRowViewModel(String title, String url, boolean complete, boolean enabledForPreRegistration) {
        this.title = title;
        this.url = url;
        this.complete = complete;
        this.enabledForPreRegistration = enabledForPreRegistration;
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

    public boolean isEnabledForPreRegistration() {
        return enabledForPreRegistration;
    }
}
