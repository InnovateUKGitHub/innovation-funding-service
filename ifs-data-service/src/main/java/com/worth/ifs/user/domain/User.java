package com.worth.ifs.user.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.security.crypto.password.StandardPasswordEncoder;

import javax.persistence.*;
import java.util.*;

/**
 * User object for saving user details to the db. This is used so we can check authentication and authorization.
 */

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private static final CharSequence PASSWORD_SECRET = "a02214f47a45171c";

    public User(Long id, String name, String email, String password, String token, String imageUrl,
                List<ProcessRole> processRoles) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.token = token;
        this.imageUrl = imageUrl;
        this.processRoles = processRoles;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToMany(mappedBy="user")
    private List<ProcessRole> processRoles = new ArrayList<>();
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

    @JsonIgnore
    public List<ProcessRole> getProcessRoles() {
        return processRoles;
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
}
