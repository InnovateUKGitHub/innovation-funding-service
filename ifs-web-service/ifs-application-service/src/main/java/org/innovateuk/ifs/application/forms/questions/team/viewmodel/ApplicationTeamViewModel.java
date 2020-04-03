package org.innovateuk.ifs.application.forms.questions.team.viewmodel;

import org.innovateuk.ifs.analytics.BaseAnalyticsViewModel;

import java.util.List;

public class ApplicationTeamViewModel implements BaseAnalyticsViewModel {

    private final long applicationId;
    private final String competitionName;
    private final String applicationName;
    private final long questionId;
    private final List<ApplicationTeamOrganisationViewModel> organisations;
    private final long loggedInUserId;
    private final boolean leadApplicant;
    private final boolean collaborationLevelSingle;
    private final boolean open;
    private final boolean complete;

    public ApplicationTeamViewModel(long applicationId,
                                    String applicationName,
                                    String competitionName,
                                    long questionId,
                                    List<ApplicationTeamOrganisationViewModel> organisations,
                                    long loggedInUserId,
                                    boolean leadApplicant,
                                    boolean collaborationLevelSingle,
                                    boolean open,
                                    boolean complete) {
        this.applicationId = applicationId;
        this.competitionName = competitionName;
        this.applicationName = applicationName;
        this.questionId = questionId;
        this.organisations = organisations;
        this.loggedInUserId = loggedInUserId;
        this.leadApplicant = leadApplicant;
        this.collaborationLevelSingle = collaborationLevelSingle;
        this.open = open;
        this.complete = complete;
    }

    @Override
    public Long getApplicationId() {
        return applicationId;
    }

    @Override
    public String getCompetitionName() {
        return competitionName;
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
