package org.innovateuk.ifs.projectteam.viewmodel;

import org.innovateuk.ifs.project.resource.ProjectResource;
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
    private final List<ProjectOrganisationViewModel> partners;
    private final ProjectOrganisationViewModel loggedInUserOrganisation;
    private final ProjectUserResource projectManager;
    private final boolean userLeadPartner;
    private final long loggedInUserId;

    private final boolean grantOfferLetterGenerated;
    private final boolean internal;
    private final boolean readOnly;
    private final boolean canInvitePartnerOrganisation;
    private final boolean collaborativeProject;

    public ProjectTeamViewModel(ProjectResource project,
                                List<ProjectOrganisationViewModel> partners,
                                ProjectOrganisationViewModel loggedInUserOrganisation,
                                ProjectUserResource projectManager,
                                boolean userLeadPartner,
                                long loggedInUserId,
                                boolean grantOfferLetterGenerated,
                                boolean internal,
                                boolean readOnly,
                                boolean canInvitePartnerOrganisation) {

        this.competitionName = project.getCompetitionName();
        this.competitionId = project.getCompetition();
        this.projectName = project.getName();
        this.projectId = project.getId();
        this.collaborativeProject = project.isCollaborativeProject();
        this.partners = partners;
        this.loggedInUserOrganisation = loggedInUserOrganisation;
        this.projectManager = projectManager;
        this.userLeadPartner = userLeadPartner;
        this.loggedInUserId = loggedInUserId;
        this.grantOfferLetterGenerated = grantOfferLetterGenerated;
        this.internal = internal;
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

    public List<ProjectOrganisationViewModel> getPartners() {
        return partners;
    }

    public boolean isUserLeadPartner() {
        return userLeadPartner;
    }

    public long getLoggedInUserId() {
        return loggedInUserId;
    }

    public boolean isInternalUserView() {
        return internal;
    }

    public boolean isGrantOfferLetterGenerated() {
        return grantOfferLetterGenerated;
    }

    public ProjectUserResource getProjectManager() {
        return projectManager;
    }

    public ProjectOrganisationViewModel getLoggedInUserOrganisation() {
        return loggedInUserOrganisation;
    }

    public boolean isInternal() {
        return internal;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public boolean isCollaborativeProject() {
        return collaborativeProject;
    }

    public ProjectTeamViewModel openAddTeamMemberForm(long organisationId) {
        partners.stream()
                .filter(partner -> partner.getId() == organisationId)
                .findAny()
                .ifPresent(partner -> partner.setOpenAddTeamMemberForm(true));
        return this;
    }

    public boolean isCanInvitePartnerOrganisation() {
        return canInvitePartnerOrganisation;
    }
}

