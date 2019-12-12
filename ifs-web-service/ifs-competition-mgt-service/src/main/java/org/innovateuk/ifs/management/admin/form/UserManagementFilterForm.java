package org.innovateuk.ifs.management.admin.form;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;

public class UserManagementFilterForm extends BaseBindingResultTarget {
    private String filter;

    public UserManagementFilterForm() {
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        UserManagementFilterForm that = (UserManagementFilterForm) o;

        return new EqualsBuilder()
                .append(filter, that.filter)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(filter)
                .toHashCode();
    }
}