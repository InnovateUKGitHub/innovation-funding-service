package org.innovateuk.ifs.user.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static org.innovateuk.ifs.util.CollectionFunctions.simpleMap;
import static java.util.stream.Collectors.toList;

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
    private List<RoleResource> roles = new ArrayList<>();
    private Gender gender;
    private Disability disability;
    private Long ethnicity;
    private Long profileId;
    private boolean allowMarketingEmails;

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

        stringBuilder
                .append(lastName)
                .toString();

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

    public List<RoleResource> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleResource> roles) {
        this.roles = roles;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public boolean hasRole(UserRoleType role) {
        return simpleMap(roles, RoleResource::getName).contains(role.getName());
    }

    public boolean hasRoles(UserRoleType... acceptedRoles) {
        return roles.stream().map(role -> UserRoleType.fromName(role.getName())).collect(toList()).containsAll(Sets.newHashSet(acceptedRoles));
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UserResource that = (UserResource) o;

        return new EqualsBuilder()
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
                .append(allowMarketingEmails, that.allowMarketingEmails)
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
                .toHashCode();
    }
}
