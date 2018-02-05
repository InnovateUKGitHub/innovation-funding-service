package org.innovateuk.ifs.thread.viewmodel;

import org.innovateuk.ifs.threads.resource.FinanceChecksSectionType;
import org.innovateuk.ifs.user.resource.UserResource;

import java.time.ZonedDateTime;
import java.util.List;

public class ThreadViewModel {

    private List<ThreadPostViewModel> viewModelPosts;
    private FinanceChecksSectionType sectionType;
    private String title;
    private boolean awaitingResponse;
    private boolean pending;
    private ZonedDateTime createdOn;
    private Long id;
    private Long organisationId;
    private Long projectId;
    private UserResource closedBy;
    private ZonedDateTime closedDate;

    public ThreadViewModel(List<ThreadPostViewModel> viewModelPosts, FinanceChecksSectionType sectionType,
                           String title, boolean awaitingResponse, Boolean pending, ZonedDateTime createdOn,
                           Long id, Long organisationId, Long projectId,
                           UserResource closedBy, ZonedDateTime closedDate) {
        this.viewModelPosts = viewModelPosts;
        this.sectionType = sectionType;
        this.title = title;
        this.awaitingResponse = awaitingResponse;
        this.pending = pending;
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

    public boolean isAwaitingResponse() {
        return awaitingResponse;
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

    public boolean isClosed() {
        return closedDate != null;
    }

    public boolean isPending() { return pending; }
}