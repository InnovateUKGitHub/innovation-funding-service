package org.innovateuk.ifs.interview.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;

import java.util.ArrayList;
import java.util.List;

/**
 * Form for the selection of assessors on the Interview Panel Overview assessors tab
 */
public class InterviewOverviewSelectionForm extends BaseBindingResultTarget {

    private boolean allSelected;
    private List<Long> selectedInviteIds;

    public InterviewOverviewSelectionForm() {
        this.selectedInviteIds = new ArrayList<>();
    }

    public boolean getAllSelected() {
        return allSelected;
    }

    public void setAllSelected(boolean allSelected) {
        this.allSelected = allSelected;
    }

    public List<Long> getSelectedInviteIds() {
        return selectedInviteIds;
    }

    public void setSelectedInviteIds(List<Long> selectedInviteIds) {
        this.selectedInviteIds = selectedInviteIds;
    }

    public boolean anySelectionIsMade() {
        return this.allSelected || !this.selectedInviteIds.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        InterviewOverviewSelectionForm that = (InterviewOverviewSelectionForm) o;

        return new EqualsBuilder()
                .append(allSelected, that.allSelected)
                .append(selectedInviteIds, that.selectedInviteIds)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(allSelected)
                .append(selectedInviteIds)
                .toHashCode();
    }
}

