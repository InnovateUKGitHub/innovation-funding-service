package org.innovateuk.ifs.management.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;

import java.util.ArrayList;
import java.util.List;

/**
 * Form for the selection of assessors on the Assessment Panel Overview assessors tab
 */
public class AssessorPanelOverviewSelectionForm extends BaseBindingResultTarget {

    private boolean allSelected;
    private List<Long> selectedInviteIds;

    public AssessorPanelOverviewSelectionForm() {
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
        return this.allSelected || this.selectedInviteIds.size() > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AssessorPanelOverviewSelectionForm that = (AssessorPanelOverviewSelectionForm) o;

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

