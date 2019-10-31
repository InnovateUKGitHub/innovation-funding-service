package org.innovateuk.ifs.project.invite.resource;

import org.innovateuk.ifs.invite.constant.InviteStatus;

import java.time.ZonedDateTime;

public class SentProjectPartnerInviteResource extends SendProjectPartnerInviteResource {

    private long id;
    private ZonedDateTime sentOn;
    private String projectName;
    private Long existingUser;
    private InviteStatus status;

    private SentProjectPartnerInviteResource() {}

    public SentProjectPartnerInviteResource(long id, ZonedDateTime sentOn, String projectName, Long existingUser, InviteStatus status, String organisationName, String userName, String email) {
        super(organisationName, userName, email);
        this.id = id;
        this.sentOn = sentOn;
        this.projectName = projectName;
        this.existingUser = existingUser;
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public ZonedDateTime getSentOn() {
        return sentOn;
    }

    public String getProjectName() {
        return projectName;
    }

    public Long getExistingUser() {
        return existingUser;
    }

    public InviteStatus getStatus() {
        return status;
    }
}
