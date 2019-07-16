package org.innovateuk.ifs.management.interview.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;

import java.util.ArrayList;
import java.util.List;

/**
 * Form for the selection of assessors on the Find assessors for assessment interview tab
 */
public class InterviewSelectionForm extends BaseBindingResultTarget {

    private boolean allSelected;
    private List<Long> selectedAssessorIds;

    public InterviewSelectionForm() {
        this.selectedAssessorIds = new ArrayList<>();
    }

    public boolean getAllSelected() {
        return allSelected;
    }

    public void setAllSelected(boolean allSelected) {
        this.allSelected = allSelected;
    }

    public List<Long> getSelectedAssessorIds() {
        return selectedAssessorIds;
    }

    public void setSelectedAssessorIds(List<Long> selectedAssessorIds) {
        this.selectedAssessorIds = selectedAssessorIds;
    }

    public boolean anySelectionIsMade() {
        return !this.selectedAssessorIds.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        InterviewSelectionForm that = (InterviewSelectionForm) o;

        return new EqualsBuilder()
                .append(allSelected, that.allSelected)
                .append(selectedAssessorIds, that.selectedAssessorIds)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(allSelected)
                .append(selectedAssessorIds)
                .toHashCode();
    }
}
