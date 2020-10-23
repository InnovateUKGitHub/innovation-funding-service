package org.innovateuk.ifs.management.cofunders.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;

import java.util.ArrayList;
import java.util.List;

public class CofunderSelectionForm extends BaseBindingResultTarget {

    private boolean allSelected;
    private List<Long> selectedCofunderIds = new ArrayList<>();

    public boolean isAllSelected() {
        return allSelected;
    }

    public void setAllSelected(boolean allSelected) {
        this.allSelected = allSelected;
    }

    public List<Long> getSelectedCofunderIds() {
        return selectedCofunderIds;
    }

    public void setSelectedCofunderIds(List<Long> selectedCofunderIds) {
        this.selectedCofunderIds = selectedCofunderIds;
    }

    public boolean anySelectionIsMade() {
        return this.allSelected != false || !this.selectedCofunderIds.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        CofunderSelectionForm that = (CofunderSelectionForm) o;

        return new EqualsBuilder()
                .append(allSelected, that.allSelected)
                .append(selectedCofunderIds, that.selectedCofunderIds)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(allSelected)
                .append(selectedCofunderIds)
                .toHashCode();
    }
}
