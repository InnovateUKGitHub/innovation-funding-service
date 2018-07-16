package org.innovateuk.ifs.invite.transactional;

import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.domain.ApplicationInvite;
import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;
import org.innovateuk.ifs.invite.resource.InviteOrganisationResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;

import java.util.List;
import java.util.Optional;

public interface ApplicationInviteService {

    @PreFilter(filterTarget = "invites", value = "hasPermission(filterObject, 'SEND')")
    ServiceResult<Void> inviteCollaborators(String baseUrl, @P("invites") List<ApplicationInvite> invites);

    @PreAuthorize("hasPermission(#invite, 'SEND')")
    ServiceResult<Void> inviteCollaboratorToApplication(String baseUrl, @P("invite") ApplicationInvite invite);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<ApplicationInvite> findOneByHash(String hash);

    @PreAuthorize("hasPermission(#inviteOrganisationResource, 'CREATE_APPLICATION_INVITES')")
    ServiceResult<Void> createApplicationInvites(@P("inviteOrganisationResource") final InviteOrganisationResource inviteOrganisationResource, Optional<Long> applicationId);

    @PreAuthorize("hasAuthority('system_registrar')")
    @SecuredBySpring(value = "READ_INVITE_ORGANISATION_ON_HASH",
            description = "The System Registration user can view an organisation invitation when looked up by hash",
            additionalComments = "The hash should be unguessable so the only way to successfully call this method would be to have been given the hash in the first place")
    ServiceResult<InviteOrganisationResource> getInviteOrganisationByHash(String hash);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<List<InviteOrganisationResource>> getInvitesByApplication(Long applicationId);

    @PreFilter(filterTarget = "inviteResources", value = "hasPermission(filterObject, 'SAVE')")
    ServiceResult<Void> saveInvites(@P("inviteResources") List<ApplicationInviteResource> inviteResources);

    @PreAuthorize("hasAuthority('system_registrar')")
    @SecuredBySpring(value = "READ_INVITE_ON_HASH",
            description = "The System Registration user can read an invite for a given hash",
            additionalComments = "The hash should be unguessable so the only way to successfully call this method would be to have been given the hash in the first place")
    ServiceResult<ApplicationInviteResource> getInviteByHash(String hash);

    @PreAuthorize("hasAuthority('system_registrar')")
    @SecuredBySpring(value = "CHECK_EXISTENCE_OF_INVITE_ON_HASH",
            description = "The System Registration user can check to see if there is an invite for a given hash",
            additionalComments = "The hash should be unguessable so the only way to successfully call this method would be to have been given the hash in the first place")
    ServiceResult<Boolean> checkUserExistsForInvite(String hash);

    @PreAuthorize("hasAuthority('system_registrar')")
    @SecuredBySpring(value = "GET_USER_ON_HASH",
            description = "The System Registration user can see if there is a user for a given hash",
            additionalComments = "The hash should be unguessable so the only way to successfully call this method would be to have been given the hash in the first place")
    ServiceResult<UserResource> getUserByInviteHash(String hash);

    @PreAuthorize("hasPermission(#applicationInviteId, 'org.innovateuk.ifs.invite.resource.ApplicationInviteResource', 'DELETE')")
    ServiceResult<Void> removeApplicationInvite(long applicationInviteId);
}
