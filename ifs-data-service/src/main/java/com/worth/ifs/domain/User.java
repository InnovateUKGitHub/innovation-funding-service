package com.worth.ifs.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;

/**
 * User object for saving user details to the db. This is used so we can check authentication and authorization.
 */
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;


    private String name;
    private String imageUrl;

    @Column(unique=true)
    private String token;


    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getToken() {
        return token;
    }
}
