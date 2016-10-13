package com.worth.ifs.user.resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.util.CollectionFunctions.simpleMap;

/**
 * User Data Transfer Object
 */
public class UserResource {
    private Long id;
    private String uid;
    private String title;
    private String firstName;
    private String lastName;
    private String inviteName;
    private String phoneNumber;
    private String imageUrl;
    private String email;
    private String password;
    private UserStatus status;
    private List<Long> organisations = new ArrayList<>();
    private List<Long> processRoles = new ArrayList<>();
    private List<RoleResource> roles = new ArrayList<>();
    private Gender gender;
    private Disability disability;
    private Long ethnicity;
    private Long profile;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
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

    public List<Long> getOrganisations() {
        return this.organisations;
    }

    public void setOrganisations(List<Long> organisationIds) {
        this.organisations = organisationIds;
    }

    public List<Long> getProcessRoles() {
        return processRoles;
    }

    public void setProcessRoles(List<Long> processRoles) {
        this.processRoles = processRoles;
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

    public Long getProfile() {
        return profile;
    }

    public void setProfile(Long profile) {
        this.profile = profile;
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
                .append(organisations, that.organisations)
                .append(processRoles, that.processRoles)
                .append(roles, that.roles)
                .append(gender, that.gender)
                .append(disability, that.disability)
                .append(ethnicity, that.ethnicity)
                .append(profile, that.profile)
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
                .append(organisations)
                .append(processRoles)
                .append(roles)
                .append(gender)
                .append(disability)
                .append(ethnicity)
                .append(profile)
                .toHashCode();
    }
}