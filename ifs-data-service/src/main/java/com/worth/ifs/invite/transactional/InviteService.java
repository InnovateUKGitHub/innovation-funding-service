package com.worth.ifs.invite.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.invite.domain.ApplicationInvite;
import com.worth.ifs.invite.resource.ApplicationInviteResource;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.invite.resource.InviteResultsResource;
import com.worth.ifs.security.SecuredBySpring;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.access.prepost.PreFilter;

import java.util.List;
import java.util.Set;

public interface InviteService {

    @PreFilter(filterTarget = "invites", value = "hasPermission(filterObject, 'SEND')")
    List<ServiceResult<Void>> inviteCollaborators(String baseUrl, @P("invites") List<ApplicationInvite> invites);

    @PreAuthorize("hasPermission(#invite, 'SEND')")
    ServiceResult<Void> inviteCollaboratorToApplication(String baseUrl, @P("invite") ApplicationInvite invite);

    @PostAuthorize("hasPermission(returnObject, 'READ')")
    ServiceResult<ApplicationInvite> findOne(Long id);

    @PreAuthorize("hasPermission(#inviteOrganisationResource, 'SEND')")
    ServiceResult<InviteResultsResource> createApplicationInvites(@P("inviteOrganisationResource") final InviteOrganisationResource inviteOrganisationResource);

    @PreAuthorize("hasAuthority('system_registrar')")
    @SecuredBySpring(value = "READ_INVITE_ORGANISATION_ON_HASH",
            description = "The System Registration user can view an organisation invitation when looked up by hash",
            additionalComments = "The hash should be unguessable so the only way to successfully call this method would be to have been given the hash in the first place")
    ServiceResult<InviteOrganisationResource> getInviteOrganisationByHash(String hash);

    @PostFilter("hasPermission(filterObject, 'READ')")
    ServiceResult<Set<InviteOrganisationResource>> getInvitesByApplication(Long applicationId);

    @PreFilter(filterTarget = "inviteResources", value = "hasPermission(filterObject, 'SAVE')")
    ServiceResult<InviteResultsResource> saveInvites(@P("inviteResources") List<ApplicationInviteResource> inviteResources);

    @PreAuthorize("hasAuthority('system_registrar')")
    @SecuredBySpring(value = "ACCEPT_INVITE",
            description = "The System Registration user can accept an invite for a given hash",
            additionalComments = "The hash should be unguessable so the only way to successfully call this method would be to have been given the hash in the first place")
    ServiceResult<Void> acceptInvite(String inviteHash, Long userId);

    @PreAuthorize("hasAuthority('system_registrar')")
    @SecuredBySpring(value = "READ_INVITE_ON_HASH",
            description = "The System Registration user can read an invite for a given hash",
            additionalComments = "The hash should be unguessable so the only way to successfully call this method would be to have been given the hash in the first place")
    ServiceResult<ApplicationInviteResource> getInviteByHash(String hash);

    @PreAuthorize("hasAuthority('system_registrar')")
    @SecuredBySpring(value = "CHECK_EXISTENCE_OF_INVITE_ON_HASH",
            description = "The System Registration user can check to see if there is an invite for a given hash",
            additionalComments = "The hash should be unguessable so the only way to successfully call this method would be to have been given the hash in the first place")
    ServiceResult<Boolean> checkUserExistingByInviteHash(@P("hash") String hash);


    @PreAuthorize("hasAuthority('system_registrar')")
    @SecuredBySpring(value = "GET_USER_ON_HASH",
            description = "The System Registration user can see if there is a user for a given hash",
            additionalComments = "The hash should be unguessable so the only way to successfully call this method would be to have been given the hash in the first place")
    ServiceResult<UserResource> getUserByInviteHash(@P("hash") String hash);

    @PreAuthorize("hasPermission(#invite, 'DELETE')")
    ServiceResult<Void> removeInvite(Long inviteId);
}
