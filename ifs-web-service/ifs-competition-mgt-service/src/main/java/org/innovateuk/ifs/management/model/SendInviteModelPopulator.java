package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.invite.resource.AssessorInviteToSendResource;
import org.innovateuk.ifs.management.viewmodel.SendInviteViewModel;
import org.springframework.stereotype.Component;

/**
 * Populator for {@Link SendInviteViewModel}
 */
@Component
public class SendInviteModelPopulator {
    public SendInviteViewModel populateModel(long inviteId, AssessorInviteToSendResource invite) {
        return new SendInviteViewModel(invite.getCompetitionId(), inviteId, invite.getCompetitionName(), invite.getRecipient());
    }
}