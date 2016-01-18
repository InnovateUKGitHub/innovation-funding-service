package com.worth.ifs.user.resource;

import com.worth.ifs.user.domain.Organisation;
import com.worth.ifs.user.domain.User;

import java.util.ArrayList;
import java.util.List;

import static com.worth.ifs.util.CollectionFunctions.simpleMap;

/**
 * User Data Transfer Object
 */
public class UserResource {
    private Long id;
    private String title;
    private String name;
    private String firstName;
    private String lastName;
    private String inviteName;
    private String phoneNumber;
    private String imageUrl;
    private String email;
    private String password;
    private List<Long> organisationIds = new ArrayList<>();

    public UserResource() {
    }

    public UserResource(User user) {
        id = user.getId();
        title = user.getTitle();
        name = user.getName();
        firstName = user.getFirstName();
        lastName = user.getLastName();
        inviteName = user.getInviteName();
        phoneNumber = user.getPhoneNumber();
        imageUrl = user.getImageUrl();
        email = user.getEmail();
        password = user.getEmail();
        organisationIds = simpleMap(user.getOrganisations(), Organisation::getId);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
