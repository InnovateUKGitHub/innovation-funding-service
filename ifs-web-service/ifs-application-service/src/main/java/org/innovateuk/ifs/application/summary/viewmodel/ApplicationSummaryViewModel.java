package org.innovateuk.ifs.application.summary.viewmodel;

import org.innovateuk.ifs.application.areas.viewmodel.ResearchCategorySummaryViewModel;
import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.team.viewmodel.ApplicationTeamViewModel;
import org.innovateuk.ifs.competition.resource.CompetitionResource;

public class ApplicationSummaryViewModel {

    private final ApplicationResource currentApplication;
    private final CompetitionResource currentCompetition;
    private final boolean applicationReadyForSubmit;
    private final SummaryViewModel summaryViewModel;
    private final ApplicationTeamViewModel applicationTeamViewModel;
    private final ResearchCategorySummaryViewModel researchCategorySummaryViewModel;
    private final boolean userIsLeadApplicant;
    private final boolean projectWithdrawn;

    public ApplicationSummaryViewModel(ApplicationResource currentApplication,
                                       CompetitionResource currentCompetition,
                                       boolean applicationReadyForSubmit,
                                       SummaryViewModel summaryViewModel,
                                       ApplicationTeamViewModel applicationTeamViewModel,
                                       ResearchCategorySummaryViewModel researchCategorySummaryViewModel,
                                       boolean userIsLeadApplicant,
                                       boolean projectWithdrawn) {
        this.currentApplication = currentApplication;
        this.currentCompetition = currentCompetition;
        this.applicationReadyForSubmit = applicationReadyForSubmit;
        this.summaryViewModel = summaryViewModel;
        this.applicationTeamViewModel = applicationTeamViewModel;
        this.researchCategorySummaryViewModel = researchCategorySummaryViewModel;
        this.userIsLeadApplicant = userIsLeadApplicant;
        this.projectWithdrawn = projectWithdrawn;
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

    public ApplicationTeamViewModel getApplicationTeamViewModel() {
        return applicationTeamViewModel;
    }

    public ResearchCategorySummaryViewModel getResearchCategorySummaryViewModel() {
        return researchCategorySummaryViewModel;
    }

    public boolean isUserIsLeadApplicant() {
        return userIsLeadApplicant;
    }

    public boolean isProjectWithdrawn() {
        return projectWithdrawn;
    }
}
