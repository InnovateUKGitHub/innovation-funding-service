package org.innovateuk.ifs.application.viewmodel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
 * TODO - comments that make sense
 */
public class QuestionAssignableViewModel {
    private static final Log LOG = LogFactory.getLog(QuestionAssignableViewModel.class);

    //Assignable details
    private QuestionStatusResource questionAssignee;
    private Future<List<ProcessRoleResource>> assignableUsers;
    private List<ApplicationInviteResource> pendingAssignableUsers;
    private Map<Long, QuestionStatusResource> questionAssignees;
    private List<QuestionStatusResource> notifications;

    public QuestionAssignableViewModel() {
        this.assignableUsers = CompletableFuture.completedFuture(new ArrayList<ProcessRoleResource>());
        this.pendingAssignableUsers = new ArrayList<ApplicationInviteResource>();
        this.questionAssignees = new HashMap<Long, QuestionStatusResource>();
        this.notifications = new ArrayList<QuestionStatusResource>();
    }

    public QuestionAssignableViewModel(QuestionStatusResource questionAssignee, Future<List<ProcessRoleResource>> assignableUsers,
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
        try {
            return assignableUsers.get();
        } catch (InterruptedException | ExecutionException e) {
            LOG.error("Exception while retrieving Assignable users");
            return null;
        }
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
