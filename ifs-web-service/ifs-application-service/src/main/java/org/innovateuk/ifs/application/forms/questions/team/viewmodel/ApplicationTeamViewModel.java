package org.innovateuk.ifs.application.forms.questions.team.viewmodel;

import java.util.List;

public class ApplicationTeamViewModel {

    private final long applicationId;
    private final String applicationName;
    private final long questionId;
    private final List<ApplicationTeamOrganisationViewModel> organisations;
    private final long loggedInUserId;
    private final boolean leadApplicant;
    private final boolean collaborationLevelSingle;
    private final boolean open;
    private final boolean complete;
    private final boolean projectSetup;

    public ApplicationTeamViewModel(long applicationId,
                                    String applicationName,
                                    long questionId,
                                    List<ApplicationTeamOrganisationViewModel> organisations,
                                    long loggedInUserId,
                                    boolean leadApplicant,
                                    boolean collaborationLevelSingle,
                                    boolean open,
                                    boolean complete,
                                    boolean projectSetup) {
        this.applicationId = applicationId;
        this.applicationName = applicationName;
        this.questionId = questionId;
        this.organisations = organisations;
        this.loggedInUserId = loggedInUserId;
        this.leadApplicant = leadApplicant;
        this.collaborationLevelSingle = collaborationLevelSingle;
        this.open = open;
        this.complete = complete;
        this.projectSetup = projectSetup;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public long getQuestionId() {
        return questionId;
    }

    public List<ApplicationTeamOrganisationViewModel> getOrganisations() {
        return organisations;
    }

    public long getLoggedInUserId() {
        return loggedInUserId;
    }

    public boolean isLeadApplicant() {
        return leadApplicant;
    }

    public boolean isOpen() {
        return open;
    }

    public boolean isComplete() {
        return complete;
    }

    public boolean isCollaborationLevelSingle() {
        return collaborationLevelSingle;
    }

    public boolean isProjectSetup() {
        return projectSetup;
    }

    public ApplicationTeamViewModel openAddTeamMemberForm(long organisationId) {
        organisations.stream()
                .filter(partner -> partner.getId() == organisationId)
                .findAny()
                .ifPresent(partner -> partner.setOpenAddTeamMemberForm(true));
        return this;
    }

    public boolean isReadOnly() {
        return !open || complete;
    }

    public boolean isAnyPendingInvites() {
        return organisations.stream()
                .flatMap(org -> org.getRows().stream())
                .anyMatch(ApplicationTeamRowViewModel::isInvite);
    }
}
