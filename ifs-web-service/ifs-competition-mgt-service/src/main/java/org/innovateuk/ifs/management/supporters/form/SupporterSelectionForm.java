package org.innovateuk.ifs.management.supporters.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;

import java.util.ArrayList;
import java.util.List;

public class SupporterSelectionForm extends BaseBindingResultTarget {

    private boolean allSelected;
    private List<Long> selectedSupporterIds = new ArrayList<>();

    public boolean isAllSelected() {
        return allSelected;
    }

    public void setAllSelected(boolean allSelected) {
        this.allSelected = allSelected;
    }

    public List<Long> getSelectedSupporterIds() {
        return selectedSupporterIds;
    }

    public void setSelectedSupporterIds(List<Long> selectedSupporterIds) {
        this.selectedSupporterIds = selectedSupporterIds;
    }

    public boolean anySelectionIsMade() {
        return this.allSelected != false || !this.selectedSupporterIds.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        SupporterSelectionForm that = (SupporterSelectionForm) o;

        return new EqualsBuilder()
                .append(allSelected, that.allSelected)
                .append(selectedSupporterIds, that.selectedSupporterIds)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(allSelected)
                .append(selectedSupporterIds)
                .toHashCode();
    }
}
