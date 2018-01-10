package org.innovateuk.ifs.management.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationCountSummaryResource;

/**
 * Holder of model attributes for the applications shown in the 'Assessor progress' page
 */
public class AssessorAssessmentProgressApplicationRowViewModel extends AssessorAssessmentProgressRowViewModel {
    private long assessors;
    private long accepted;
    private long submitted;

    public AssessorAssessmentProgressApplicationRowViewModel(ApplicationCountSummaryResource applicationCountSummaryResource) {
        super(applicationCountSummaryResource.getId(), applicationCountSummaryResource.getName(), applicationCountSummaryResource.getLeadOrganisation());
        this.assessors = applicationCountSummaryResource.getAssessors();
        this.accepted = applicationCountSummaryResource.getAccepted();
        this.submitted = applicationCountSummaryResource.getSubmitted();
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
