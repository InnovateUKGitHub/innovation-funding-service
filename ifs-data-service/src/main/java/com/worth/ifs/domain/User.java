package com.worth.ifs.domain;

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

    public User(Long id, String name, String email, String password, String token, String imageUrl, List<UserApplicationRole> userApplicationRoles) {
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
    private Long id;

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

    public Long getId() {
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

    public Boolean passwordEquals(String passwordInput){
        StandardPasswordEncoder encoder = new StandardPasswordEncoder(PASSWORD_SECRET);
        return encoder.matches(passwordInput, this.password);
    }

    public void setPassword(String setPassword) {
        StandardPasswordEncoder encoder = new StandardPasswordEncoder(PASSWORD_SECRET);
        setPassword = encoder.encode(setPassword);
        this.password = setPassword;
    }
}
