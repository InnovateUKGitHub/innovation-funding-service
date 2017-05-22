package org.innovateuk.ifs.management.model;

import org.innovateuk.ifs.invite.resource.AssessorInvitesToSendResource;
import org.innovateuk.ifs.management.viewmodel.SendInviteViewModel;
import org.springframework.stereotype.Component;

/**
 * Populator for {@link SendInviteViewModel}
 */
@Component
public class SendInviteModelPopulator {

    public SendInviteViewModel populateModel(AssessorInvitesToSendResource invite) {
        return new SendInviteViewModel(
                invite.getCompetitionId(),
                invite.getCompetitionName(),
                invite.getRecipients(),
                invite.getContent()
        );
    }
}