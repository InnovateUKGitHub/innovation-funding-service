package org.innovateuk.ifs.application.summary.viewmodel;

import org.innovateuk.ifs.analytics.BaseAnalyticsViewModel;
import org.innovateuk.ifs.application.readonly.viewmodel.ApplicationReadOnlyViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;

import java.time.LocalDate;

public class ApplicationSummaryViewModel implements BaseAnalyticsViewModel {
    private final ApplicationReadOnlyViewModel applicationReadOnlyViewModel;
    private final ApplicationResource application;
    private final CompetitionResource competition;
    private final boolean projectWithdrawn;

    private final String competitionName;
    private final String applicationName;
    private final LocalDate startDate;
    private final Long duration;
    private final Boolean resubmission;
    private final boolean canSelectInnovationArea;
    private final String innovationAreaName;
    private final String previousApplicationNumber;
    private final String previousApplicationTitle;
    private final boolean ktpCompetition;

    public ApplicationSummaryViewModel(ApplicationReadOnlyViewModel applicationReadOnlyViewModel, ApplicationResource application, CompetitionResource competition, boolean projectWithdrawn) {
        this.applicationReadOnlyViewModel = applicationReadOnlyViewModel;
        this.application = application;
        this.competition = competition;
        this.projectWithdrawn = projectWithdrawn;
        this.competitionName = competition.getName();
        this.applicationName = application.getName();
        this.startDate = application.getStartDate();
        this.duration = application.getDurationInMonths();
        this.resubmission = application.getResubmission();
        this.canSelectInnovationArea = competition.getInnovationAreas().size() > 1;
        this.innovationAreaName = application.getInnovationArea().getName();
        this.previousApplicationNumber = application.getPreviousApplicationNumber();
        this.previousApplicationTitle = application.getPreviousApplicationTitle();
        this.ktpCompetition = competition.isKtp();
    }

    @Override
    public Long getApplicationId() {
        return application.getId();
    }

    public ApplicationReadOnlyViewModel getApplicationReadOnlyViewModel() {
        return applicationReadOnlyViewModel;
    }

    public ApplicationResource getApplication() {
        return application;
    }

    public CompetitionResource getCompetition() {
        return competition;
    }

    public boolean isProjectWithdrawn() {
        return projectWithdrawn;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public Long getDuration() {
        return duration;
    }

    public Boolean getResubmission() {
        return resubmission;
    }

    public boolean isCanSelectInnovationArea() {
        return canSelectInnovationArea;
    }

    public String getInnovationAreaName() {
        return innovationAreaName;
    }

    public String getPreviousApplicationNumber() {
        return previousApplicationNumber;
    }

    public String getPreviousApplicationTitle() {
        return previousApplicationTitle;
    }

    public boolean isKtpCompetition() {
        return ktpCompetition;
    }
}
