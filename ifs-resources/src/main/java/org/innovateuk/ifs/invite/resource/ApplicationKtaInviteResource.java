package org.innovateuk.ifs.invite.resource;

public class ApplicationKtaInviteResource extends InviteResource {

    private String email;
    private Long application;

    public ApplicationKtaInviteResource(String email, Long application) {
        this.email = email;
        this.application = application;
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
}
