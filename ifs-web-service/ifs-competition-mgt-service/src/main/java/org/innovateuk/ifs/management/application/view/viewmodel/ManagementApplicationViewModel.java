package org.innovateuk.ifs.management.application.view.viewmodel;

import org.innovateuk.ifs.application.readonly.viewmodel.ApplicationReadOnlyViewModel;
import org.innovateuk.ifs.application.resource.AppendixResource;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;

import java.time.LocalDate;
import java.util.List;

public class ManagementApplicationViewModel {

    private final ApplicationResource application;
    private final CompetitionResource competition;
    private final String backUrl;
    private final String originQuery;
    private final ApplicationOverviewIneligibilityViewModel ineligibility;
    private final ApplicationReadOnlyViewModel applicationReadOnlyViewModel;
    private final List<AppendixResource> appendices;
    private final boolean canMarkAsIneligible;
    private final boolean canReinstate;

    private final String competitionName;
    private final String applicationName;
    private final LocalDate startDate;
    private final Long duration;
    private final Boolean resubmission;
    private final boolean canSelectInnovationArea;
    private final String innovationAreaName;
    private final String previousApplicationNumber;
    private final String previousApplicationTitle;


    public ManagementApplicationViewModel(ApplicationResource application, CompetitionResource competition, String backUrl, String originQuery, ApplicationOverviewIneligibilityViewModel ineligibility, ApplicationReadOnlyViewModel applicationReadOnlyViewModel, List<AppendixResource> appendices, boolean canMarkAsIneligible, boolean canReinstate) {
        this.application = application;
        this.competition = competition;
        this.backUrl = backUrl;
        this.originQuery = originQuery;
        this.ineligibility = ineligibility;
        this.applicationReadOnlyViewModel = applicationReadOnlyViewModel;
        this.appendices = appendices;
        this.canMarkAsIneligible = canMarkAsIneligible;
        this.canReinstate = canReinstate;

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

    public ApplicationResource getApplication() {
        return application;
    }

    public CompetitionResource getCompetition() {
        return competition;
    }

    public String getBackUrl() {
        return backUrl;
    }

    public String getOriginQuery() {
        return originQuery;
    }

    public ApplicationOverviewIneligibilityViewModel getIneligibility() {
        return ineligibility;
    }

    public ApplicationReadOnlyViewModel getApplicationReadOnlyViewModel() {
        return applicationReadOnlyViewModel;
    }

    public List<AppendixResource> getAppendices() {
        return appendices;
    }

    public boolean isCanMarkAsIneligible() {
        return canMarkAsIneligible;
    }

    public boolean isCanReinstate() {
        return canReinstate;
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
