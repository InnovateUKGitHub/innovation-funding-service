package org.innovateuk.ifs.application.viewmodel;

import org.innovateuk.ifs.applicant.resource.ApplicantResource;
import org.innovateuk.ifs.application.resource.QuestionResource;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;

import java.util.List;

public class AssignButtonsViewModel {
    private ApplicantResource assignedBy;
    private ApplicantResource assignee;
    private ApplicantResource leadApplicant;
    private ApplicantResource currentApplicant;
    private QuestionResource question;
    private List<ApplicantResource> assignableApplicants;
    private List<ApplicationInviteResource> pendingAssignableUsers;
    private boolean hideAssignButtons;
    private boolean currentCompetitionIsClosed;

    public ApplicantResource getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(ApplicantResource assignedBy) {
        this.assignedBy = assignedBy;
    }

    public ApplicantResource getAssignee() {
        return assignee;
    }

    public void setAssignee(ApplicantResource assignee) {
        this.assignee = assignee;
    }

    public ApplicantResource getLeadApplicant() {
        return leadApplicant;
    }

    public void setLeadApplicant(ApplicantResource leadApplicant) {
        this.leadApplicant = leadApplicant;
    }

    public ApplicantResource getCurrentApplicant() {
        return currentApplicant;
    }

    public void setCurrentApplicant(ApplicantResource currentApplicant) {
        this.currentApplicant = currentApplicant;
    }

    public List<ApplicantResource> getAssignableApplicants() {
        return assignableApplicants;
    }

    public void setAssignableApplicants(List<ApplicantResource> assignableApplicants) {
        this.assignableApplicants = assignableApplicants;
    }

    public List<ApplicationInviteResource> getPendingAssignableUsers() {
        return pendingAssignableUsers;
    }

    public void setPendingAssignableUsers(List<ApplicationInviteResource> pendingAssignableUsers) {
        this.pendingAssignableUsers = pendingAssignableUsers;
    }

    public QuestionResource getQuestion() {
        return question;
    }

    public void setQuestion(QuestionResource question) {
        this.question = question;
    }

    public boolean isHideAssignButtons() {
        return hideAssignButtons;
    }

    public void setHideAssignButtons(boolean hideAssignButtons) {
        this.hideAssignButtons = hideAssignButtons;
    }

    /* View logic methods. */
    public boolean isAssignedToCurrentUser() {
        return isAssignedTo(currentApplicant);
    }

    public boolean isAssignedTo(ApplicantResource applicant) {
        return (isNotAssigned() && applicant.isLead()) || (isAssigned() && assignee.isSameUser(applicant));
    }

    public boolean isNotAssigned() {
        return !isAssigned();
    }
    public boolean isAssigned() {
        return assignee != null;
    }

    public boolean getCurrentUserIsLead() {
        return currentApplicant.isLead();
    }

    public boolean isAssignedByLead() {
        return assignedBy.isLead();
    }

    public boolean isAssignedByCurrentUser() { return assignedBy.isSameUser(currentApplicant); }

    public boolean isAssignedToLead() { return assignee.isLead(); }

    public boolean isCurrentCompetitionIsOpen() {
        return currentCompetitionIsClosed;
    }

    public void setCurrentCompetitionIsClosed(boolean currentCompetitionIsClosed) {
        this.currentCompetitionIsClosed = currentCompetitionIsClosed;
    }
}
