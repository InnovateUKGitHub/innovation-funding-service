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
    private List<Long> selectedAssessorIds;
    private Long selectedInnovationArea;

    public AssessorSelectionForm() {
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

    public Long getSelectedInnovationArea() {
        return selectedInnovationArea;
    }

    public void setSelectedInnovationArea(Long selectedInnovationArea) {
        this.selectedInnovationArea = selectedInnovationArea;
    }

    public boolean anyFilterIsActive() {
        return selectedInnovationArea != null;
    }

    public boolean anySelectionIsMade() {
        return this.allSelected != false ||
                this.selectedAssessorIds.size() > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AssessorSelectionForm that = (AssessorSelectionForm) o;

        return new EqualsBuilder()
                .append(allSelected, that.allSelected)
                .append(selectedAssessorIds, that.selectedAssessorIds)
                .append(selectedInnovationArea, that.selectedInnovationArea)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(allSelected)
                .append(selectedAssessorIds)
                .append(selectedInnovationArea)
                .toHashCode();
    }
}
