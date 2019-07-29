package org.innovateuk.ifs.management.assessor.form;

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
    private List<Long> selectedInviteIds;
    private ParticipantStatusResource selectedStatus;
    private Boolean compliant;
    private String assessorName;

    public OverviewSelectionForm() {
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

    public String getAssessorName() {
        return assessorName;
    }

    public void setAssessorName(String assessorName) {
        this.assessorName = assessorName;
    }

    public boolean anyFilterIsActive() {
        return assessorName != null || selectedStatus != null || compliant != null;
    }

    public boolean anySelectionIsMade() {
        return this.allSelected || !this.selectedInviteIds.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        OverviewSelectionForm that = (OverviewSelectionForm) o;

        return new EqualsBuilder()
                .append(allSelected, that.allSelected)
                .append(selectedInviteIds, that.selectedInviteIds)
                .append(selectedStatus, that.selectedStatus)
                .append(compliant, that.compliant)
                .append(assessorName, that.assessorName)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(allSelected)
                .append(selectedInviteIds)
                .append(selectedStatus)
                .append(compliant)
                .append(assessorName)
                .toHashCode();
    }
}

