package org.innovateuk.ifs.thread.viewmodel;

import org.innovateuk.ifs.threads.resource.FinanceChecksSectionType;

import java.time.ZonedDateTime;
import java.util.List;

public class ThreadViewModel {

    private List<ThreadPostViewModel> viewModelPosts;
    private FinanceChecksSectionType sectionType;
    private String title;
    private boolean awaitingResponse;
    private ZonedDateTime createdOn;
    private Long id;
    private Long organisationId;
    private Long projectId;
    private boolean resolved;

    public ThreadViewModel(List<ThreadPostViewModel> viewModelPosts, FinanceChecksSectionType sectionType,
                           String title, boolean awaitingResponse, ZonedDateTime createdOn,
                           Long id, Long organisationId, Long projectId, boolean resolved) {
        this.viewModelPosts = viewModelPosts;
        this.sectionType = sectionType;
        this.title = title;
        this.awaitingResponse = awaitingResponse;
        this.createdOn = createdOn;
        this.id = id;
        this.organisationId = organisationId;
        this.projectId = projectId;
        this.resolved = resolved;
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
}