package org.innovateuk.ifs.management.form;

import org.innovateuk.ifs.controller.BaseBindingResultTarget;

import java.util.ArrayList;
import java.util.List;

/**
 * Form for the selection of assessors on the Find assessors tab
 */
public class AssessorSelectionForm extends BaseBindingResultTarget {

    private boolean allSelected = true;

    private List<String> assessorEmails;

    public AssessorSelectionForm() {
        this.assessorEmails = new ArrayList<>();
    }

    public boolean getAllSelected() {
        return allSelected;
    }

    public void setAllSelected(boolean allSelected) {
        this.allSelected = allSelected;
    }

    public List<String> getAssessorEmails() {
        return assessorEmails;
    }

    public void setAssessorEmails(List<String> assessorEmails) {
        this.assessorEmails = assessorEmails;
    }
}
