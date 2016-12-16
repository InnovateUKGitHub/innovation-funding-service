package org.innovateuk.ifs.invite.resource;

import javax.validation.Valid;
import java.util.List;

public class NewUserStagedInvitesResource {

    @Valid
    private List<NewUserStagedInviteResource> invites;

    public NewUserStagedInvitesResource() {}

    public NewUserStagedInvitesResource(List<NewUserStagedInviteResource> invites) {
        this.invites = invites;
    }

    public List<NewUserStagedInviteResource> getInvites() {
        return invites;
    }

    public void setInvites(List<NewUserStagedInviteResource> invites) {
        this.invites = invites;
    }
}
