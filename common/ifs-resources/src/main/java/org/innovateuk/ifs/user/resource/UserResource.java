package org.innovateuk.ifs.user.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.disjoint;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;
import static org.innovateuk.ifs.user.resource.Role.*;

/**
 * User Data Transfer Object
 * Serializable so that it can be persisted in a redis cache.
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
public class UserResource implements Serializable {
    private static final long serialVersionUID = 746809237007138492L;
    private Long id;
    private String uid;
    private Title title;
    private String firstName;
    private String lastName;
    private String inviteName;
    private String phoneNumber;
    private String imageUrl;
    private String email;
    private UserStatus status;
    private List<Role> roles = new ArrayList<>();
    private Long profileId;
    private boolean allowMarketingEmails;
    private Set<Long> termsAndConditionsIds;
    private ZonedDateTime createdOn;
    private String createdBy;
    private ZonedDateTime modifiedOn;
    private String modifiedBy;
    private EDIStatus ediStatus;
    private ZonedDateTime ediReviewDate;


    public UserResource(String uid) {
        this.uid = uid;
    }


    @JsonIgnore
    public String getName() {
        StringBuilder stringBuilder = new StringBuilder();
        if (StringUtils.hasText(firstName)) {
            stringBuilder.append(firstName)
                    .append(" ");
        }

        if (StringUtils.hasText(lastName)) {
            stringBuilder
                    .append(lastName);
        }

        return stringBuilder.toString();
    }


    @JsonIgnore
    public String getRoleDisplayNames() {
        return roles.stream().map(Role::getDisplayName).collect(joining(", "));
    }

    public void setRoles(List<Role> roles) {
        roles.sort(comparing(Role::getId));
        this.roles = roles;
    }

    public UserStatus getStatus() {
        return status;
    }

    @JsonIgnore
    public String getStatusDisplay() {
        switch (getStatus()) {
            case ACTIVE:
                return "Active";
            case INACTIVE:
            case PENDING:
                return "Inactive";
            default:
                return "";
        }
    }

    public boolean hasRole(Role role) {
        return roles.contains(role);
    }

    public boolean hasAuthority(Authority auth) {
        return roles.stream().flatMap(r -> r.getAuthorities().stream()).anyMatch(a -> a == auth);
    }

    public boolean hasAnyAuthority(List<Authority> auth) {
        return roles.stream().flatMap(r -> r.getAuthorities().stream()).anyMatch(auth::contains);
    }

    @JsonIgnore
    public boolean isInternalUser() {
        return internalRoles().stream().anyMatch(roles::contains);
    }

    @JsonIgnore
    public boolean isExternalUser() {
        return externalRoles().stream().anyMatch(roles::contains);
    }

    @JsonIgnore
    public boolean isReadOnlyUser() {
        return readOnlyRoles().stream().anyMatch(roles::contains);
    }

    public boolean hasAnyRoles(Role... acceptedRoles) {
        return !disjoint(roles, newHashSet(acceptedRoles));
    }

    public boolean hasAnyRoles(Collection<Role> testRoles) {
        return !disjoint(roles, newHashSet(testRoles));
    }

    public boolean hasRoles(Role... acceptedRoles) {
        return roles.containsAll(newHashSet(acceptedRoles));
    }


    /**
     * Currently only used for IFS-605 to display role for internal users
     *
     * @return Single role display string. (may show comma separated roles if multiple exist.  Except for IFS_Administrator
     * See IFS-656.
     */
    @JsonIgnore
    public String getRolesString() {
        return roles.stream()
                .map(Role::getDisplayName)
                .collect(joining(", "));
    }


}