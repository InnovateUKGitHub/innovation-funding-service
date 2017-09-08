package org.innovateuk.ifs.management.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;
import org.innovateuk.ifs.invite.resource.ParticipantStatusResource;

import java.util.ArrayList;
import java.util.List;

/**
 * Form for the selection of assessors on the Overview assessors tab
 */
public class OverviewSelectionForm extends BaseBindingResultTarget {

    private boolean allSelected;
    private List<Long> selectedAssessorIds;
    private Long selectedInnovationArea;
    private ParticipantStatusResource selectedStatus;
    private Boolean compliant;

    public OverviewSelectionForm() {
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

    public ParticipantStatusResource getSelectedStatus() {
        return selectedStatus;
    }

    public void setSelectedStatus(ParticipantStatusResource selectedStatus) {
        this.selectedStatus = selectedStatus;
    }

    public Boolean getCompliant() {
        return compliant;
    }

    public void setCompliant(Boolean compliant) {
        this.compliant = compliant;
    }

    public boolean anyFilterIsActive() {
        return selectedInnovationArea != null || selectedStatus != null || compliant != null;
    }

    public boolean anySelectionIsMade() {
        return this.allSelected || this.selectedAssessorIds.size() > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        OverviewSelectionForm that = (OverviewSelectionForm) o;

        return new EqualsBuilder()
                .append(allSelected, that.allSelected)
                .append(selectedAssessorIds, that.selectedAssessorIds)
                .append(selectedInnovationArea, that.selectedInnovationArea)
                .append(selectedStatus, that.selectedStatus)
                .append(compliant, that.compliant)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(allSelected)
                .append(selectedAssessorIds)
                .append(selectedInnovationArea)
                .append(selectedStatus)
                .append(compliant)
                .toHashCode();
    }
}

