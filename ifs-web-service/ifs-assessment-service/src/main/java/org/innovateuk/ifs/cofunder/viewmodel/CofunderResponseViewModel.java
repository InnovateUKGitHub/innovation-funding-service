package org.innovateuk.ifs.cofunder.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationResource;

public class CofunderResponseViewModel {

    private final long applicationId;
    private final String applicationName;
    private final boolean readonly;
    private final long competitionId;

    public CofunderResponseViewModel(ApplicationResource application, boolean readonly) {
        this.applicationId = application.getId();
        this.applicationName = application.getName();
        this.readonly = readonly;
        this.competitionId = application.getCompetition();
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

    public long getCompetitionId() {
        return competitionId;
    }
}
