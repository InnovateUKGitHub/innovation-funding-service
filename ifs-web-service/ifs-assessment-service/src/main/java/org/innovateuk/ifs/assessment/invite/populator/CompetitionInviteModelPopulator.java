package org.innovateuk.ifs.assessment.invite.populator;

import org.innovateuk.ifs.assessment.invite.viewmodel.CompetitionInviteViewModel;
import org.innovateuk.ifs.assessment.service.CompetitionInviteRestService;
import org.innovateuk.ifs.competition.publiccontent.resource.PublicContentItemResource;
import org.innovateuk.ifs.invite.resource.CompetitionInviteResource;
import org.innovateuk.ifs.publiccontent.service.PublicContentItemRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Build the model for the Competition Invitation view.
 */
@Component
public class CompetitionInviteModelPopulator extends InviteModelPopulator<CompetitionInviteViewModel> {

    @Autowired
    private CompetitionInviteRestService inviteRestService;

    @Autowired
    private PublicContentItemRestService publicContentItemRestService;

    @Override
    public CompetitionInviteViewModel populateModel(String inviteHash, boolean userLoggedIn) {
        CompetitionInviteResource invite = inviteRestService.openInvite(inviteHash).getSuccess();
        PublicContentItemResource publicContentItem = publicContentItemRestService.getItemByCompetitionId(invite.getCompetitionId()).getSuccess();

        return new CompetitionInviteViewModel(inviteHash, invite, userLoggedIn, publicContentItem.getPublicContentResource().getHash());
    }
}
