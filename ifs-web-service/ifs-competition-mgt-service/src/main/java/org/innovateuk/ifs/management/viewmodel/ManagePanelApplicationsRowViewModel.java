package org.innovateuk.ifs.management.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationSummaryResource;

/**
 * Holder of model attributes for the applications shown in the 'Manage applications' page
 */
public class ManagePanelApplicationsRowViewModel {
    private long id;
    private String title;
    private String leadOrganisation;
    private long assessors;
    private long accepted;
    private long completed;
    private String innovationArea;

    public ManagePanelApplicationsRowViewModel(ApplicationSummaryResource applicationSummaryResource) {
        this.id = applicationSummaryResource.getId();
        this.title = applicationSummaryResource.getName();
        this.leadOrganisation = applicationSummaryResource.getLead();
        this.innovationArea = applicationSummaryResource.getInnovationArea();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public String getInnovationArea() { return innovationArea; }

    public void setInnovationArea(String innovationArea) { this.innovationArea = innovationArea; }
}
