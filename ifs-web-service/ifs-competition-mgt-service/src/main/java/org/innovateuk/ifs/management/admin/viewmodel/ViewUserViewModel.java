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
public class ViewUserViewModel {

    private final UserResource user;
    private final UserResource loggedInUser;
    private final List<RoleProfileStatusResource> roleProfiles;
    private final boolean displayRoleProfileLink;


    public ViewUserViewModel(UserResource user, UserResource loggedInUser, List<RoleProfileStatusResource> roleProfiles, boolean displayRoleProfileLink) {
        this.user = user;
        this.loggedInUser = loggedInUser;
        this.roleProfiles = roleProfiles;
        this.displayRoleProfileLink = displayRoleProfileLink;
    }

    public UserResource getUser() {
        return user;
    }

    public boolean isIfsAdmin() {
        return loggedInUser.hasRole(Role.IFS_ADMINISTRATOR);
    }

    public boolean isSupport() {
        return loggedInUser.hasRole(Role.SUPPORT);
    }

    public boolean isDisplayRoleProfileLink() {
        return displayRoleProfileLink;
    }

    public boolean isDisplayAssessorTitle() {
        return !(isSupport() || isIfsAdmin());
    }
    /* view logic */
    public boolean isReadOnly() {
        boolean editable = isIfsAdmin()
                || loggedInUser.hasRole(Role.SUPPORT) && user.isExternalUser();
        return !editable;
    }

    public boolean isCanEditUserDetails() {
        return isIfsAdmin() && user.isInternalUser();
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

        ViewUserViewModel that = (ViewUserViewModel) o;

        return new EqualsBuilder()
                .append(displayRoleProfileLink, that.displayRoleProfileLink)
                .append(user, that.user)
                .append(loggedInUser, that.loggedInUser)
                .append(roleProfiles, that.roleProfiles)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(user)
                .append(loggedInUser)
                .append(roleProfiles)
                .append(displayRoleProfileLink)
                .toHashCode();
    }
}
