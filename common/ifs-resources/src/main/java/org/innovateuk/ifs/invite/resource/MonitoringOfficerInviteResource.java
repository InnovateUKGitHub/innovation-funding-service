package org.innovateuk.ifs.invite.resource;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.invite.constant.InviteStatus;

public class MonitoringOfficerInviteResource {
    private long id;
    private String hash;
    private String email;
    private InviteStatus status;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public InviteStatus getStatus() {
        return status;
    }

    public void setStatus(InviteStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        MonitoringOfficerInviteResource that = (MonitoringOfficerInviteResource) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(hash, that.hash)
                .append(email, that.email)
                .append(status, that.status)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(hash)
                .append(email)
                .append(status)
                .toHashCode();
    }
}