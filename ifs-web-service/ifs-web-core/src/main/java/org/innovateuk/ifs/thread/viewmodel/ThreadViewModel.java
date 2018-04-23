package org.innovateuk.ifs.thread.viewmodel;

import org.innovateuk.ifs.threads.resource.FinanceChecksSectionType;
import org.innovateuk.ifs.user.resource.UserResource;

import java.time.ZonedDateTime;
import java.util.List;

import static org.innovateuk.ifs.thread.viewmodel.ThreadState.*;
import static org.innovateuk.ifs.user.resource.Role.PROJECT_FINANCE;

public class ThreadViewModel {

    private List<ThreadPostViewModel> viewModelPosts;
    private FinanceChecksSectionType sectionType;
    private String title;
    private ZonedDateTime createdOn;
    private Long id;
    private Long organisationId;
    private Long projectId;
    private UserResource closedBy;
    private ZonedDateTime closedDate;

    public ThreadViewModel(List<ThreadPostViewModel> viewModelPosts, FinanceChecksSectionType sectionType,
                           String title, ZonedDateTime createdOn,
                           Long id, Long organisationId, Long projectId,
                           UserResource closedBy, ZonedDateTime closedDate) {
        this.viewModelPosts = viewModelPosts;
        this.sectionType = sectionType;
        this.title = title;
        this.createdOn = createdOn;
        this.id = id;
        this.organisationId = organisationId;
        this.projectId = projectId;
        this.closedBy = closedBy;
        this.closedDate = closedDate;
    }

    public List<ThreadPostViewModel> getViewModelPosts() {
        return viewModelPosts;
    }

    public FinanceChecksSectionType getSectionType() {
        return sectionType;
    }

    public String getTitle() {
        return title;
    }

    public ZonedDateTime getCreatedOn() {
        return createdOn;
    }

    public Long getId() {
        return id;
    }

    public Long getOrganisationId() {
        return organisationId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public UserResource getClosedBy() {
        return closedBy;
    }

    public ZonedDateTime getClosedDate() {
        return closedDate;
    }

    public ThreadState getState() {
        if (closedDate != null) {
            return CLOSED;
        } else if (isLastPostProjectFinance()) {
            return LAST_POST_BY_INTERNAL_USER;
        }
        return LAST_POST_BY_EXTERNAL_USER;
    }


    public boolean isClosed() {
        return getState().equals(CLOSED);
    }

    public boolean isLastPostByInternalUser() {
        return getState().equals(LAST_POST_BY_INTERNAL_USER);
    }

    public boolean isLastPostByExternalUser() {
        return getState().equals(LAST_POST_BY_EXTERNAL_USER);
    }

    private boolean isLastPostProjectFinance() {
        return viewModelPosts
                .get(viewModelPosts.size() -1)
                .author.hasRole(PROJECT_FINANCE);
    }
}