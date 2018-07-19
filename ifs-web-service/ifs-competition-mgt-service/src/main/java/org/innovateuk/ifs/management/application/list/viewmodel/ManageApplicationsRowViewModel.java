package org.innovateuk.ifs.management.application.list.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource;

/**
 * Holder of model attributes for the applications shown in the 'Manage applications' page
 */
public class ManageApplicationsRowViewModel {
    private Long id;
    private String title;
    private String leadOrganisation;
    private long assessors;
    private long accepted;
    private long completed;

    public ManageApplicationsRowViewModel(ApplicationCountSummaryResource applicationCountSummaryResource) {
        this.id = applicationCountSummaryResource.getId();
        this.title = applicationCountSummaryResource.getName();
        this.leadOrganisation = applicationCountSummaryResource.getLeadOrganisation();
        this.assessors = applicationCountSummaryResource.getAssessors();
        this.accepted = applicationCountSummaryResource.getAccepted();
        this.completed = applicationCountSummaryResource.getSubmitted();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLeadOrganisation() {
        return leadOrganisation;
    }

    public void setLeadOrganisation(String leadOrganisation) {
        this.leadOrganisation = leadOrganisation;
    }

    public long getAssessors() {
        return assessors;
    }

    public void setAssessors(long assessors) {
        this.assessors = assessors;
    }

    public long getAccepted() {
        return accepted;
    }

    public void setAccepted(long accepted) {
        this.accepted = accepted;
    }

    public long getCompleted() {
        return completed;
    }

    public void setCompleted(long completed) {
        this.completed = completed;
    }
}
