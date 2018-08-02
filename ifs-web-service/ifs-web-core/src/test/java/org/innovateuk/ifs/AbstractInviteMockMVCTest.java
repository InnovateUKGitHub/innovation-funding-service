package org.innovateuk.ifs;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.invite.constant.InviteStatus;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.invite.service.InviteOrganisationRestService;
import org.innovateuk.ifs.invite.service.InviteRestService;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.OrganisationRestService;
import org.mockito.Mock;

import java.util.Arrays;

import static java.util.Collections.emptyList;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;

public abstract class AbstractInviteMockMVCTest<ControllerType> extends BaseControllerMockMVCTest<ControllerType> {
    protected Long competitionId = 1L;

    @Mock
    protected InviteRestService inviteRestService;
    @Mock
    private InviteOrganisationRestService inviteOrganisationRestService;

    public ApplicationInviteResource invite;
    public ApplicationInviteResource acceptedInvite;
    public ApplicationInviteResource existingUserInvite;

    public static final String INVITE_HASH = "b157879c18511630f220325b7a64cf3eb782759326d3cbb85e546e0d03e663ec711ec7ca65827a96";
    public static final String INVITE_HASH_EXISTING_USER = "cccccccccc630f220325b7a64cf3eb782759326d3cbb85e546e0d03e663ec711ec7ca65827a96";
    public static final String INVALID_INVITE_HASH = "aaaaaaa7a64cf3eb782759326d3cbb85e546e0d03e663ec711ec7ca65827a96";
    public static final String ACCEPTED_INVITE_HASH = "BBBBBBBBB7a64cf3eb782759326d3cbb85e546e0d03e663ec711ec7ca65827a96";

    public void setupInvites() {
        when(inviteRestService.getInvitesByApplication(isA(Long.class))).thenReturn(restSuccess(emptyList()));
        InviteOrganisationResource inviteOrganisation = new InviteOrganisationResource(2L, "Invited Organisation Ltd", "Org type", null, null);

        invite = new ApplicationInviteResource();
        invite.setStatus(InviteStatus.SENT);
        invite.setApplication(1L);
        invite.setName("Some Invitee");
        invite.setHash(INVITE_HASH);
        String email = "invited@email.com";
        invite.setEmail(email);
        invite.setInviteOrganisation(inviteOrganisation.getId());
        invite.setCompetitionId(competitionId);
        inviteOrganisation.setInviteResources(Arrays.asList(invite));

        when(inviteRestService.getInviteByHash(eq(INVITE_HASH))).thenReturn(restSuccess(invite));
        when(inviteOrganisationRestService.getByIdForAnonymousUserFlow(eq(invite.getInviteOrganisation()))).thenReturn(restSuccess(inviteOrganisation));
        when(inviteOrganisationRestService.put(any())).thenReturn(restSuccess());
        when(inviteRestService.checkExistingUser(eq(INVITE_HASH))).thenReturn(restSuccess(false));
        when(inviteRestService.checkExistingUser(eq(INVALID_INVITE_HASH))).thenReturn(restFailure(notFoundError(UserResource.class, email)));
        when(inviteRestService.getInviteByHash(eq(INVALID_INVITE_HASH))).thenReturn(restFailure(notFoundError(ApplicationResource.class, INVALID_INVITE_HASH)));
        when(inviteRestService.getInviteOrganisationByHash(INVITE_HASH)).thenReturn(restSuccess(new InviteOrganisationResource()));

        acceptedInvite = new ApplicationInviteResource();
        acceptedInvite.setStatus(InviteStatus.OPENED);
        acceptedInvite.setApplication(1L);
        acceptedInvite.setName("Some Invitee");
        acceptedInvite.setHash(ACCEPTED_INVITE_HASH);
        acceptedInvite.setEmail(email);
        when(inviteRestService.getInviteByHash(eq(ACCEPTED_INVITE_HASH))).thenReturn(restSuccess(acceptedInvite));

        existingUserInvite = new ApplicationInviteResource();
        existingUserInvite.setStatus(InviteStatus.SENT);
        existingUserInvite.setApplication(1L);
        existingUserInvite.setName("Some Invitee");
        existingUserInvite.setHash(INVITE_HASH_EXISTING_USER);
        existingUserInvite.setEmail("existing@email.com");
        when(inviteRestService.checkExistingUser(eq(INVITE_HASH_EXISTING_USER))).thenReturn(restSuccess(true));
        when(inviteRestService.getInviteByHash(eq(INVITE_HASH_EXISTING_USER))).thenReturn(restSuccess(existingUserInvite));

        when(inviteRestService.getInvitesByApplication(isA(Long.class))).thenReturn(restSuccess(emptyList()));
        when(inviteRestService.getInviteOrganisationByHash(INVITE_HASH_EXISTING_USER)).thenReturn(restSuccess(new InviteOrganisationResource()));

    }
}