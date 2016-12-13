package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.invite.resource.CompetitionInviteResource;
import org.innovateuk.ifs.management.viewmodel.SendInviteViewModel;
import org.springframework.stereotype.Component;

/**
 * Created by peter.moreton on 12/12/2016.
 */
@Component
public class SendInvitePopulator {
    public SendInviteViewModel populateModel(CompetitionInviteResource invite) {
        return new SendInviteViewModel(invite.getId(), 52L, invite.getCompetitionName(), "User name here");
    }
}
