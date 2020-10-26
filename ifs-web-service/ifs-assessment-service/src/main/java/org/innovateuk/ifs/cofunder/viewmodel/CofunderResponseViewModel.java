package org.innovateuk.ifs.cofunder.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;

public class CofunderResponseViewModel {

    private final long applicationId;
    private final String applicationName;
    private final boolean canEdit;
    private final boolean readonly;

    public CofunderResponseViewModel(ApplicationResource application, boolean readonly) {
        this.applicationId = application.getId();
        this.applicationName = application.getName();
        this.canEdit = !application.getCompetitionStatus().isLaterThan(CompetitionStatus.IN_ASSESSMENT);
        this.readonly = readonly;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public boolean isCanEdit() {
        return canEdit;
    }

    public boolean isReadonly() {
        return readonly;
    }
}
