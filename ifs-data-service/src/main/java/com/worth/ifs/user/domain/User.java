package com.worth.ifs.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.security.crypto.password.StandardPasswordEncoder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User object for saving user details to the db. This is used so we can check authentication and authorization.
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private static final CharSequence PASSWORD_SECRET = "a02214f47a45171c";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;
    private String name;
    private String firstName;
    private String lastName;
    private String inviteName;
    private String phoneNumber;
    private String imageUrl;

    @Column(unique=true)
    private String token;



    @Column(unique=true)
    private String email;
    private String password;

    @OneToMany(mappedBy="user")
    private List<ProcessRole> processRoles = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name="user_organisation",
            joinColumns={@JoinColumn(name="user_id", referencedColumnName = "id")},
            inverseJoinColumns={@JoinColumn(name="organisation_id", referencedColumnName = "id")})
    private List<Organisation> organisations = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name="user_role",
            joinColumns={@JoinColumn(name="user_id", referencedColumnName = "id")},
            inverseJoinColumns={@JoinColumn(name="role_id", referencedColumnName = "id")})
    private List<Role> roles = new ArrayList<>();

    public User() {

    }

    public User(String name, String email, String password, String token, String imageUrl,
                List<ProcessRole> processRoles) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.token = token;
        this.imageUrl = imageUrl;
        this.processRoles = processRoles;
    }

    public User(Long id, String name, String email, String password, String token, String imageUrl,
                List<ProcessRole> processRoles) {
        this(name, email, password, token, imageUrl, processRoles);
        this.id = id;
    }


    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getToken() {
        return token;
    }

    @JsonIgnore
    public List<ProcessRole> getProcessRoles() {
        return processRoles;
    }

    @JsonIgnore
    public List<Organisation> getOrganisations() {
        return organisations;
    }

    public void setOrganisations(List<Organisation> organisations) {
        this.organisations = organisations;
    }

    public void addUserApplicationRole(ProcessRole... r){
        if(this.processRoles == null){
            this.processRoles = new ArrayList<>();
        }
        this.processRoles.addAll(Arrays.asList(r));
    }

    public Boolean passwordEquals(String passwordInput){
        StandardPasswordEncoder encoder = new StandardPasswordEncoder(PASSWORD_SECRET);
        return encoder.matches(passwordInput, this.password);
    }

    public void setPassword(String setPassword) {
        StandardPasswordEncoder encoder = new StandardPasswordEncoder(PASSWORD_SECRET);
        setPassword = encoder.encode(setPassword);
        this.password = setPassword;
    }

    public List<Role> getRoles() {
        return roles;
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

    public void setToken(String token) { this.token = token; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (id != null ? !id.equals(user.id) : user.id != null) return false;
        if (name != null ? !name.equals(user.name) : user.name != null) return false;
        if (imageUrl != null ? !imageUrl.equals(user.imageUrl) : user.imageUrl != null) return false;
        if (token != null ? !token.equals(user.token) : user.token != null) return false;
        if (email != null ? !email.equals(user.email) : user.email != null) return false;
        return !(password != null ? !password.equals(user.password) : user.password != null);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (imageUrl != null ? imageUrl.hashCode() : 0);
        result = 31 * result + (token != null ? token.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        return result;
    }


}
