package org.innovateuk.ifs.application.viewmodel;

import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ViewModel for the assignable part of the question
 */
public class QuestionAssignableViewModel {
    private QuestionStatusResource questionAssignee;
    private List<ProcessRoleResource> assignableUsers;
    private List<ApplicationInviteResource> pendingAssignableUsers;
    private Map<Long, QuestionStatusResource> questionAssignees;
    private List<QuestionStatusResource> notifications;

    public QuestionAssignableViewModel() {
        this.assignableUsers = new ArrayList<ProcessRoleResource>();
        this.pendingAssignableUsers = new ArrayList<ApplicationInviteResource>();
        this.questionAssignees = new HashMap<Long, QuestionStatusResource>();
        this.notifications = new ArrayList<QuestionStatusResource>();
    }

    public QuestionAssignableViewModel(QuestionStatusResource questionAssignee, List<ProcessRoleResource> assignableUsers,
                                       List<ApplicationInviteResource> pendingAssignableUsers, Map<Long, QuestionStatusResource> questionAssignees,
                                       List<QuestionStatusResource> notifications) {
        this.questionAssignee = questionAssignee;
        this.assignableUsers = assignableUsers;
        this.pendingAssignableUsers = pendingAssignableUsers;
        this.questionAssignees = questionAssignees;
        this.notifications = notifications;
    }

    public QuestionStatusResource getQuestionAssignee() {
        return questionAssignee;
    }

    public List<ProcessRoleResource> getAssignableUsers() {
        return assignableUsers;
    }

    public List<ApplicationInviteResource> getPendingAssignableUsers() {
        return pendingAssignableUsers;
    }

    public Map<Long, QuestionStatusResource> getQuestionAssignees() {
        return questionAssignees;
    }

    public List<QuestionStatusResource> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<QuestionStatusResource> notifications) {
        this.notifications = notifications;
    }
}
