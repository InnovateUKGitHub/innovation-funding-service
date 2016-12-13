package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.invite.resource.CompetitionInviteResource;
import org.innovateuk.ifs.management.viewmodel.SendInviteViewModel;

/**
 * Created by peter.moreton on 12/12/2016.
 */
public class SendInvitePopulator {
    public SendInviteViewModel populatedModel(CompetitionInviteResource invite) {
        return new SendInviteViewModel(invite.getId(), invite.getCompetitionName(), "User name here");
    }
}
