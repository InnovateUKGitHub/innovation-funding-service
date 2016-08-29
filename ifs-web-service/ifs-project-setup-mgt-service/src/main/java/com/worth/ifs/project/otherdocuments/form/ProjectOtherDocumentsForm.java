package com.worth.ifs.project.otherdocuments.form;

import com.worth.ifs.controller.BaseBindingResultTarget;

/**
 * Form backing the Other Documents page
 */
public class ProjectOtherDocumentsForm extends BaseBindingResultTarget {

    private boolean rejected;
    private boolean approved;
    private String rejectionReason;

    public boolean isApproved(){return this.approved;}

    public void setApproved(boolean approved){this.approved = approved;}

    public boolean isRejected(){return this.rejected;}

    public void setRejected(boolean rejected){this.rejected = rejected;}

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReasons) {
        this.rejectionReason = rejectionReason;
    }


}
