package org.innovateuk.ifs.project.projectdetails.form;

import org.innovateuk.ifs.commons.validation.ValidationConstants;
import org.innovateuk.ifs.controller.BaseBindingResultTarget;
import org.innovateuk.ifs.invite.constant.InviteStatus;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;


/**
 * Form field model for the partner invitation content
 */
public class InviteeForm extends BaseBindingResultTarget {

    private Long userId;
    @javax.validation.constraints.NotBlank
    private String name;
    @NotBlank
    @Email(regexp = ValidationConstants.EMAIL_DISALLOW_INVALID_CHARACTERS_REGEX)
    private String email;
    private InviteStatus inviteStatus;

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
}

