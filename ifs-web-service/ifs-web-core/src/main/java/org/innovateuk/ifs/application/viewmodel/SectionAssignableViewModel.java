package org.innovateuk.ifs.application.viewmodel;

import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Shared assignable viewModel used by {@link AbstractSectionViewModel}
 */
public class SectionAssignableViewModel {
    private Future<List<ProcessRoleResource>> assignableUsers;
    private List<ApplicationInviteResource> pendingAssignableUsers;
    private Map<Long, QuestionStatusResource> questionAssignees;
    private List<QuestionStatusResource> notifications;

    public SectionAssignableViewModel() {
        this.assignableUsers = CompletableFuture.completedFuture(new ArrayList<ProcessRoleResource>());
        this.pendingAssignableUsers = new ArrayList<ApplicationInviteResource>();
        this.questionAssignees = new HashMap<Long, QuestionStatusResource>();
        this.notifications = new ArrayList<QuestionStatusResource>();
    }

    public SectionAssignableViewModel(Map<Long, QuestionStatusResource> questionAssignees,
                                      List<QuestionStatusResource> notifications) {
        this.assignableUsers = CompletableFuture.completedFuture(new ArrayList<ProcessRoleResource>());
        this.pendingAssignableUsers = new ArrayList<ApplicationInviteResource>();
        this.questionAssignees = questionAssignees;
        this.notifications = notifications;
    }

    public SectionAssignableViewModel(Future<List<ProcessRoleResource>> assignableUsers,
                                      List<ApplicationInviteResource> pendingAssignableUsers, Map<Long, QuestionStatusResource> questionAssignees,
                                      List<QuestionStatusResource> notifications) {
        this.assignableUsers = assignableUsers;
        this.pendingAssignableUsers = pendingAssignableUsers;
        this.questionAssignees = questionAssignees;
        this.notifications = notifications;
    }

    public List<ProcessRoleResource> getAssignableUsers() throws ExecutionException, InterruptedException {
        return assignableUsers.get();
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
