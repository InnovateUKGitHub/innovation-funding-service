package org.innovateuk.ifs.management.application.view.viewmodel;

import org.innovateuk.ifs.application.readonly.viewmodel.ApplicationReadOnlyViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;

import java.time.LocalDate;
import java.util.List;

public class ManagementApplicationViewModel {

    private final ApplicationResource application;
    private final CompetitionResource competition;
    private final ApplicationOverviewIneligibilityViewModel ineligibility;
    private final ApplicationReadOnlyViewModel applicationReadOnlyViewModel;
    private final List<AppendixViewModel> appendices;
    private final boolean canMarkAsIneligible;
    private final boolean canReinstate;
    private final boolean support;

    private final String competitionName;
    private final String applicationName;
    private final LocalDate startDate;
    private final Long durationInMonths;
    private final Boolean resubmission;
    private final boolean canSelectInnovationArea;
    private final String innovationAreaName;
    private final String previousApplicationNumber;
    private final String previousApplicationTitle;
    private final Long projectId;
    private final boolean externalFinanceUser;


    public ManagementApplicationViewModel(ApplicationResource application,
                                          CompetitionResource competition,
                                          ApplicationOverviewIneligibilityViewModel ineligibility,
                                          ApplicationReadOnlyViewModel applicationReadOnlyViewModel,
                                          List<AppendixViewModel> appendices,
                                          boolean canMarkAsIneligible,
                                          boolean canReinstate,
                                          boolean support,
                                          Long projectId,
                                          boolean externalFinanceUser) {
        this.application = application;
        this.competition = competition;
        this.ineligibility = ineligibility;
        this.applicationReadOnlyViewModel = applicationReadOnlyViewModel;
        this.appendices = appendices;
        this.canMarkAsIneligible = canMarkAsIneligible;
        this.canReinstate = canReinstate;
        this.support = support;
        this.projectId = projectId;
        this.externalFinanceUser = externalFinanceUser;

        this.competitionName = competition.getName();
        this.applicationName = application.getName();
        this.startDate = application.getStartDate();
        this.durationInMonths = application.getDurationInMonths();
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

    public ApplicationOverviewIneligibilityViewModel getIneligibility() {
        return ineligibility;
    }

    public ApplicationReadOnlyViewModel getApplicationReadOnlyViewModel() {
        return applicationReadOnlyViewModel;
    }

    public List<AppendixViewModel> getAppendices() {
        return appendices;
    }

    public boolean isCanMarkAsIneligible() {
        return canMarkAsIneligible;
    }

    public boolean isCanReinstate() {
        return canReinstate;
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

    public Long getProjectId() {
        return projectId;
    }

    public boolean hasProject() {
        return projectId != null;
    }

    public boolean canViewActivityLog() {
        return projectId != null && !isExternalFinanceUser();
    }
    public LocalDate getStartDate() {
        return startDate;
    }

    public Long getDurationInMonths() {
        return durationInMonths;
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

    public boolean isExternalFinanceUser() {
        return externalFinanceUser;
    }
}
