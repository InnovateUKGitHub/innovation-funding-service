package org.innovateuk.ifs.user.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.util.StringUtils;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.disjoint;
import static java.util.Comparator.comparing;
import static org.innovateuk.ifs.user.resource.Role.IFS_ADMINISTRATOR;
import static org.innovateuk.ifs.user.resource.Role.internalRoles;

/**
 * User Data Transfer Object
 */
public class UserResource {
    private Long id;
    private String uid;
    private Title title;
    private String firstName;
    private String lastName;
    private String inviteName;
    private String phoneNumber;
    private String imageUrl;
    private String email;
    private String password;
    private UserStatus status;
    private List<Role> roles = new ArrayList<>();
    private Gender gender;
    private Disability disability;
    private Long ethnicity;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        roles.sort(comparing(Role::getId));
        this.roles = roles;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public boolean hasRole(Role role) {
        return roles.contains(role);
    }

    @JsonIgnore
    public boolean isInternalUser() {
        return CollectionUtils.containsAny(internalRoles(), roles);
    }

    public boolean hasRoles(Role... acceptedRoles) {
        return roles.containsAll(newHashSet(acceptedRoles));
    }

    public boolean hasAnyRoles(Role... acceptedRoles) {
        return !disjoint(roles, newHashSet(acceptedRoles));
    }

    public boolean hasAnyRoles(Collection<Role> testRoles) {
        return !disjoint(roles, newHashSet(testRoles));
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Disability getDisability() {
        return disability;
    }

    public void setDisability(Disability disability) {
        this.disability = disability;
    }

    public Long getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(Long ethnicity) {
        this.ethnicity = ethnicity;
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
    public String getRolesString(){
        //TODO: Replace and simplify this once IFS-656 is implemented
        if (hasRole(IFS_ADMINISTRATOR)) {
            return IFS_ADMINISTRATOR.getDisplayName();
        } else {    // Most are not yet hierarchical so in most cases this will also return single role at present.
            return roles.stream()
                    .map(Role::getDisplayName)
                    .collect(Collectors.joining(", "));
        }
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
                .append(inviteName, that.inviteName)
                .append(phoneNumber, that.phoneNumber)
                .append(imageUrl, that.imageUrl)
                .append(email, that.email)
                .append(password, that.password)
                .append(status, that.status)
                .append(roles, that.roles)
                .append(gender, that.gender)
                .append(disability, that.disability)
                .append(ethnicity, that.ethnicity)
                .append(profileId, that.profileId)
                .append(termsAndConditionsIds, that.termsAndConditionsIds)
                .append(createdOn, that.createdOn)
                .append(createdBy, that.createdBy)
                .append(modifiedOn, that.modifiedOn)
                .append(modifiedBy, that.modifiedBy)
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
                .append(inviteName)
                .append(phoneNumber)
                .append(imageUrl)
                .append(email)
                .append(password)
                .append(status)
                .append(roles)
                .append(gender)
                .append(disability)
                .append(ethnicity)
                .append(profileId)
                .append(allowMarketingEmails)
                .append(termsAndConditionsIds)
                .append(createdOn)
                .append(createdBy)
                .append(modifiedOn)
                .append(modifiedBy)
                .toHashCode();
    }
}
