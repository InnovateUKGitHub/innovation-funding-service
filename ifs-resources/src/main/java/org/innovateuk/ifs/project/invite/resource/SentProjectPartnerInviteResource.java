package org.innovateuk.ifs.project.invite.resource;

import java.time.ZonedDateTime;

public class SentProjectPartnerInviteResource extends SendProjectPartnerInviteResource {

    private long id;
    private ZonedDateTime sentOn;

    private SentProjectPartnerInviteResource() {}

    public SentProjectPartnerInviteResource(long id, ZonedDateTime sentOn, String organisationName, String userName, String email) {
        super(organisationName, userName, email);
        this.id = id;
        this.sentOn = sentOn;
    }

    public long getId() {
        return id;
    }

    public ZonedDateTime getSentOn() {
        return sentOn;
    }
}
