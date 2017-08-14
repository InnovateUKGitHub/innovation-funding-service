package org.innovateuk.ifs.management.viewmodel;

import org.innovateuk.ifs.assessment.resource.AssessmentRejectOutcomeValue;

/**
 * Holder of model attributes for a rejected Assessment in the Assessor Progress view.
 */
public class AssessorAssessmentProgressRejectedRowViewModel {

    private long applicationId;
    private String applicationName;
    private String leadOrganisation;
    private int totalAssessors;
    private AssessmentRejectOutcomeValue rejectReason;
    private String rejectComment;

    public AssessorAssessmentProgressRejectedRowViewModel(long applicationId,
                                                          String applicationName,
                                                          String leadOrganisation,
                                                          int totalAssessors,
                                                          AssessmentRejectOutcomeValue rejectReason,
                                                          String rejectComment) {
        this.applicationId = applicationId;
        this.applicationName = applicationName;
        this.leadOrganisation = leadOrganisation;
        this.totalAssessors = totalAssessors;
        this.rejectReason = rejectReason;
        this.rejectComment = rejectComment;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String getLeadOrganisation() {
        return leadOrganisation;
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
