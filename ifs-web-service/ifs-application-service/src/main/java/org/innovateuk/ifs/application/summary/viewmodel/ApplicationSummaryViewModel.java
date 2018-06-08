package org.innovateuk.ifs.application.summary.viewmodel;

import org.innovateuk.ifs.application.overview.viewmodel.ApplicationOverviewCompletedViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;

public class ApplicationSummaryViewModel {

    private final ApplicationResource currentApplication;
    private final CompetitionResource currentCompetition;
    private final boolean applicationReadyForSubmit;
    private final SummaryViewModel summaryViewModel;

    public ApplicationSummaryViewModel(ApplicationResource currentApplication,
                                       CompetitionResource currentCompetition,
                                       boolean applicationReadyForSubmit,
                                       SummaryViewModel summaryViewModel) {
        this.currentApplication = currentApplication;
        this.currentCompetition = currentCompetition;
        this.applicationReadyForSubmit = applicationReadyForSubmit;
        this.summaryViewModel = summaryViewModel;
    }

    public ApplicationResource getCurrentApplication() {
        return currentApplication;
    }

    public CompetitionResource getCurrentCompetition() {
        return currentCompetition;
    }

    public boolean isApplicationReadyForSubmit() {
        return applicationReadyForSubmit;
    }

    public SummaryViewModel getSummaryViewModel() {
        return summaryViewModel;
    }
}
