package org.innovateuk.ifs.management.admin.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.user.resource.UserResource;

/**
 * A view model for serving page listing users to be managed by IFS Administrators
 */
public class EditUserViewModel {

    private final UserResource user;
    private final boolean ifsAdmin;

    public EditUserViewModel(UserResource user, boolean ifsAdmin) {
        this.user = user;
        this.ifsAdmin = ifsAdmin;
    }

    public UserResource getUser() {
        return user;
    }

    public boolean isIfsAdmin() {
        return ifsAdmin;
    }

    public boolean isReadOnly() {
        return !ifsAdmin && !user.isExternalUser();
    }

    public boolean isCanEditUserDetails() {
        return ifsAdmin && user.isInternalUser();
    }

    public boolean isInternal() {
        return user.isInternalUser();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        EditUserViewModel that = (EditUserViewModel) o;

        return new EqualsBuilder()
                .append(ifsAdmin, that.ifsAdmin)
                .append(user, that.user)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(user)
                .append(ifsAdmin)
                .toHashCode();
    }
}
