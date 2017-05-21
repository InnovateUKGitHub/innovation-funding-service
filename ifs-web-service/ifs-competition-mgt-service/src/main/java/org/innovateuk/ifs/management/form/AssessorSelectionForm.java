package org.innovateuk.ifs.management.form;

import java.util.ArrayList;
import java.util.List;

/**
 * Form for the selection of assessors on the Find assessors tab
 */
public class AssessorSelectionForm {

    private Boolean allSelected;

    private List<String> assessorEmails;

    public AssessorSelectionForm() {
        this.assessorEmails = new ArrayList<>();
    }

    public Boolean getAllSelected() {
        return allSelected;
    }

    public void setAllSelected(Boolean allSelected) {
        this.allSelected = allSelected;
    }

    public List<String> getAssessorEmails() {
        return assessorEmails;
    }

    public void setAssessorEmails(List<String> assessorEmails) {
        this.assessorEmails = assessorEmails;
    }
}
