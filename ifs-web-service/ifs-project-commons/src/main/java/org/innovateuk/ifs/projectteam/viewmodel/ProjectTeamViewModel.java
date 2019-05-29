package org.innovateuk.ifs.projectteam.viewmodel;

import org.innovateuk.ifs.project.resource.ProjectUserResource;

import java.util.List;

/**
 * View model backing the Project Team page for Project Setup
 */
public class ProjectTeamViewModel {

    private String competitionName;
    private String projectName;
    private long projectId;
    private List<ProjectOrganisationViewModel> partnerOrgs;
    private ProjectOrganisationViewModel loggedInUserOrg;
    private ProjectUserResource projectManager;
    private boolean userLeadPartner;
    private long loggedInUserId;

    private boolean grantOfferLetterGenerated;
    private boolean internalUserView;
    private boolean readOnly;

    public ProjectTeamViewModel(String competitionName,
                                String projectName,
                                long projectId,
                                List<ProjectOrganisationViewModel> partnerOrgs,
                                ProjectOrganisationViewModel loggedInUserOrg,
                                ProjectUserResource projectManager,
                                boolean userLeadPartner,
                                long loggedInUserId,
                                boolean grantOfferLetterGenerated,
                                boolean internalUserView,
                                boolean readOnly) {

        this.competitionName = competitionName;
        this.projectName = projectName;
        this.projectId = projectId;
        this.partnerOrgs = partnerOrgs;
        this.loggedInUserOrg = loggedInUserOrg;
        this.projectManager = projectManager;
        this.userLeadPartner = userLeadPartner;
        this.loggedInUserId = loggedInUserId;
        this.grantOfferLetterGenerated = grantOfferLetterGenerated;
        this.internalUserView = internalUserView;
        this.readOnly = readOnly;
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

    public boolean isInternalUserView() {
        return internalUserView;
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

    public boolean isReadOnly() {
        return readOnly;
    }

    public ProjectTeamViewModel openAddTeamMemberForm(long organisationId) {
        partnerOrgs.stream()
                .filter(partner -> partner.getOrgId() == organisationId)
                .findAny()
                .ifPresent(partner -> partner.setOpenAddTeamMemberForm(true));
        return this;
    }
}

