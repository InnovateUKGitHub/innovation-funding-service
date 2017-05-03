package org.innovateuk.ifs.application.viewmodel.overview;

import org.innovateuk.ifs.application.resource.QuestionStatusResource;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * View model for the application overview - users
 */
public class ApplicationOverviewAssignableViewModel {
    private Future<List<ProcessRoleResource>> assignableUsers;
    private List<ApplicationInviteResource> pendingAssignableUsers;
    private Map<Long, QuestionStatusResource> questionAssignees;
    private List<QuestionStatusResource> notifications;

    public ApplicationOverviewAssignableViewModel() {

    }

    public ApplicationOverviewAssignableViewModel(Future<List<ProcessRoleResource>> assignableUsers, List<ApplicationInviteResource> pendingAssignableUsers,
                                                  Map<Long, QuestionStatusResource> questionAssignees, List<QuestionStatusResource> notifications) {
        this.assignableUsers = assignableUsers;
        this.pendingAssignableUsers = pendingAssignableUsers;
        this.questionAssignees = questionAssignees;
        this.notifications = notifications;
    }

    public List<ProcessRoleResource> getAssignableUsers() throws ExecutionException, InterruptedException {
        if(null != assignableUsers) {
            return assignableUsers.get();
        }

        return null;
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
}
