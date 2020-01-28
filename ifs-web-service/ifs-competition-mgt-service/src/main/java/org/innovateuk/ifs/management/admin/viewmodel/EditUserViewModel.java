package org.innovateuk.ifs.management.admin.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.user.resource.ProfileRole;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.RoleProfileStatusResource;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.List;
import java.util.Optional;

/**
 * A view model for serving page listing users to be managed by IFS Administrators
 */
public class EditUserViewModel {

    private final UserResource user;
    private final List<RoleProfileStatusResource> roleProfiles;
    private final boolean ifsAdmin;
    private final boolean displayRoleProfileLink;


    public EditUserViewModel(UserResource user, List<RoleProfileStatusResource> roleProfiles, boolean ifsAdmin, boolean displayRoleProfileLink) {
        this.user = user;
        this.roleProfiles = roleProfiles;
        this.ifsAdmin = ifsAdmin;
        this.displayRoleProfileLink = displayRoleProfileLink;
    }

    public UserResource getUser() {
        return user;
    }

    public boolean isIfsAdmin() {
        return ifsAdmin;
    }

    public boolean isDisplayRoleProfileLink() {
        return displayRoleProfileLink;
    }

    /* view logic */
    public boolean isReadOnly() {
        return !ifsAdmin && !user.isExternalUser();
    }

    public boolean isCanEditUserDetails() {
        return ifsAdmin && user.isInternalUser();
    }

    public boolean isInternal() {
        return user.isInternalUser();
    }

    public String roleDisplay(Role role) {
        if (displayRoleProfileLink && role.isAssessor()) {
            Optional<RoleProfileStatusResource> maybeProfileStatus = roleProfiles.stream()
                    .filter(status -> status.getProfileRole().equals(ProfileRole.ASSESSOR))
                    .findFirst();
            if (maybeProfileStatus.isPresent()) {
                return maybeProfileStatus.get().getRoleProfileState().getDescription();
            } else {
                return "Available";
            }
        }
        return "Active";
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        EditUserViewModel that = (EditUserViewModel) o;

        return new EqualsBuilder()
                .append(ifsAdmin, that.ifsAdmin)
                .append(displayRoleProfileLink, that.displayRoleProfileLink)
                .append(user, that.user)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(user)
                .append(ifsAdmin)
                .append(displayRoleProfileLink)
                .toHashCode();
    }
}
