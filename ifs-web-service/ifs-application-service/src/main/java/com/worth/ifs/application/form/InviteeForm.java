package com.worth.ifs.application.form;

import com.worth.ifs.commons.validation.ValidationConstants;
import com.worth.ifs.invite.constant.InviteStatus;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Size;
import java.io.Serializable;

public class InviteeForm implements Serializable {
    private static final long serialVersionUID = 8494848676778443648L;

    private Long userId;
    @NotEmpty (message="{validation.field.must.not.be.blank}")
    private String personName;
    @NotEmpty (message="{validation.invite.email.required}")
    @Email(regexp = ValidationConstants.EMAIL_DISALLOW_INVALID_CHARACTERS_REGEX, message="{validation.standard.email.format}")
    @Size(max = 256, message = "{validation.standard.email.length.max}")
    private String email;
    private InviteStatus inviteStatus;

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

    public void setInviteStatus(InviteStatus inviteStatus) {
        this.inviteStatus = inviteStatus;
    }

    public InviteStatus getInviteStatus() {
        return inviteStatus;
    }
}
