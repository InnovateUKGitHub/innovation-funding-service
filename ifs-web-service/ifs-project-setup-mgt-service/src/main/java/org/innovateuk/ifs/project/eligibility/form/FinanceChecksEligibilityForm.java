package org.innovateuk.ifs.project.eligibility.form;

import org.innovateuk.ifs.controller.BaseBindingResultTarget;
import org.innovateuk.ifs.project.finance.resource.EligibilityStatus;

/**
 * Form backing the checkboxes and dropdowns on the Eligibility page
 */
public class FinanceChecksEligibilityForm extends BaseBindingResultTarget {

    private EligibilityStatus eligibilityStatus;

    private boolean confirmEligibilityChecked;

    // for Spring MVC
    FinanceChecksEligibilityForm() {
    }

    public FinanceChecksEligibilityForm(EligibilityStatus eligibilityStatus, boolean confirmEligibilityChecked) {
        this.eligibilityStatus = eligibilityStatus;
        this.confirmEligibilityChecked = confirmEligibilityChecked;
    }

    public EligibilityStatus getEligibilityStatus() {
        return eligibilityStatus;
    }

    public void setEligibilityStatus(EligibilityStatus eligibilityStatus) {
        this.eligibilityStatus = eligibilityStatus;
    }

    public boolean isConfirmEligibilityChecked() {
        return confirmEligibilityChecked;
    }

    public void setConfirmEligibilityChecked(boolean confirmEligibilityChecked) {
        this.confirmEligibilityChecked = confirmEligibilityChecked;
    }
}
