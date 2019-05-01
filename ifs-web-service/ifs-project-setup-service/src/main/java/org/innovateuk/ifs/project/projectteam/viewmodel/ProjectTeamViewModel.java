package org.innovateuk.ifs.project.projectteam.viewmodel;

import org.innovateuk.ifs.project.resource.ProjectUserResource;

import java.util.List;

public class ProjectTeamViewModel {

    /**
     * View model backing the Project Team page for Project Setup
     */

    private String competitionName;
    private String projectName;
    private long projectId;
    private List<ProjectOrganisationViewModel> partnerOrgs;
    private ProjectOrganisationViewModel loggedInUserOrg;
    private ProjectUserResource projectManager;
    private boolean userLeadPartner;
    private long loggedInUserId;

    private boolean monitoringOfficerAssigned;
    private boolean spendProfileGenerated;
    private boolean grantOfferLetterGenerated;
    private boolean readOnlyView;

    public ProjectTeamViewModel(String competitionName,
                                String projectName,
                                long projectId,
                                List<ProjectOrganisationViewModel> partnerOrgs,
                                ProjectOrganisationViewModel loggedInUserOrg,
                                ProjectUserResource projectManager,
                                boolean userLeadPartner,
                                long loggedInUserId,
                                boolean monitoringOfficerAssigned,
                                boolean spendProfileGenerated,
                                boolean grantOfferLetterGenerated,
                                boolean readOnlyView) {

        this.competitionName = competitionName;
        this.projectName = projectName;
        this.projectId = projectId;
        this.partnerOrgs = partnerOrgs;
        this.loggedInUserOrg = loggedInUserOrg;
        this.projectManager = projectManager;
        this.userLeadPartner = userLeadPartner;
        this.loggedInUserId = loggedInUserId;
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

    public boolean isUserLeadPartner() {
        return userLeadPartner;
    }

    public long getLoggedInUserId() {
        return loggedInUserId;
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

    public ProjectOrganisationViewModel getLoggedInUserOrg() {
        return loggedInUserOrg;
    }

    public ProjectTeamViewModel openAddTeamMemberForm(long organisationId) {
        partnerOrgs.stream()
                .filter(partner -> partner.getOrgId() == organisationId)
                .findAny()
                .ifPresent(partner -> partner.setOpenAddTeamMemberForm(true));
        return this;
    }
}

