package com.worth.ifs.application;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import java.io.Serializable;

public class InviteeForm implements Serializable {

    private Long userId;
    @NotEmpty
    private String personName;
    @NotEmpty
    @Email
    private String email;

    public InviteeForm(Long userId, String personName, String email) {
        this.userId = userId;
        this.personName = personName;
        this.email = email;
    }

    public InviteeForm() {
        this.personName = "";
        this.email = "";
    }

    public String getPersonName() {
        return personName;
    }

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
