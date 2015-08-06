package com.worth.ifs.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
    public User(long id, String name, String email, String password, String token, String imageUrl, List<UserApplicationRole> userApplicationRoles) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.token = token;
        this.imageUrl = imageUrl;
        this.userApplicationRoles = userApplicationRoles;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToMany(mappedBy="user")
    private List<UserApplicationRole> userApplicationRoles = new ArrayList<UserApplicationRole>();

    private String name;
    private String imageUrl;

    @Column(unique=true)
    private String token;

    public String getEmail() {
        return email;
    }

    @Column(unique=true)
    private String email;

    private String password;

    public User(){

    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getToken() {
        return token;
    }

    public List<UserApplicationRole> getUserApplicationRoles() {
        return userApplicationRoles;
    }

    public void addUserApplicationRole(UserApplicationRole... r){
        if(this.userApplicationRoles == null){
            this.userApplicationRoles = new ArrayList<>();
        }
        this.userApplicationRoles.addAll(Arrays.asList(r));
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
