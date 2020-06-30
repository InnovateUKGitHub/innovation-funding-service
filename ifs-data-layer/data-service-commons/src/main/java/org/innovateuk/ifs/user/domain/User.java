package org.innovateuk.ifs.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.commons.util.AuditableEntity;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.Title;
import org.innovateuk.ifs.user.resource.UserStatus;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;
import static javax.persistence.EnumType.STRING;

/**
 * User object for saving user details to the db. This is used so we can check authentication and authorization.
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class User extends AuditableEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(STRING)
    private Title title;
    private String firstName;
    private String lastName;
    private String inviteName;
    private String phoneNumber;
    private String imageUrl;
    @Enumerated(STRING)
    private UserStatus status;

    @Column(unique = true)
    private String uid;

    @Column(unique = true)
    private String email;

    @CollectionTable(name = "user_role", joinColumns = {@JoinColumn(name="user_id")})
    @ElementCollection(targetClass = Role.class)
    @Column(name = "role_id", nullable = false)
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy="user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Affiliation> affiliations = new ArrayList<>();

    @OneToMany(mappedBy="user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RoleProfileStatus> roleProfileStatuses = new HashSet<>();

    @Column(name = "allow_marketing_emails")
    private boolean allowMarketingEmails = false;

    @Column(unique = true)
    private Long profileId;

    @ElementCollection
    @CollectionTable(name = "user_terms_and_conditions", joinColumns = {@JoinColumn(name="user_id")})
    @Column(name="terms_and_conditions_id")
    @OrderColumn(name="accepted_date")
    private Set<Long> termsAndConditionsIds = new LinkedHashSet<>();

    public User() {
        // no-arg constructor
    }

    public User(String firstName, String lastName, String email, String imageUrl,
                String uid) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.imageUrl = imageUrl;
        this.uid = uid;
    }

    public User(Long id, String firstName, String lastName, String email, String imageUrl,
                String uid) {
        this(firstName, lastName, email, imageUrl, uid);
        this.id = id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getUid() {
        return uid;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void addRole(Role role) {
        roles.add(role);
    }

    public boolean hasRole(Role type) {
        return getRoles().contains(type);
    }

    public void setRoles(Set<Role> roles) {
        requireNonNull(roles);
        this.roles = roles;
    }

    public Title getTitle() {
        return title;
    }

    public void setTitle(Title title) {
        this.title = title;
    }

    public String getName() {
        return Stream.of(this.getFirstName(), this.getLastName()).filter(StringUtils::isNotBlank).collect(joining(" "));
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
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

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return new EqualsBuilder()
                .append(id, user.id)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .toHashCode();
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public Set<Long> getTermsAndConditionsIds() {
        return termsAndConditionsIds;
    }

    public void setTermsAndConditionsIds(Set<Long> termsAndConditionsIds) {
        this.termsAndConditionsIds = termsAndConditionsIds;
    }

    public List<Affiliation> getAffiliations() {
        return affiliations;
    }

    public void setAffiliations(List<Affiliation> affiliations) {
        this.affiliations.clear();
        this.affiliations.addAll(affiliations);
    }

    public boolean hasId(Long id) {
        return this.id.equals(id);
    }

    public boolean isAllowMarketingEmails() {
        return allowMarketingEmails;
    }

    public void setAllowMarketingEmails(boolean allowMarketingEmails) {
        this.allowMarketingEmails = allowMarketingEmails;
    }

    public boolean isInternalUser() {
        return CollectionUtils.containsAny(Role.internalRoles(), roles);
    }

    public Set<RoleProfileStatus> getRoleProfileStatuses() {
        return roleProfileStatuses;
    }

    public void setRoleProfileStatuses(Set<RoleProfileStatus> roleProfileStatuses) {
        this.roleProfileStatuses = roleProfileStatuses;
    }

    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }
}
