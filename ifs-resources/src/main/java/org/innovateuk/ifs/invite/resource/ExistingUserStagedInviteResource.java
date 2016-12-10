package org.innovateuk.ifs.invite.resource;

public class ExistingUserStagedInviteResource extends StagedInviteResource {

    public ExistingUserStagedInviteResource() {
    }

    public ExistingUserStagedInviteResource(String email, long competitionId) {
        super(email, competitionId);
    }
}
