package org.innovateuk.ifs.invite.service;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;

import static org.innovateuk.ifs.application.builder.ApplicationResourceBuilder.newApplicationResource;
import static org.innovateuk.ifs.base.amend.BaseBuilderAmendFunctions.id;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InviteServiceImplTest {

    @InjectMocks
    private InviteServiceImpl service;

    @Mock
    private InviteRestService inviteRestService;

    @Test
    public void getPendingInvitations() throws Exception {
        ApplicationResource app = newApplicationResource()
                .with(id(1L))
                .build();

        ApplicationInviteResource inv1 = inviteResource("name1", "teamA", InviteStatus.CREATED);
        ApplicationInviteResource inv2 = inviteResource("name2", "teamA", InviteStatus.SENT);
        ApplicationInviteResource inv3 = inviteResource("name3", "teamA", InviteStatus.OPENED);

        InviteOrganisationResource inviteOrgResource = inviteOrganisationResource(inv1, inv2, inv3);

        List<InviteOrganisationResource> inviteOrgResources = Arrays.asList(inviteOrgResource);
        RestResult<List<InviteOrganisationResource>> invitesResult = restSuccess(inviteOrgResources, HttpStatus.OK);

        when(inviteRestService.getInvitesByApplication(app.getId())).thenReturn(invitesResult);

        List<ApplicationInviteResource> invites = service.getPendingInvitationsByApplicationId(app.getId());

        verify(inviteRestService).getInvitesByApplication(app.getId());

        assertTrue(invites.size() == 2);
    }

    @Test
    public void pendingOrganisationNamesOmitsEmptyOrganisationName() throws Exception {
        ApplicationResource app = newApplicationResource()
                .with(id(1L))
                .build();

        ApplicationInviteResource inv1 = inviteResource("kirk", "teamA", InviteStatus.CREATED);
        ApplicationInviteResource inv2 = inviteResource("picard", "", InviteStatus.CREATED);

        InviteOrganisationResource inviteOrgResource1 = inviteOrganisationResource(inv1);
        InviteOrganisationResource inviteOrgResource2 = inviteOrganisationResource(inv2);

        List<InviteOrganisationResource> inviteOrgResources = Arrays.asList(inviteOrgResource1, inviteOrgResource2);
        RestResult<List<InviteOrganisationResource>> invitesResult = RestResult.restSuccess(inviteOrgResources, HttpStatus.OK);

        when(inviteRestService.getInvitesByApplication(app.getId())).thenReturn(invitesResult);

        List<ApplicationInviteResource> invites = service.getPendingInvitationsByApplicationId(app.getId());

        verify(inviteRestService).getInvitesByApplication(app.getId());

        assertTrue(invites.size() == 2);
        assertTrue(invites.contains(inv1));
        assertTrue(invites.contains(inv2));
    }

    @Test
    public void nonAcceptedInvitationsAffectPendingAssignableUsers() throws Exception {
        ApplicationResource app = newApplicationResource()
                .with(id(1L))
                .build();

        ApplicationInviteResource inv1 = inviteResource("kirk", "teamA", InviteStatus.CREATED);
        ApplicationInviteResource inv2 = inviteResource("spock", "teamA", InviteStatus.SENT);
        ApplicationInviteResource inv3 = inviteResource("bones", "teamA", InviteStatus.OPENED);

        ApplicationInviteResource inv4 = inviteResource("picard", "teamB", InviteStatus.CREATED);

        InviteOrganisationResource inviteOrgResource1 = inviteOrganisationResource(inv1, inv2, inv3);
        InviteOrganisationResource inviteOrgResource2 = inviteOrganisationResource(inv4);

        List<InviteOrganisationResource> inviteOrgResources = Arrays.asList(inviteOrgResource1, inviteOrgResource2);
        RestResult<List<InviteOrganisationResource>> invitesResult = restSuccess(inviteOrgResources, HttpStatus.OK);

        when(inviteRestService.getInvitesByApplication(app.getId())).thenReturn(invitesResult);

        List<ApplicationInviteResource> invites = service.getPendingInvitationsByApplicationId(app.getId());

        verify(inviteRestService).getInvitesByApplication(app.getId());

        assertTrue(invites.size() == 3);
        assertTrue(invites.contains(inv1));
        assertTrue(invites.contains(inv2));
        assertTrue(invites.contains(inv4));
    }

    private ApplicationInviteResource inviteResource(String name, String organisation, InviteStatus status) {
        ApplicationInviteResource invRes = new ApplicationInviteResource();
        invRes.setName(name);
        invRes.setInviteOrganisationName(organisation);
        invRes.setStatus(status);
        return invRes;
    }

    private InviteOrganisationResource inviteOrganisationResource(ApplicationInviteResource... invs) {
        InviteOrganisationResource ior = new InviteOrganisationResource();
        ior.setInviteResources(Arrays.asList(invs));
        return ior;
    }
}
