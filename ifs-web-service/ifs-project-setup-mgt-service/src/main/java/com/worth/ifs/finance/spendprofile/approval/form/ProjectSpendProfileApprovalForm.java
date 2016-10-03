package com.worth.ifs.finance.spendprofile.approval.form;

import com.worth.ifs.controller.BaseBindingResultTarget;

/**
 * TODO
 */
public class ProjectSpendProfileApprovalForm extends BaseBindingResultTarget {

    private Boolean approvedByLeadTechnologist;

    public void setApprovedByLeadTechnologist(Boolean approvedByLeadTechnologist) {
        this.approvedByLeadTechnologist = approvedByLeadTechnologist;
    }

    public Boolean getApprovedByLeadTechnologist() {
        return approvedByLeadTechnologist;
    }
}
