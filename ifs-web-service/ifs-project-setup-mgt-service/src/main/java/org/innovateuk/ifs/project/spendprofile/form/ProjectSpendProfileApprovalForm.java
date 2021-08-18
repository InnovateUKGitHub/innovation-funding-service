package org.innovateuk.ifs.project.spendprofile.form;

import org.innovateuk.ifs.controller.BaseBindingResultTarget;

/**
 * Empty form to handle validation errors
 */
public class ProjectSpendProfileApprovalForm extends BaseBindingResultTarget {

    private boolean spendProfileApproved;

    public boolean isSpendProfileApproved() {
        return spendProfileApproved;
    }

    public void setSpendProfileApproved(boolean spendProfileApproved) {
        this.spendProfileApproved = spendProfileApproved;
    }
}
