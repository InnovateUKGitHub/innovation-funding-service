package org.innovateuk.ifs.interview.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;

import java.util.ArrayList;
import java.util.List;

/**
 * Form for the selection of assignments for interview panels
 */
public class InterviewAllocationSelectionForm extends BaseBindingResultTarget {

    private boolean allSelected;
    private List<Long> selectedIds;

    public InterviewAllocationSelectionForm() {
        this.selectedIds = new ArrayList<>();
    }

    public boolean getAllSelected() {
        return allSelected;
    }

    public void setAllSelected(boolean allSelected) {
        this.allSelected = allSelected;
    }

    public List<Long> getSelectedIds() {
        return selectedIds;
    }

    public void setSelectedIds(List<Long> selectedIds) {
        this.selectedIds = selectedIds;
    }

    public boolean anySelectionIsMade() {
        return this.selectedIds.size() > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        InterviewAllocationSelectionForm that = (InterviewAllocationSelectionForm) o;

        return new EqualsBuilder()
                .append(allSelected, that.allSelected)
                .append(selectedIds, that.selectedIds)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(allSelected)
                .append(selectedIds)
                .toHashCode();
    }
}
