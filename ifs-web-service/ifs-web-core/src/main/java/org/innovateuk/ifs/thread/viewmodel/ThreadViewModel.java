package org.innovateuk.ifs.thread.viewmodel;

import org.innovateuk.threads.resource.FinanceChecksSectionType;

import java.time.ZonedDateTime;
import java.util.List;

public class ThreadViewModel {
    public List<ThreadPostViewModel> getViewModelPosts() {
        return viewModelPosts;
    }

    public void setViewModelPosts(List<ThreadPostViewModel> posts) {
        this.viewModelPosts = posts;
    }


    public FinanceChecksSectionType getSectionType() {
        return sectionType;
    }

    public void setSectionType(FinanceChecksSectionType sectionType) {
        this.sectionType = sectionType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isAwaitingResponse() {
        return awaitingResponse;
    }

    public void setAwaitingResponse(boolean awaitingResponse) {
        this.awaitingResponse = awaitingResponse;
    }

    public ZonedDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(ZonedDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(Long organisationId) {
        this.organisationId = organisationId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }


    private List<ThreadPostViewModel> viewModelPosts;
    private FinanceChecksSectionType sectionType;
    private String title;
    private boolean awaitingResponse;
    private ZonedDateTime createdOn;
    private Long id;
    private Long organisationId;
    private Long projectId;
}
