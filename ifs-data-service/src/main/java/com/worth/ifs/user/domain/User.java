package com.worth.ifs.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.worth.ifs.user.resource.Disability;
import com.worth.ifs.user.resource.Gender;
import com.worth.ifs.user.resource.UserRoleType;
import com.worth.ifs.user.resource.UserStatus;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.worth.ifs.util.CollectionFunctions.simpleMap;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static javax.persistence.EnumType.STRING;

/**
 * User object for saving user details to the db. This is used so we can check authentication and authorization.
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;
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

    @OneToMany(mappedBy = "user")
    private List<ProcessRole> processRoles = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "user_organisation",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "organisation_id", referencedColumnName = "id")})
    private List<Organisation> organisations = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "user_role",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")})
    private List<Role> roles = new ArrayList<>();

    @Enumerated(STRING)
    private Gender gender;

    @Enumerated(STRING)
    private Disability disability;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="ethnicity_id", referencedColumnName = "id")
    private Ethnicity ethnicity;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true, optional = true)
    private Profile profile;

    @OneToMany(mappedBy="user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Affiliation> affiliations = new ArrayList<>();

    public User() {
        // no-arg constructor
    }

    public User(String firstName, String lastName, String email, String imageUrl,
                List<ProcessRole> processRoles, String uid) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.imageUrl = imageUrl;
        this.processRoles = processRoles;
        this.uid = uid;
    }

    public User(Long id, String firstName, String lastName, String email, String imageUrl,
                List<ProcessRole> processRoles, String uid) {
        this(firstName, lastName, email, imageUrl, processRoles, uid);
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

    @JsonIgnore
    public List<ProcessRole> getProcessRoles() {
        return processRoles;
    }

    @JsonIgnore
    public List<ProcessRole> getProcessRolesForRole(UserRoleType role) {
        return processRoles.stream().filter(processRole -> processRole.getRole().getName().equals(role.getName())).collect(toList());
    }

    @JsonIgnore
    public List<Organisation> getOrganisations() {
        return organisations;
    }

    public void setOrganisations(List<Organisation> organisations) {
        this.organisations = organisations;
    }

    public void addUserApplicationRole(ProcessRole... r) {
        if (this.processRoles == null) {
            this.processRoles = new ArrayList<>();
        }
        this.processRoles.addAll(asList(r));
    }

    public void addUserOrganisation(Organisation... orgs) {
        organisations = organisations == null ? new ArrayList<>() : organisations;
        asList(orgs).forEach(o -> {
            if (!organisations.stream().map(Organisation::getId).collect(toList()).contains(o.getId())){
                organisations.add(o);
            }
        });
    }

    public List<Role> getRoles() {
        return roles;
    }

    public boolean hasRole(UserRoleType type) {
        return simpleMap(getRoles(), Role::getName).contains(type.getName());
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
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
                .append(lastName);

        return stringBuilder.toString();
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

    public Ethnicity getEthnicity() {
        return ethnicity;
    }

    public void setEthnicity(Ethnicity ethnicity) {
        this.ethnicity = ethnicity;
    }
    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public List<Affiliation> getAffiliations() {
        return affiliations;
    }

    public void setAffiliations(List<Affiliation> affiliations) {
        this.affiliations.clear();
        this.affiliations.addAll(affiliations);
    }
}
