package org.innovateuk.ifs.application.forms.questions.team.viewmodel;

import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.ApplicationKtaInviteResource;
import org.innovateuk.ifs.organisation.resource.HeukarPartnerOrganisationResource;
import org.innovateuk.ifs.organisation.resource.OrganisationTypeResource;
import org.innovateuk.ifs.user.resource.ProcessRoleResource;
import org.junit.Test;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.innovateuk.ifs.invite.builder.ApplicationKtaInviteResourceBuilder.newApplicationKtaInviteResource;
import static org.innovateuk.ifs.organisation.builder.HeukarPartnerOrganisationResourceBuilder.newHeukarPartnerOrganisationResource;
import static org.innovateuk.ifs.organisation.builder.OrganisationTypeResourceBuilder.newOrganisationTypeResource;
import static org.innovateuk.ifs.user.builder.ProcessRoleResourceBuilder.newProcessRoleResource;
import static org.junit.Assert.assertEquals;

public class ApplicationTeamViewModelTest {

    private ApplicationTeamViewModel model;

    @Test
    public void heukarPartnerOrganisations() {
        List<HeukarPartnerOrganisationResource> partnerOrganisationResourceList = new ArrayList<>();

        HeukarPartnerOrganisationResource org1 = newHeukarPartnerOrganisationResource()
                .withId(1L, 2L)
                .withApplicationId(1L, 2L)
                .withOrganisationTypeResource(newOrganisationTypeResource().withId(1L).withName("test").build())
                .build();

        partnerOrganisationResourceList.add(org1);

        model = new ApplicationTeamViewModel(1, null, null, 1, null, 1,
                false, false, false, false, true, null, null, false,
                partnerOrganisationResourceList);

        assertEquals(model.getHeukarPartnerOrganisationResources().size(), partnerOrganisationResourceList.size());
    }

    @Test
    public void pendingDays() {
        // given
        ZonedDateTime fiveDaysAgo = ZonedDateTime.now().minus(5, ChronoUnit.DAYS);
        ApplicationKtaInviteResource invite = newApplicationKtaInviteResource()
                .withStatus(InviteStatus.SENT)
                .withSentOn(fiveDaysAgo)
                .build();
        model = new ApplicationTeamViewModel(1, null, null, 1, null, 1,
                false, false, false, false, true, invite, null, false, Collections.emptyList());

        // when
        long pendingDays = model.getKtaInvitePendingDays();

        // then
        assertEquals(5L, pendingDays);
    }

    @Test
    public void emailShouldComeFromProcessRoleIfPresent() {
        // given
        ApplicationKtaInviteResource invite = newApplicationKtaInviteResource()
                .withEmail("inviteemail@example.com")
                .build();
        ProcessRoleResource processRole = newProcessRoleResource().withUserEmail("processrole@example.com").build();
        model = new ApplicationTeamViewModel(1, null, null, 1, null, 1,
                false, false, false, false, true, invite, processRole, false, Collections.emptyList());

        // when
        String result = model.getKtaEmail();

        // then
        assertEquals("processrole@example.com", result);
    }

    @Test
    public void emailShouldFallbackToInviteIfNoProcessRole() {
        // given
        ApplicationKtaInviteResource invite = newApplicationKtaInviteResource()
                .withEmail("inviteemail@example.com")
                .build();
        model = new ApplicationTeamViewModel(1, null, null, 1, null, 1,
                false, false, false, false, true, invite, null, false, Collections.emptyList());

        // when
        String result = model.getKtaEmail();

        // then
        assertEquals("inviteemail@example.com", result);
    }

    @Test
    public void nameShouldComeFromProcessRoleIfPresent() {
        // given
        ApplicationKtaInviteResource invite = newApplicationKtaInviteResource()
                .withName("Invited")
                .build();
        ProcessRoleResource processRole = newProcessRoleResource().withUserName("ProcessRole").build();
        model = new ApplicationTeamViewModel(1, null, null, 1, null, 1,
                false, false, false, false, true, invite, processRole, false, Collections.emptyList());

        // when
        String result = model.getKtaName();

        // then
        assertEquals("ProcessRole", result);
    }

    @Test
    public void nameShouldFallbackToInviteIfNoProcessRole() {
        // given
        ApplicationKtaInviteResource invite = newApplicationKtaInviteResource()
                .withName("Invited")
                .build();
        model = new ApplicationTeamViewModel(1, null, null, 1, null, 1,
                false, false, false, false, true, invite, null, false, Collections.emptyList());

        // when
        String result = model.getKtaName();

        // then
        assertEquals("Invited", result);
    }
}
