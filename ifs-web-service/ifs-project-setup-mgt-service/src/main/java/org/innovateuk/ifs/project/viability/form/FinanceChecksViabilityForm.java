package org.innovateuk.ifs.project.viability.form;

import org.innovateuk.ifs.controller.BaseBindingResultTarget;
import org.innovateuk.ifs.project.finance.resource.ViabilityStatus;

/**
 * Form backing the checkboxes and dropdowns on the Viability page
 */
public class FinanceChecksViabilityForm extends BaseBindingResultTarget {

    private boolean creditReportConfirmed;
    private ViabilityStatus ragStatus;

    // for Spring MVC
    FinanceChecksViabilityForm() {
    }

    public FinanceChecksViabilityForm(boolean creditReportConfirmed, ViabilityStatus ragStatus) {
        this.creditReportConfirmed = creditReportConfirmed;
        this.ragStatus = ragStatus;
    }

    public boolean isCreditReportConfirmed() {
        return creditReportConfirmed;
    }

    public boolean isViabilityConfirmedChecked() {
        return ragStatus != ViabilityStatus.UNSET;
    }

    public ViabilityStatus getRagStatus() {
        return ragStatus;
    }

    public void setCreditReportConfirmed(boolean creditReportConfirmed) {
        this.creditReportConfirmed = creditReportConfirmed;
    }

    public void setRagStatus(ViabilityStatus ragStatus) {
        this.ragStatus = ragStatus;
    }
}
