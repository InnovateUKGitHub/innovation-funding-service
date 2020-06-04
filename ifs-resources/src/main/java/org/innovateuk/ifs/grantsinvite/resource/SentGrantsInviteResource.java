package org.innovateuk.ifs.grantsinvite.resource;

import org.innovateuk.ifs.invite.constant.InviteStatus;

import java.time.ZonedDateTime;

public class SentGrantsInviteResource extends GrantsInviteResource {

    private long id;
    private long applicationId;
    private String projectName;
    private Long user;
    private InviteStatus status;
    private ZonedDateTime sentOn;

    public SentGrantsInviteResource() {
    }

    public SentGrantsInviteResource(Long organisationId, String userName, String email, GrantsInviteRole grantsInviteRole, long id, long applicationId, String projectName, Long user, InviteStatus status, ZonedDateTime sentOn) {
        super(organisationId, userName, email, grantsInviteRole);
        this.id = id;
        this.applicationId = applicationId;
        this.projectName = projectName;
        this.user = user;
        this.status = status;
        this.sentOn = sentOn;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(long applicationId) {
        this.applicationId = applicationId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Long getUser() {
        return user;
    }

    public void setUser(Long user) {
        this.user = user;
    }

    public InviteStatus getStatus() {
        return status;
    }

    public void setStatus(InviteStatus status) {
        this.status = status;
    }

    public ZonedDateTime getSentOn() {
        return sentOn;
    }

    public void setSentOn(ZonedDateTime sentOn) {
        this.sentOn = sentOn;
    }
}
