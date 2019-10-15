package org.innovateuk.ifs.projectteam.viewmodel;

import org.innovateuk.ifs.project.resource.ProjectUserResource;

import java.util.List;

/**
 * View model backing the Project Team page for Project Setup
 */
public class ProjectTeamViewModel {

    private final String competitionName;
    private final long competitionId;
    private final String projectName;
    private final long projectId;
    private final List<ProjectOrganisationViewModel> partnerOrgs;
    private final ProjectOrganisationViewModel loggedInUserOrg;
    private final ProjectUserResource projectManager;
    private final boolean userLeadPartner;
    private final long loggedInUserId;

    private final boolean grantOfferLetterGenerated;
    private final boolean internalUserView;
    private final boolean readOnly;
    private final boolean canInvitePartnerOrganisation;

    public ProjectTeamViewModel(String competitionName,
                                long competitionId,
                                String projectName,
                                long projectId,
                                List<ProjectOrganisationViewModel> partnerOrgs,
                                ProjectOrganisationViewModel loggedInUserOrg,
                                ProjectUserResource projectManager,
                                boolean userLeadPartner,
                                long loggedInUserId,
                                boolean grantOfferLetterGenerated,
                                boolean internalUserView,
                                boolean readOnly,
                                boolean canInvitePartnerOrganisation) {

        this.competitionName = competitionName;
        this.competitionId = competitionId;
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
        this.canInvitePartnerOrganisation = canInvitePartnerOrganisation;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public long getCompetitionId() {
        return competitionId;
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

    public boolean isCanInvitePartnerOrganisation() {
        return canInvitePartnerOrganisation;
    }
}

