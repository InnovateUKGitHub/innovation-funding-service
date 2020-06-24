package org.innovateuk.ifs.project.status.viewmodel;

import org.innovateuk.ifs.competition.resource.PostAwardService;
import org.innovateuk.ifs.project.projectdetails.viewmodel.BasicProjectDetailsViewModel;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectState;

import java.util.List;

/**
 * A view model that backs the Project Status page
 */
public class SetupStatusViewModel implements BasicProjectDetailsViewModel {

    private final long projectId;
    private final String projectName;
    private final boolean monitoringOfficer;
    private final ProjectState projectState;
    private final long applicationId;
    private final String competitionName;
    private final long competitionId;
    private final boolean loanCompetition;
    private final boolean investorPartnershipCompetition;
    private final boolean collaborativeProject;
    private final boolean showApplicationFeedbackLink;
    private final List<SetupStatusStageViewModel> stages;
    private final boolean projectManager;
    private final boolean projectFinanceContact;
    private final PostAwardService postAwardService;
    private final String liveProjectsLandingPageUrl;

    public SetupStatusViewModel(ProjectResource project,
                                boolean monitoringOfficer,
                                List<SetupStatusStageViewModel> stages,
                                boolean loanCompetition,
                                boolean showApplicationFeedbackLink,
                                boolean investorPartnershipCompetition,
                                boolean projectManager,
                                boolean projectFinanceContact,
                                PostAwardService postAwardService,
                                String liveProjectsLandingPageUrl) {
        this.projectId = project.getId();
        this.projectName = project.getName();
        this.monitoringOfficer = monitoringOfficer;
        this.projectState = project.getProjectState();
        this.applicationId = project.getApplication();
        this.competitionName = project.getCompetitionName();
        this.competitionId = project.getCompetition();
        this.stages = stages;
        this.loanCompetition = loanCompetition;
        this.investorPartnershipCompetition = investorPartnershipCompetition;
        this.collaborativeProject = project.isCollaborativeProject();
        this.showApplicationFeedbackLink = showApplicationFeedbackLink;
        this.projectManager = projectManager;
        this.projectFinanceContact = projectFinanceContact;
        this.postAwardService = postAwardService;
        this.liveProjectsLandingPageUrl = liveProjectsLandingPageUrl;
    }

    public Long getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public ProjectState getProjectState() {
        return projectState;
    }

    public boolean isMonitoringOfficer() {
        return monitoringOfficer;
    }

    public boolean isLoanCompetition() {
        return loanCompetition;
    }

    public boolean isInvestorPartnershipCompetition() {
        return investorPartnershipCompetition;
    }

    public List<SetupStatusStageViewModel> getStages() {
        return stages;
    }

    public boolean isCollaborativeProject() {
        return collaborativeProject;
    }

    public boolean isShowApplicationFeedbackLink() {
        return showApplicationFeedbackLink;
    }

    public boolean shouldShowStatus(SetupStatusStageViewModel stage) {
        return isMonitoringOfficer() || !stage.getAccess().isNotAccessible();
    }

    public boolean isProjectManager() {
        return projectManager;
    }

    public boolean isProjectFinanceContact() {
        return projectFinanceContact;
    }

    public PostAwardService getPostAwardService() {
        return postAwardService;
    }

    public boolean isIfsPostAward() {
        return this.postAwardService == PostAwardService.IFS_POST_AWARD;
    }

    public String getLiveProjectsLandingPageUrl() {
        return liveProjectsLandingPageUrl;
    }
}