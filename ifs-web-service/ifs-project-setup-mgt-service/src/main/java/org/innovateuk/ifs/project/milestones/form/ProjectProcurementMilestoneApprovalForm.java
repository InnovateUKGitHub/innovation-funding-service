package org.innovateuk.ifs.project.milestones.form;

import org.innovateuk.ifs.controller.BaseBindingResultTarget;

/**
 * Form backing the checkbox and approval on the Payment Milestone check page
 */
public class ProjectProcurementMilestoneApprovalForm extends BaseBindingResultTarget {

    private boolean confirmMilestoneChecked;
    private String retractionReason;

    public ProjectProcurementMilestoneApprovalForm() {
    }

    public ProjectProcurementMilestoneApprovalForm(boolean confirmMilestoneChecked) {
        this.confirmMilestoneChecked = confirmMilestoneChecked;
    }

    public boolean isConfirmMilestoneChecked() {
        return confirmMilestoneChecked;
    }

    public void setConfirmMilestoneChecked(boolean confirmMilestoneChecked) {
        this.confirmMilestoneChecked = confirmMilestoneChecked;
    }

    public String getRetractionReason() {
        return retractionReason;
    }

    public void setRetractionReason(String retractionReason) {
        this.retractionReason = retractionReason;
    }
}
