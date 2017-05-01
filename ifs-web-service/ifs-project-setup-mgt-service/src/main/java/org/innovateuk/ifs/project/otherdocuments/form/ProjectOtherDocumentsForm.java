package org.innovateuk.ifs.project.otherdocuments.form;

import org.innovateuk.ifs.controller.BaseBindingResultTarget;

/**
 * Form backing the Other Documents page
 */
public class ProjectOtherDocumentsForm extends BaseBindingResultTarget {

    private Boolean approved;

    public Boolean isApproved(){return this.approved;}

    public void setApproved(Boolean approved){this.approved = approved;}

}
