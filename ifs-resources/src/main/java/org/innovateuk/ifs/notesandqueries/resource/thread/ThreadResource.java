package org.innovateuk.ifs.notesandqueries.resource.thread;

import org.innovateuk.ifs.notesandqueries.resource.post.PostResource;

import java.time.LocalDateTime;
import java.util.List;

public class ThreadResource {
    private List<PostResource> posts;
    private FinanceChecksSectionType sectionType;
    private String title;
    private boolean awaitingResponse;
    private LocalDateTime createdOn;
    private Long id;
    private Long organisationId;
    private Long projectId;

    public List<PostResource> getPosts() {
        return posts;
    }

    public void setPosts(List<PostResource> posts) {
        this.posts = posts;
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

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
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

}
