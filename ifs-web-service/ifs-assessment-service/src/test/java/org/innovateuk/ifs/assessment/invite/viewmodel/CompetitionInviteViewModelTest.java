package org.innovateuk.ifs.assessment.invite.viewmodel;

import org.innovateuk.ifs.assessment.builder.CompetitionInviteResourceBuilder;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.invite.resource.CompetitionInviteResource;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CompetitionInviteViewModelTest {

    @Test
    public void testNonKtpCompetition() {
        String inviteHash = "invite-hash";

        CompetitionInviteResource competitionInviteResource = CompetitionInviteResourceBuilder.newCompetitionInviteResource()
                .withCompetitionFundingType(FundingType.GRANT)
                .build();

        CompetitionInviteViewModel viewModel = new CompetitionInviteViewModel(inviteHash, competitionInviteResource, false);

        assertFalse(viewModel.isKtpCompetition());
    }

    @Test
    public void testKtpCompetition() {
        String inviteHash = "invite-hash";

        CompetitionInviteResource competitionInviteResource = CompetitionInviteResourceBuilder.newCompetitionInviteResource()
                .withCompetitionFundingType(FundingType.KTP)
                .build();

        CompetitionInviteViewModel viewModel = new CompetitionInviteViewModel(inviteHash, competitionInviteResource, false);

        assertTrue(viewModel.isKtpCompetition());
    }
}
