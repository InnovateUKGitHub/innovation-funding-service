package org.innovateuk.ifs.management.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;

import java.util.ArrayList;
import java.util.List;

/**
 * Form for the selection of assessors on the Find assessors tab
 */
public class AssessorSelectionForm extends BaseBindingResultTarget {

    private boolean allSelected;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AssessorSelectionForm that = (AssessorSelectionForm) o;

        return new EqualsBuilder()
                .append(allSelected, that.allSelected)
                .append(assessorEmails, that.assessorEmails)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(allSelected)
                .append(assessorEmails)
                .toHashCode();
    }
}
