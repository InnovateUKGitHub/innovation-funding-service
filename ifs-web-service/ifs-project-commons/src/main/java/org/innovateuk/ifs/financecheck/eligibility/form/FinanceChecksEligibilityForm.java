package org.innovateuk.ifs.financecheck.eligibility.form;

import org.innovateuk.ifs.controller.BaseBindingResultTarget;
import org.innovateuk.ifs.project.finance.resource.EligibilityRagStatus;

/**
 * Form backing the checkboxes and dropdowns on the Eligibility page
 */
public class FinanceChecksEligibilityForm extends BaseBindingResultTarget {

    private EligibilityRagStatus eligibilityRagStatus;

    private boolean confirmEligibilityChecked;

    // for Spring MVC
    FinanceChecksEligibilityForm() {
    }

    public FinanceChecksEligibilityForm(EligibilityRagStatus eligibilityRagStatus, boolean confirmEligibilityChecked) {
        this.eligibilityRagStatus = eligibilityRagStatus;
        this.confirmEligibilityChecked = confirmEligibilityChecked;
    }

    public EligibilityRagStatus getEligibilityRagStatus() {
        return eligibilityRagStatus;
    }

    public void setEligibilityRagStatus(EligibilityRagStatus eligibilityRagStatus) {
        this.eligibilityRagStatus = eligibilityRagStatus;
    }

    public boolean isConfirmEligibilityChecked() {
        return confirmEligibilityChecked;
    }

    public void setConfirmEligibilityChecked(boolean confirmEligibilityChecked) {
        this.confirmEligibilityChecked = confirmEligibilityChecked;
    }
}
