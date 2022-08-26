package org.innovateuk.ifs.assessment.invite.viewmodel;

import org.innovateuk.ifs.assessment.builder.CompetitionInviteResourceBuilder;
import org.innovateuk.ifs.competition.publiccontent.resource.FundingType;
import org.innovateuk.ifs.invite.resource.CompetitionInviteResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.KTP;
import static org.innovateuk.ifs.competition.publiccontent.resource.FundingType.KTP_AKT;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class KtpCompetitionInviteViewModelTest {

    private final FundingType fundingType;

    @Parameterized.Parameters(name = "{index}: FundingType->{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[] [] {
                {KTP}, {KTP_AKT}
        });
    }

    public KtpCompetitionInviteViewModelTest(FundingType fundingType) {
        this.fundingType = fundingType;
    }

    @Test
    public void testKtpCompetition() {
        String inviteHash = "invite-hash";

        CompetitionInviteResource competitionInviteResource = CompetitionInviteResourceBuilder.newCompetitionInviteResource()
                .withCompetitionFundingType(fundingType)
                .build();

        CompetitionInviteViewModel viewModel = new CompetitionInviteViewModel(inviteHash, competitionInviteResource, false, null);

        assertTrue(viewModel.isKtpCompetition());
        assertFalse(viewModel.isAlwaysOpenCompetition());
    }
}
