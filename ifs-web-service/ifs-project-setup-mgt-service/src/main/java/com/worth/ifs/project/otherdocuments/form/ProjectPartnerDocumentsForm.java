package com.worth.ifs.project.otherdocuments.form;

import com.worth.ifs.controller.BaseBindingResultTarget;

/**
 * Form backing the Other Documents page
 */
public class ProjectPartnerDocumentsForm extends BaseBindingResultTarget {

    private Boolean approved;

    public Boolean isApproved(){return this.approved;}

    public void setApproved(Boolean approved){this.approved = approved;}

}
