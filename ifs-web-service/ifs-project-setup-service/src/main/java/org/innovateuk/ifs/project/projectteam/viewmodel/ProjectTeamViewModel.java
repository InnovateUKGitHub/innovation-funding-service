package org.innovateuk.ifs.project.projectteam.viewmodel;

import org.innovateuk.ifs.project.resource.ProjectUserResource;

import java.util.List;

public class ProjectTeamViewModel {

    /**
     * View model backing the Project Details page for Project Setup
     */

    private String competitionName;
    private String projectName;
    private long projectId;
    private List<ProjectOrganisationViewModel> partnerOrgs;
    private ProjectOrganisationViewModel loggedInUserOrg;
    private ProjectOrganisationViewModel leadOrg;
    private ProjectUserResource projectManager;
    private boolean userLeadPartner;

    private boolean monitoringOfficerAssigned;
    private boolean spendProfileGenerated;
    private boolean grantOfferLetterGenerated;
    private boolean readOnlyView;

    public ProjectTeamViewModel(String competitionName,
                                String projectName,
                                long projectId,
                                List<ProjectOrganisationViewModel> partnerOrgs,
                                ProjectOrganisationViewModel loggedInUserOrg,
                                ProjectOrganisationViewModel leadOrg,
                                ProjectUserResource projectManager,
                                boolean userLeadPartner,
                                boolean monitoringOfficerAssigned,
                                boolean spendProfileGenerated,
                                boolean grantOfferLetterGenerated,
                                boolean readOnlyView) {

        this.competitionName = competitionName;
        this.projectName = projectName;
        this.projectId = projectId;
        this.partnerOrgs = partnerOrgs;
        this.loggedInUserOrg = loggedInUserOrg;
        this.leadOrg = leadOrg;
        this.projectManager = projectManager;
        this.userLeadPartner = userLeadPartner;
        this.monitoringOfficerAssigned = monitoringOfficerAssigned;
        this.spendProfileGenerated = spendProfileGenerated;
        this.grantOfferLetterGenerated = grantOfferLetterGenerated;
        this.readOnlyView = readOnlyView;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public String getProjectName() {
        return projectName;
    }

    public long getProjectId() {
        return projectId;
    }

    public List<ProjectOrganisationViewModel> getPartnerOrgs() {
        return partnerOrgs;
    }

    public ProjectOrganisationViewModel getLoggedInUserOrg() {
        return loggedInUserOrg;
    }

    public ProjectOrganisationViewModel getLeadOrg() {
        return leadOrg;
    }

    public boolean isUserLeadPartner() {
        return userLeadPartner;
    }

    public boolean isReadOnly() {
        return readOnlyView;
    }

    public boolean isMonitoringOfficerAssigned() {
        return monitoringOfficerAssigned;
    }

    public boolean isSpendProfileGenerated() {
        return spendProfileGenerated;
    }

    public boolean isGrantOfferLetterGenerated() {
        return grantOfferLetterGenerated;
    }

    public ProjectUserResource getProjectManager() {
        return projectManager;
    }

}

