package com.worth.ifs.project.form;

// this class defines the form for inviting new users to the project

import java.io.Serializable;

import com.worth.ifs.commons.validation.ValidationConstants;
import com.worth.ifs.invite.constant.InviteStatus;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

public class InviteeForm implements Serializable {
    private static final long serialVersionUID = 8494848676778443648L;

    private Long userId;
    @NotEmpty
    private String name;
    @NotEmpty
    @Email(regexp = ValidationConstants.EMAIL_DISALLOW_INVALID_CHARACTERS_REGEX)
    private String email;
    private InviteStatus inviteStatus;

    private String emailExistsError;

    public InviteeForm(Long userId, String name, String email) {
        this.userId = userId;
        this.name = name;
        this.email = email;
    }

    public InviteeForm() {
        this.name = "";
        this.email = "";
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

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setInviteStatus(InviteStatus inviteStatus) {
        this.inviteStatus = inviteStatus;
    }

    public InviteStatus getInviteStatus() {
        return inviteStatus;
    }

    public String getEmailExistsError ()  {
        return emailExistsError;
    }

    public void setEmailExistsError (String emailExistsError)  { this.emailExistsError = emailExistsError; }
}

