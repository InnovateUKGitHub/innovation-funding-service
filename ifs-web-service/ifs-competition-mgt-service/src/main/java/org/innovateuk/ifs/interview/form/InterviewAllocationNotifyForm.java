package org.innovateuk.ifs.interview.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.management.form.SendInviteForm;

import java.util.ArrayList;
import java.util.List;

/**
 * Form for notifying assessors of allocated interview applications
 */
public class InterviewAllocationNotifyForm extends SendInviteForm {

    private List<Long> selectedIds;

    public InterviewAllocationNotifyForm() {
        this.selectedIds = new ArrayList<>();
    }

    public InterviewAllocationNotifyForm(List<Long> selectedIds) {
        this.selectedIds = selectedIds;
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

        InterviewAllocationNotifyForm that = (InterviewAllocationNotifyForm) o;

        return new EqualsBuilder()
                .append(selectedIds, that.selectedIds)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(selectedIds)
                .toHashCode();
    }
}