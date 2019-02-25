package org.innovateuk.ifs.application.forms.questions.grantagreement.model;

public class GrantAgreementViewModel {

    private final long applicationId;
    private final String applicationName;

    public GrantAgreementViewModel(long applicationId, String applicationName) {
        this.applicationId = applicationId;
        this.applicationName = applicationName;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public long getApplicationId() {
        return applicationId;
    }
}
