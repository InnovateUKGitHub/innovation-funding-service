package org.innovateuk.ifs.management.admin.viewmodel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.user.resource.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.innovateuk.ifs.user.resource.Role.SUPPORTER;
import static org.innovateuk.ifs.user.resource.Role.externalRolesToInvite;

/**
 * A view model for serving page listing users to be managed by IFS Administrators
 */
public class ViewUserViewModel {

    private final UserResource user;
    private final UserResource loggedInUser;
    private final List<RoleProfileStatusResource> roleProfiles;
    private final boolean externalRoleLinkEnabled;
    private Set<Role> roles;

    public ViewUserViewModel(UserResource user, UserResource loggedInUser, List<RoleProfileStatusResource> roleProfiles, boolean externalRoleLinkEnabled, Set<Role> roles) {
        this.user = user;
        this.loggedInUser = loggedInUser;
        this.roleProfiles = roleProfiles;
        this.externalRoleLinkEnabled = externalRoleLinkEnabled;
        this.roles = roles;
    }

    public UserResource getUser() {
        return user;
    }

    public boolean isIfsAdmin() {
        return loggedInUser.hasAuthority(Authority.IFS_ADMINISTRATOR);
    }

    public boolean isSupport() {
        return loggedInUser.hasRole(Role.SUPPORT);
    }

    public boolean isExternalRoleLinkEnabled() {
        return externalRoleLinkEnabled;
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

    public boolean isLinkVisibleToIfsAdmin() {
        return isIfsAdmin() && !user.getRoles().stream().anyMatch(externalRolesToInvite()::contains) && isExternalRoleLinkEnabled();
    }

    public boolean isCanEditUserDetails() {
        return isIfsAdmin() && user.isInternalUser();
    }

    public boolean isInternal() {
        return user.isInternalUser();
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String roleDisplay(Role role) {
        if (role.isAssessor()) {
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

    public boolean isOrganisationVisible() {
        return this.user.getRoles().contains(SUPPORTER);
    }

    @JsonIgnore
    public UserStatus getActiveUserStatus() {
        return UserStatus.ACTIVE;
    }

    @JsonIgnore
    public Boolean isInactiveUser() {
        return user.getStatus().equals(UserStatus.INACTIVE);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ViewUserViewModel that = (ViewUserViewModel) o;

        return new EqualsBuilder()
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
                .toHashCode();
    }
}
