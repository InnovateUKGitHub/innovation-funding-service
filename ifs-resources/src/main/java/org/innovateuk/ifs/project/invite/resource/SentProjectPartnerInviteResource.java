package org.innovateuk.ifs.project.invite.resource;

import org.innovateuk.ifs.invite.constant.InviteStatus;

import java.time.ZonedDateTime;

public class SentProjectPartnerInviteResource extends SendProjectPartnerInviteResource {

    private long id;
    private ZonedDateTime sentOn;
    private String projectName;
    private Long user;
    private InviteStatus status;
    private long applicationId;

    private SentProjectPartnerInviteResource() {}

    public SentProjectPartnerInviteResource(long id, ZonedDateTime sentOn, String projectName, Long user, InviteStatus status, String organisationName, String userName, String email, long applicationId) {
        super(organisationName, userName, email);
        this.id = id;
        this.sentOn = sentOn;
        this.projectName = projectName;
        this.user = user;
        this.status = status;
        this.applicationId = applicationId;
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

    public Long getUser() {
        return user;
    }

    public InviteStatus getStatus() {
        return status;
    }

    public long getApplicationId() {
        return applicationId;
    }
}
