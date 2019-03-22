package org.innovateuk.ifs.application.summary.viewmodel;

import org.innovateuk.ifs.application.viewmodel.granttransfer.GrantAgreementSummaryViewModel;
import org.innovateuk.ifs.application.viewmodel.granttransfer.GrantTransferDetailsSummaryViewModel;
import org.innovateuk.ifs.application.viewmodel.researchCategory.ResearchCategorySummaryViewModel;
import org.innovateuk.ifs.application.common.viewmodel.SummaryViewModel;
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
    private final GrantTransferDetailsSummaryViewModel grantTransferDetailsSummaryViewModel;
    private final GrantAgreementSummaryViewModel grantAgreementSummaryViewModel;
    private final boolean userIsLeadApplicant;
    private final boolean projectWithdrawn;
    private final boolean support;
    private final boolean collaborativeProject;

    public ApplicationSummaryViewModel(ApplicationResource currentApplication,
                                       CompetitionResource currentCompetition,
                                       boolean applicationReadyForSubmit,
                                       SummaryViewModel summaryViewModel,
                                       ApplicationTeamViewModel applicationTeamViewModel,
                                       ResearchCategorySummaryViewModel researchCategorySummaryViewModel,
                                       boolean userIsLeadApplicant,
                                       boolean projectWithdrawn,
                                       boolean support,
                                       boolean collaborativeProject,
                                       GrantTransferDetailsSummaryViewModel grantTransferDetailsSummaryViewModel,
                                       GrantAgreementSummaryViewModel grantAgreementSummaryViewModel) {
        this.currentApplication = currentApplication;
        this.currentCompetition = currentCompetition;
        this.applicationReadyForSubmit = applicationReadyForSubmit;
        this.summaryViewModel = summaryViewModel;
        this.applicationTeamViewModel = applicationTeamViewModel;
        this.researchCategorySummaryViewModel = researchCategorySummaryViewModel;
        this.userIsLeadApplicant = userIsLeadApplicant;
        this.projectWithdrawn = projectWithdrawn;
        this.support = support;
        this.collaborativeProject = collaborativeProject;
        this.grantTransferDetailsSummaryViewModel = grantTransferDetailsSummaryViewModel;
        this.grantAgreementSummaryViewModel = grantAgreementSummaryViewModel;
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

    public boolean isSupport() {
        return support;
    }

    public boolean isCollaborativeProject() {
        return collaborativeProject;
    }

    public GrantTransferDetailsSummaryViewModel getGrantTransferDetailsSummaryViewModel() {
        return grantTransferDetailsSummaryViewModel;
    }

    public GrantAgreementSummaryViewModel getGrantAgreementSummaryViewModel() {
        return grantAgreementSummaryViewModel;
    }

    /* view logic. */
    public boolean getApplicationIsClosed() {
        return !currentCompetition.isOpen() || !currentApplication.isOpen();
    }

    public boolean getApplicationIsReadOnly() {
        return !currentCompetition.isOpen() || !currentApplication.isOpen();
    }
}
