package com.worth.ifs.application;

import java.io.Serializable;

public class InviteeForm implements Serializable {
    public Long userId;
    public String personName;
    public String email;

    public InviteeForm(Long userId, String personName, String email) {
        this.userId = userId;
        this.personName = personName;
        this.email = email;
    }

    public InviteeForm() {
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
