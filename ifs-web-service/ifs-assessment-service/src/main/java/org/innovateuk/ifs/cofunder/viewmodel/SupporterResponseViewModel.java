package org.innovateuk.ifs.supporter.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationResource;

public class SupporterResponseViewModel {

    private final long applicationId;
    private final String applicationName;
    private final boolean readonly;

    public SupporterResponseViewModel(ApplicationResource application, boolean readonly) {
        this.applicationId = application.getId();
        this.applicationName = application.getName();
        this.readonly = readonly;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public boolean isReadonly() {
        return readonly;
    }
}
