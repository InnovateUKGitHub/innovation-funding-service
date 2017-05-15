package org.innovateuk.ifs.applicant.resource;

import org.innovateuk.ifs.application.resource.QuestionStatusResource;

/**
 * Rich resource for an application question status.
 */
public class ApplicantQuestionStatusResource {

    private QuestionStatusResource status;

    private ApplicantResource markedAsCompleteBy;
    private ApplicantResource assignee;
    private ApplicantResource assignedBy;


    public QuestionStatusResource getStatus() {
        return status;
    }

    public void setStatus(QuestionStatusResource status) {
        this.status = status;
    }

    public ApplicantResource getMarkedAsCompleteBy() {
        return markedAsCompleteBy;
    }

    public void setMarkedAsCompleteBy(ApplicantResource markedAsCompleteBy) {
        this.markedAsCompleteBy = markedAsCompleteBy;
    }

    public ApplicantResource getAssignee() {
        return assignee;
    }

    public void setAssignee(ApplicantResource assignee) {
        this.assignee = assignee;
    }

    public ApplicantResource getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(ApplicantResource assignedBy) {
        this.assignedBy = assignedBy;
    }
}
