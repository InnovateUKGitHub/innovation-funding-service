package org.innovateuk.ifs.user.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.asList;
import static java.util.Collections.disjoint;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;
import static org.innovateuk.ifs.user.resource.Role.*;

/**
 * User Data Transfer Object
 * Serializable so that it can be persisted in a redis cache.
 */
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

    public UserResource() {
        // no-arg constructor
    }

    public UserResource(String uid) {
        this.uid = uid;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Title getTitle() {
        return title;
    }

    public void setTitle(Title title) {
        this.title = title;
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getInviteName() {
        return inviteName;
    }

    public void setInviteName(String inviteName) {
        this.inviteName = inviteName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Role> getRoles() {
        return roles;
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

    public void setStatus(UserStatus status) {
        this.status = status;
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
        return CollectionUtils.containsAny(internalRoles(), roles);
    }

    @JsonIgnore
    public boolean isExternalUser() {
        return CollectionUtils.containsAny(externalRoles(), roles);
    }

    public boolean hasAnyRoles(Role... acceptedRoles) {
        return !disjoint(roles, newHashSet(acceptedRoles));
    }

    public boolean hasAnyRoles(Collection<Role> testRoles) {
        return !disjoint(roles, newHashSet(testRoles));
    }

    public boolean hasRoles(Role... acceptedRoles){
        return roles.containsAll(newHashSet(acceptedRoles));
    }

    public boolean hasMoreThanOneRoleOf(Role... acceptedRoles){
        return CollectionUtils.retainAll(roles, asList(acceptedRoles)).size() > 1;
    }

    public boolean hasMoreThanOneRoleOf(Collection<Role> acceptedRoles){
        return CollectionUtils.retainAll(roles, acceptedRoles).size() > 1;
    }

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public boolean getAllowMarketingEmails() {
        return allowMarketingEmails;
    }

    public void setAllowMarketingEmails(boolean allowMarketingEmails) {
        this.allowMarketingEmails = allowMarketingEmails;
    }

    public Set<Long> getTermsAndConditionsIds() {
        return termsAndConditionsIds;
    }

    public void setTermsAndConditionsIds(Set<Long> termsAndConditionsIds) {
        this.termsAndConditionsIds = termsAndConditionsIds;
    }

    public ZonedDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(ZonedDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public ZonedDateTime getModifiedOn() {
        return modifiedOn;
    }

    public void setModifiedOn(ZonedDateTime modifiedOn) {
        this.modifiedOn = modifiedOn;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    /**
     * Currently only used for IFS-605 to display role for internal users
     * @return Single role display string. (may show comma separated roles if multiple exist.  Except for IFS_Administrator
     * See IFS-656.
     */
    @JsonIgnore
    public String getRolesString() {
        return roles.stream()
                .map(Role::getDisplayName)
                .collect(joining(", "));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final UserResource that = (UserResource) o;

        return new EqualsBuilder()
                .append(allowMarketingEmails, that.allowMarketingEmails)
                .append(id, that.id)
                .append(uid, that.uid)
                .append(title, that.title)
                .append(firstName, that.firstName)
                .append(lastName, that.lastName)
                .append(phoneNumber, that.phoneNumber)
                .append(email, that.email)
                .append(status, that.status)
                .append(roles, that.roles)
                .append(profileId, that.profileId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(uid)
                .append(title)
                .append(firstName)
                .append(lastName)
                .append(phoneNumber)
                .append(email)
                .append(status)
                .append(roles)
                .append(profileId)
                .append(allowMarketingEmails)
                .toHashCode();
    }
}