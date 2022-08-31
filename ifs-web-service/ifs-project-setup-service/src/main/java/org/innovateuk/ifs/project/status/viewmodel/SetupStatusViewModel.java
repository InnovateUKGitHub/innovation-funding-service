package org.innovateuk.ifs.project.status.viewmodel;

import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.competition.resource.CompetitionThirdPartyConfigResource;
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
    private final FundingType fundingType;
    private final boolean collaborativeProject;
    private final boolean showApplicationSummaryLink;
    private final List<SetupStatusStageViewModel> stages;
    private final boolean projectManager;
    private final boolean projectFinanceContact;
    private final PostAwardService postAwardService;
    private final String liveProjectsLandingPageUrl;
    private final boolean thirdPartyProcurement;
    private final CompetitionThirdPartyConfigResource thirdPartyConfig;
    private final boolean hasAssessmentStage;
    private final boolean isDirectAward;

    public SetupStatusViewModel(ProjectResource project,
                                boolean monitoringOfficer,
                                List<SetupStatusStageViewModel> stages,
                                FundingType fundingType,
                                boolean showApplicationSummaryLink,
                                boolean projectManager,
                                boolean projectFinanceContact,
                                PostAwardService postAwardService,
                                String liveProjectsLandingPageUrl,
                                boolean thirdPartyProcurement,
                                CompetitionThirdPartyConfigResource thirdPartyConfig,
                                boolean hasAssessmentStage,
                                boolean isDirectAward) {
        this.projectId = project.getId();
        this.projectName = project.getName();
        this.monitoringOfficer = monitoringOfficer;
        this.projectState = project.getProjectState();
        this.applicationId = project.getApplication();
        this.competitionName = project.getCompetitionName();
        this.competitionId = project.getCompetition();
        this.stages = stages;
        this.fundingType = fundingType;
        this.collaborativeProject = project.isCollaborativeProject();
        this.showApplicationSummaryLink = showApplicationSummaryLink;
        this.projectManager = projectManager;
        this.projectFinanceContact = projectFinanceContact;
        this.postAwardService = postAwardService;
        this.liveProjectsLandingPageUrl = liveProjectsLandingPageUrl;
        this.thirdPartyProcurement = thirdPartyProcurement;
        this.thirdPartyConfig = thirdPartyConfig;
        this.hasAssessmentStage = hasAssessmentStage;
        this.isDirectAward = isDirectAward;
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
        return this.fundingType == FundingType.LOAN;
    }

    public boolean isInvestorPartnershipCompetition() {
        return this.fundingType == FundingType.INVESTOR_PARTNERSHIPS;
    }

    public boolean isKtpCompetition() {
        return this.fundingType == FundingType.KTP
                || this.fundingType == FundingType.KTP_AKT;
    }

    public List<SetupStatusStageViewModel> getStages() {
        return stages;
    }

    public boolean isCollaborativeProject() {
        return collaborativeProject;
    }

    public boolean isShowApplicationSummaryLink() {
        return showApplicationSummaryLink;
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

    public boolean isApplicationFeedbackAvailable() {
        return !isKtpCompetition();
    }

    public boolean isBannerVisible() {
        return !isLoanCompetition() && !isKtpCompetition() && getProjectState().isActive();
    }

    public boolean isLiveProjectMessageVisible() {
        return !isKtpCompetition() && getProjectState().isLive();
    }

    public boolean isThirdPartyProcurement() { return thirdPartyProcurement; }

    public CompetitionThirdPartyConfigResource getThirdPartyConfig() { return thirdPartyConfig; }

    public boolean isHasAssessmentStage() {
        return hasAssessmentStage;
    }

    public boolean isDirectAward() {
        return isDirectAward;
    }
}