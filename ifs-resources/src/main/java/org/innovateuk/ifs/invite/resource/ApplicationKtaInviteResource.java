package org.innovateuk.ifs.invite.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.invite.constant.InviteStatus;

import java.time.ZonedDateTime;

public class ApplicationKtaInviteResource extends InviteResource {
    private Long id;
    private String email;
    private Long application;
    private InviteStatus status;
    private ZonedDateTime sentOn;

    public ApplicationKtaInviteResource(String email, Long application) {
        this.email = email;
        this.application = application;
    }

    public ApplicationKtaInviteResource() {
        // no-arg constructor
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getApplication() {
        return application;
    }

    public void setApplication(Long application) {
        this.application = application;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ApplicationKtaInviteResource that = (ApplicationKtaInviteResource) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(email, that.email)
                .append(application, that.application)
                .append(status, that.status)
                .append(sentOn, that.sentOn)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(email)
                .append(application)
                .append(status)
                .append(sentOn)
                .toHashCode();
    }
}
