package org.innovateuk.ifs.supporter.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.resource.CompetitionStatus;

public class SupporterResponseViewModel {

    private final long applicationId;
    private final String applicationName;
    private final boolean canEdit;
    private final boolean readonly;
    private final long competitionId;

    public SupporterResponseViewModel(ApplicationResource application, boolean readonly) {
        this.applicationId = application.getId();
        this.applicationName = application.getName();
        this.canEdit = !application.getCompetitionStatus().isLaterThan(CompetitionStatus.IN_ASSESSMENT);
        this.readonly = readonly;
        this.competitionId = application.getCompetition();
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

    public long getCompetitionId() {
        return competitionId;
    }
}
