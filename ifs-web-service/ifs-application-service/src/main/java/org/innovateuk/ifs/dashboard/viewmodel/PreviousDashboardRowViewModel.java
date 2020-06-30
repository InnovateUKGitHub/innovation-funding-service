package org.innovateuk.ifs.dashboard.viewmodel;

import org.innovateuk.ifs.applicant.resource.dashboard.DashboardPreviousRowResource;
import org.innovateuk.ifs.application.resource.ApplicationState;
import org.innovateuk.ifs.competition.resource.CompetitionCompletionStage;
import org.innovateuk.ifs.project.resource.ProjectState;

import java.time.LocalDate;

import static com.google.common.base.Strings.isNullOrEmpty;
import static org.innovateuk.ifs.application.resource.ApplicationState.*;

/**
 * View model for each application row in the 'Previous' section of the applicant dashboard.
 */
public class PreviousDashboardRowViewModel extends AbstractApplicantDashboardRowViewModel {

    private static String SINGLE_TITLE = "Delete this application";
    private static String COLLABORATIVE_TITLE = "Delete this application for all partners";
    private static String SINGLE_MESSAGE = "This application and all its data will be permanently deleted.";
    private static String COLLABORATIVE_MESSAGE = "This application and all its data will be permanently deleted for all partners.";

    private final ApplicationState applicationState;
    private final LocalDate startDate;
    private final ProjectState projectState;
    private final Long projectId;
    private final boolean leadApplicant;
    private final boolean collaborationLevelSingle;
    private final CompetitionCompletionStage competitionCompletionStage;

    public PreviousDashboardRowViewModel(String title,
                                         long applicationId,
                                         Long projectId,
                                         String competitionTitle,
                                         ApplicationState applicationState,
                                         ProjectState projectState,
                                         LocalDate startDate,
                                         boolean leadApplicant,
                                         boolean collaborationLevelSingle,
                                         CompetitionCompletionStage competitionCompletionStage) {
        super(title, applicationId, competitionTitle);
        this.applicationState = applicationState;
        this.projectState = projectState;
        this.projectId = projectId;
        this.startDate = startDate;
        this.leadApplicant = leadApplicant;
        this.collaborationLevelSingle = collaborationLevelSingle;
        this.competitionCompletionStage = competitionCompletionStage;
    }

    public PreviousDashboardRowViewModel(DashboardPreviousRowResource resource){
        super(resource.getTitle(), resource.getApplicationId(), resource.getCompetitionTitle());
        this.applicationState = resource.getApplicationState();
        this.projectState = resource.getProjectState();
        this.projectId = resource.getProjectId();
        this.startDate = resource.getStartDate();
        this.leadApplicant = resource.isLeadApplicant();
        this.collaborationLevelSingle = resource.isCollaborationLevelSingle();
        this.competitionCompletionStage = resource.getCompetitionCompletionStage();
    }

    public CompetitionCompletionStage getCompetitionCompletionStage() {
        return competitionCompletionStage;
    }

    public ApplicationState getApplicationState() {
        return applicationState;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    private boolean hasProject() {
        return projectState != null;
    }

    public boolean isLeadApplicant() {
        return leadApplicant;
    }

    /* View logic */

    private boolean isRejected() {
        return REJECTED.equals(applicationState);
    }

    private boolean isApproved() {
        return APPROVED.equals(applicationState) && !hasProject();
    }

    private boolean isCreatedOrOpen() {
        return OPENED.equals(applicationState)
                ||  CREATED.equals(applicationState);
    }

    private boolean isInformedIneligible() {
        return INELIGIBLE_INFORMED.equals(applicationState);
    }

    private boolean isWithdrawn() {
        return hasProject() && projectState.isWithdrawn();
    }

    private boolean isLiveOrCompletedOffline() {
        return hasProject() && (projectState.isLive() || projectState.isCompletedOffline());
    }

    private boolean isUnsuccessful() {
        return hasProject() && projectState.isUnsuccessful();
    }

    private boolean isSubmitted() {
        return SUBMITTED.equals(applicationState) && CompetitionCompletionStage.COMPETITION_CLOSE.equals(this.competitionCompletionStage);
    }

    public boolean canHideApplication() {
        return !leadApplicant && !submittedAndFinishedStates.contains(applicationState);
    }

    public boolean canDeleteApplication() {
        return leadApplicant && !submittedAndFinishedStates.contains(applicationState);
    }

    public String getDeleteModalTitle() {
        return collaborationLevelSingle ? SINGLE_TITLE : COLLABORATIVE_TITLE;
    }

    public String getDeleteModalMessage() {
        return collaborationLevelSingle ? SINGLE_MESSAGE : COLLABORATIVE_MESSAGE;
    }

    @Override
    public String getLinkUrl() {
        return hasProject()
                ? String.format("/project-setup/project/%d", projectId)
                : String.format("/application/%d/summary", getApplicationNumber());
    }

    @Override
    public String getTitle() {
        return !isNullOrEmpty(title) ? title : "Untitled application";
    }

}
