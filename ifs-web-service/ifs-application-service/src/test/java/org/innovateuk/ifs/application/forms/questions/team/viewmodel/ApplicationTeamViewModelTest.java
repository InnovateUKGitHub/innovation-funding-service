package org.innovateuk.ifs.application.forms.questions.team.viewmodel;

import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.ApplicationKtaInviteResource;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import static org.innovateuk.ifs.invite.builder.ApplicationKtaInviteResourceBuilder.newApplicationKtaInviteResource;
import static org.junit.Assert.assertEquals;

public class ApplicationTeamViewModelTest {

    private ApplicationTeamViewModel model;

    @Test
    public void pendingDays() {
        // given
        ZonedDateTime fiveDaysAgo = ZonedDateTime.now().minus(5, ChronoUnit.DAYS);
        ApplicationKtaInviteResource invite = newApplicationKtaInviteResource()
                .withStatus(InviteStatus.SENT)
                .withSentOn(fiveDaysAgo)
                .build();
        model = new ApplicationTeamViewModel(1, null, null, 1, null, 1,
                false, false, false, false, true, invite, null, null);

        // when
        long pendingDays = model.getKtaInvitePendingDays();

        // then
        assertEquals(5L, pendingDays);
    }
}
