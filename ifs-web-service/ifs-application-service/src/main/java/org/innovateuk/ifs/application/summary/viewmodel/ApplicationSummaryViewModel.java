package org.innovateuk.ifs.application.summary.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;

import java.time.LocalDate;

public class ApplicationSummaryViewModel {
    private final ApplicationRowsSummaryViewModel applicationSummaryViewModel;
    private final ApplicationResource application;
    private final CompetitionResource competition;
    private final boolean projectWithdrawn;
    private final boolean support;

    private final String competitionName;
    private final String applicationName;
    private final LocalDate startDate;
    private final Long duration;
    private final Boolean resubmission;
    private final boolean canSelectInnovationArea;
    private final String innovationAreaName;
    private final String previousApplicationNumber;
    private final String previousApplicationTitle;

    public ApplicationSummaryViewModel(ApplicationRowsSummaryViewModel applicationSummaryViewModel, ApplicationResource application, CompetitionResource competition, boolean projectWithdrawn, boolean support) {
        this.applicationSummaryViewModel = applicationSummaryViewModel;
        this.application = application;
        this.competition = competition;
        this.projectWithdrawn = projectWithdrawn;
        this.support = support;
        this.competitionName = competition.getName();
        this.applicationName = application.getName();
        this.startDate = application.getStartDate();
        this.duration = application.getDurationInMonths();
        this.resubmission = application.getResubmission();
        this.canSelectInnovationArea = competition.getInnovationAreas().size() > 1;
        this.innovationAreaName = application.getInnovationArea().getName();
        this.previousApplicationNumber = application.getPreviousApplicationNumber();
        this.previousApplicationTitle = application.getPreviousApplicationTitle();
    }

    public ApplicationRowsSummaryViewModel getApplicationSummaryViewModel() {
        return applicationSummaryViewModel;
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

    public boolean isSupport() {
        return support;
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
}
