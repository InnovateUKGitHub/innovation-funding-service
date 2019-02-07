package org.innovateuk.ifs.invite.resource;


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
}