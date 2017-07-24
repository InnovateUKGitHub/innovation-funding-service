package org.innovateuk.ifs.management.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource;

/**
 * Holder of model attributes for the applications shown in the 'Assessor progress' page
 */
public class AssessorAssessmentProgressApplicationRowViewModel {
    private long id;
    private String title;
    private String leadOrganisation;
    private long assessors;
    private long accepted;
    private long submitted;

    public AssessorAssessmentProgressApplicationRowViewModel(ApplicationCountSummaryResource applicationCountSummaryResource) {
        this.id = applicationCountSummaryResource.getId();
        this.title = applicationCountSummaryResource.getName();
        this.leadOrganisation = applicationCountSummaryResource.getLeadOrganisation();
        this.assessors = applicationCountSummaryResource.getAssessors();
        this.accepted = applicationCountSummaryResource.getAccepted();
        this.submitted = applicationCountSummaryResource.getSubmitted();
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

    public long getSubmitted() {
        return submitted;
    }

    public void setSubmitted(long completed) {
        this.submitted = completed;
    }
}
