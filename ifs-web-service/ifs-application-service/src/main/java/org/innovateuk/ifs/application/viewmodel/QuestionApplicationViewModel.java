package org.innovateuk.ifs.application.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.form.resource.FormInputResponseResource;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

/**
 * TODO - comments that make sense
 */
public class QuestionApplicationViewModel {
    private Set<Long> markedAsComplete;
    private Boolean allReadOnly;
    private OrganisationResource leadOrganisation;
    private ApplicationResource currentApplication;
    private CompetitionResource competitionResource;
    private OrganisationResource userOrganisation;
    private Map<Long, FormInputResponseResource> responses;
    private Boolean userIsLeadApplicant;
    private UserResource leadApplicant;

    //Assignable details
    private QuestionStatusResource questionAssignee;
    private Future<List<ProcessRoleResource>> assignableUsers;
    private List<ApplicationInviteResource> pendingAssignableUsers;
    private Map<Long, QuestionStatusResource> questionAssignees;
    private List<QuestionStatusResource> notifications;

    public QuestionApplicationViewModel(Set<Long> markedAsComplete, Boolean allReadOnly, ApplicationResource currentApplication,
                                        CompetitionResource competitionResource, OrganisationResource userOrganisation) {
        this.markedAsComplete = markedAsComplete;
        this.allReadOnly = allReadOnly;
        this.currentApplication = currentApplication;
        this.competitionResource = competitionResource;
        this.userOrganisation = userOrganisation;
    }

    public void setLeadOrganisation(OrganisationResource leadOrganisation) {
        this.leadOrganisation = leadOrganisation;
    }

    public Set<Long> getMarkedAsComplete() {
        return markedAsComplete;
    }

    public Boolean getAllReadOnly() {
        return allReadOnly;
    }

    public OrganisationResource getLeadOrganisation() {
        return leadOrganisation;
    }

    public ApplicationResource getCurrentApplication() {
        return currentApplication;
    }

    public CompetitionResource getCompetitionResource() {
        return competitionResource;
    }

    public OrganisationResource getUserOrganisation() {
        return userOrganisation;
    }

    public Map<Long, FormInputResponseResource> getResponses() {
        return responses;
    }

    public void setResponses(Map<Long, FormInputResponseResource> responses) {
        this.responses = responses;
    }

    public Boolean getUserIsLeadApplicant() {
        return userIsLeadApplicant;
    }

    public void setUserIsLeadApplicant(Boolean userIsLeadApplicant) {
        this.userIsLeadApplicant = userIsLeadApplicant;
    }

    public UserResource getLeadApplicant() {
        return leadApplicant;
    }

    public void setLeadApplicant(UserResource leadApplicant) {
        this.leadApplicant = leadApplicant;
    }

    public QuestionStatusResource getQuestionAssignee() {
        return questionAssignee;
    }

    public void setQuestionAssignee(QuestionStatusResource questionAssignee) {
        this.questionAssignee = questionAssignee;
    }

    public Future<List<ProcessRoleResource>> getAssignableUsers() {
        return assignableUsers;
    }

    public void setAssignableUsers(Future<List<ProcessRoleResource>> assignableUsers) {
        this.assignableUsers = assignableUsers;
    }

    public List<ApplicationInviteResource> getPendingAssignableUsers() {
        return pendingAssignableUsers;
    }

    public void setPendingAssignableUsers(List<ApplicationInviteResource> pendingAssignableUsers) {
        this.pendingAssignableUsers = pendingAssignableUsers;
    }

    public Map<Long, QuestionStatusResource> getQuestionAssignees() {
        return questionAssignees;
    }

    public void setQuestionAssignees(Map<Long, QuestionStatusResource> questionAssignees) {
        this.questionAssignees = questionAssignees;
    }

    public List<QuestionStatusResource> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<QuestionStatusResource> notifications) {
        this.notifications = notifications;
    }
}
