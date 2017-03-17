package org.innovateuk.ifs.project.spendprofile.form;

import org.innovateuk.ifs.controller.BaseBindingResultTarget;
import org.innovateuk.ifs.project.resource.SpendProfileTableResource;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Form field model for the spend profile
 */
public class SpendProfileForm  extends BaseBindingResultTarget {

    private SpendProfileTableResource table;

    // for spring form binding
    public SpendProfileForm() {

    }

    public SpendProfileTableResource getTable() {
        return table;
    }

    public void setTable(SpendProfileTableResource table) {
        this.table = table;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        SpendProfileForm that = (SpendProfileForm) o;

        return new EqualsBuilder()
                .append(table, that.table)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(table)
                .toHashCode();
    }
}
