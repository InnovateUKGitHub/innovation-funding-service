package org.innovateuk.ifs.invite.populator;

import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.ApplicationKtaInviteResource;
import org.innovateuk.ifs.invite.viewmodel.AcceptRejectApplicationKtaInviteViewModel;
import org.junit.Test;

import static org.innovateuk.ifs.invite.builder.ApplicationKtaInviteResourceBuilder.newApplicationKtaInviteResource;
import static org.junit.Assert.assertEquals;

public class AcceptRejectApplicationKtaInviteModelPopulatorTest {

    @Test
    public void populateModel() {
        long applicationId= 123L;
        ApplicationKtaInviteResource invite = newApplicationKtaInviteResource()
                .withApplication(applicationId)
                .withApplicationName("KTP Application")
                .withCompetitionName("KTP Competition")
                .withLeadApplicant("Steve Smith")
                .withLeadOrganisationName("Empire Ltd")
                .withHash("hash123")
                .withStatus(InviteStatus.SENT)
                .build();

        AcceptRejectApplicationKtaInviteViewModel model =
                new AcceptRejectApplicationKtaInviteModelPopulator().populateModel(invite);

        assertEquals(invite.getCompetitionName(), model.getCompetitionName());
        assertEquals(invite.getLeadOrganisationName(), model.getLeadOrganisationName());
        assertEquals(invite.getLeadApplicant(), model.getLeadApplicantName());
        assertEquals(invite.getApplicationName(), model.getApplicationName());
        assertEquals(invite.getApplication(), model.getApplicationId());
        assertEquals(invite.getHash(), model.getHash());
    }
}