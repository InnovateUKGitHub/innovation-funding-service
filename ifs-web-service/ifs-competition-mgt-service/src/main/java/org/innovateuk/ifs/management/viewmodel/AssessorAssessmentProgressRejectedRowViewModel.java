package org.innovateuk.ifs.management.viewmodel;

import org.innovateuk.ifs.assessment.resource.AssessmentRejectOutcomeValue;

/**
 * Holder of model attributes for a rejected Assessment in the Assessor Progress view.
 */
public class AssessorAssessmentProgressRejectedRowViewModel extends AssessorAssessmentProgressRowViewModel {

    private int totalAssessors;
    private AssessmentRejectOutcomeValue rejectReason;
    private String rejectComment;

    public AssessorAssessmentProgressRejectedRowViewModel(long applicationId,
                                                          String applicationName,
                                                          String leadOrganisation,
                                                          int totalAssessors,
                                                          AssessmentRejectOutcomeValue rejectReason,
                                                          String rejectComment) {
        super(applicationId, applicationName, leadOrganisation);
        this.totalAssessors = totalAssessors;
        this.rejectReason = rejectReason;
        this.rejectComment = rejectComment;
    }

    public int getTotalAssessors() {
        return totalAssessors;
    }

    public AssessmentRejectOutcomeValue getRejectReason() {
        return rejectReason;
    }

    public String getRejectComment() {
        return rejectComment;
    }
}
