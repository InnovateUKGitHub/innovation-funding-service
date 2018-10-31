package org.innovateuk.ifs.project.otherdocuments.form;

import org.innovateuk.ifs.commons.OtherDocsWindDown;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;

/**
 * Form backing the Other Documents page
 */
@OtherDocsWindDown
public class OtherDocumentsForm extends BaseBindingResultTarget {

    private Boolean approved;

    public Boolean isApproved(){return this.approved;}

    public void setApproved(Boolean approved){this.approved = approved;}

}
