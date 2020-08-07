package org.innovateuk.ifs.management.admin.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.innovateuk.ifs.management.admin.form.InviteUserView;

public class InviteUserViewModel {

    private InviteUserView type;

    public InviteUserViewModel(InviteUserView type) {
        this.type = type;
    }

    public InviteUserView getType() {
        return type;
    }

    public void setType(InviteUserView type) {
        this.type = type;
    }

    public String getTypeName() {
        return type.getName();
    }

    public boolean isInternal() {
        return type.equals(InviteUserView.INTERNAL_USER);
    }

    public boolean isExternal() {
        return type.equals(InviteUserView.EXTERNAL_USER);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(type)
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (obj == null || getClass() != obj.getClass()) return false;

        InviteUserViewModel that = (InviteUserViewModel) obj;

        return new EqualsBuilder()
                .append(type, this.type)
                .isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("type", type)
                .toString();
    }
}
