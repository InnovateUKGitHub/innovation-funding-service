package org.innovateuk.ifs.management.assessor.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.competition.resource.AvailableAssessorsSortFieldType;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;

import javax.validation.constraints.NotNull;

import static org.innovateuk.ifs.competition.resource.AvailableAssessorsSortFieldType.TITLE;

public class AvailableAssessorsForm extends BaseBindingResultTarget {

    @NotNull(message = "{validation.availableAssessorsForm.selectedSortField.required}")
    private AvailableAssessorsSortFieldType sortField = TITLE;

    public AvailableAssessorsSortFieldType getSortField() {
        return sortField;
    }

    public void setSortField(AvailableAssessorsSortFieldType sortField) {
        this.sortField = sortField;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AvailableAssessorsForm that = (AvailableAssessorsForm) o;

        return new EqualsBuilder()
                .append(sortField, that.sortField)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(sortField)
                .toHashCode();
    }
}
