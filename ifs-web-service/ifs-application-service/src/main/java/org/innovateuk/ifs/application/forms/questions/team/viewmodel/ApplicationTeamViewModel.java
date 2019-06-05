package org.innovateuk.ifs.application.forms.questions.team.viewmodel;

import java.util.List;

public class ApplicationTeamViewModel {

    private final long applicationId;
    private final String applicationName;
    private final long questionId;
    private final List<ApplicationTeamOrganisationViewModel> organisations;
    private final long loggedInUserId;
    private final boolean closed;
    private final boolean complete;

    public ApplicationTeamViewModel(long applicationId, String applicationName, long questionId, List<ApplicationTeamOrganisationViewModel> organisations, long loggedInUserId, boolean closed, boolean complete) {
        this.applicationId = applicationId;
        this.applicationName = applicationName;
        this.questionId = questionId;
        this.organisations = organisations;
        this.loggedInUserId = loggedInUserId;
        this.closed = closed;
        this.complete = complete;
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

    public boolean isClosed() {
        return closed;
    }

    public boolean isComplete() {
        return complete;
    }

    public ApplicationTeamViewModel openAddTeamMemberForm(long organisationId) {
        organisations.stream()
                .filter(partner -> partner.getId() == organisationId)
                .findAny()
                .ifPresent(partner -> partner.setOpenAddTeamMemberForm(true));
        return this;
    }

    public boolean isReadOnly() {
        return closed || complete;
    }

    public boolean isCanMarkAsComplete() { return true; }
}
