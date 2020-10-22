package org.innovateuk.ifs.cofunder.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationResource;

public class CofunderResponseViewModel {

    private final long applicationId;
    private final String applicationName;
    private final boolean readonly;

    public CofunderResponseViewModel(ApplicationResource application, boolean readonly) {
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
