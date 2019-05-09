package org.innovateuk.ifs.application.viewmodel;

import org.innovateuk.ifs.form.resource.QuestionResource;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;

import java.util.List;

public class AssignButtonsViewModel {
    private ProcessRoleResource assignedBy;
    private ProcessRoleResource assignee;
    private ProcessRoleResource leadApplicant;
    private ProcessRoleResource currentApplicant;
    private QuestionResource question;
    private List<ProcessRoleResource> assignableApplicants;
    private List<ApplicationInviteResource> pendingAssignableUsers;
    private boolean hideAssignButtons;

    public ProcessRoleResource getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(ProcessRoleResource assignedBy) {
        this.assignedBy = assignedBy;
    }

    public ProcessRoleResource getAssignee() {
        return assignee;
    }

    public void setAssignee(ProcessRoleResource assignee) {
        this.assignee = assignee;
    }

    public ProcessRoleResource getLeadApplicant() {
        return leadApplicant;
    }

    public void setLeadApplicant(ProcessRoleResource leadApplicant) {
        this.leadApplicant = leadApplicant;
    }

    public ProcessRoleResource getCurrentApplicant() {
        return currentApplicant;
    }

    public void setCurrentApplicant(ProcessRoleResource currentApplicant) {
        this.currentApplicant = currentApplicant;
    }

    public List<ProcessRoleResource> getAssignableApplicants() {
        return assignableApplicants;
    }

    public void setAssignableApplicants(List<ProcessRoleResource> assignableApplicants) {
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

    public boolean isAssignedTo(ProcessRoleResource role) {
        return (isNotAssigned() && role.getRole().isLeadApplicant()) || (isAssigned() && assignee.getUser().equals(role.getUser()));
    }

    public boolean isNotAssigned() {
        return !isAssigned();
    }
    public boolean isAssigned() {
        return assignee != null;
    }

    public boolean getCurrentUserIsLead() {
        return currentApplicant.getRole().isLeadApplicant();
    }

    public boolean isAssignedByLead() {
        return assignedBy.getRole().isLeadApplicant();
    }

    public boolean isAssignedByCurrentUser() { return assignedBy.getUser().equals(currentApplicant.getUser()); }

    public boolean isAssignedToLead() { return assignee.getRole().isLeadApplicant(); }
}
