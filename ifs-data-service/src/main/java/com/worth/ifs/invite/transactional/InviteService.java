package com.worth.ifs.invite.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.invite.domain.Invite;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.invite.resource.InviteResource;
import com.worth.ifs.invite.resource.InviteResultsResource;
import com.worth.ifs.notifications.resource.Notification;
import com.worth.ifs.security.NotSecured;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Set;

public interface InviteService {

    // TODO qqRP
    // @PreFilter(filterTarget = "invites", value = "hasPermission(filterObject, 'SEND')")
    @NotSecured("TODO")
    List<ServiceResult<Notification>> inviteCollaborators(String baseUrl, @P("invites") List<Invite> invites);

    // TODO qqRP
    // @PreAuthorize("hasPermission(#invite, 'SEND')")
    @NotSecured("TODO")
    ServiceResult<Notification> inviteCollaboratorToApplication(String baseUrl, @P("invite") Invite invite);

    // TODO qqRP
    // @PostAuthorize("hasPermission(returnObject, 'READ')")
    @NotSecured("TODO")
    ServiceResult<Invite> findOne(Long id);

    // TODO qqRP
//    @PreAuthorize("hasPermission(#inviteOrganisationResource, 'SEND')")
    @NotSecured("TODO")
    ServiceResult<InviteResultsResource> createApplicationInvites(@P("inviteOrganisationResource") final InviteOrganisationResource inviteOrganisationResource);

    // TODO qqRP
//    @PreAuthorize("hasAuthority('system_registrar')")
//    @SecuredBySpring(value = "READ_INVITE_ORGANISATION_ON_HASH",
//            description = "The System Registration user can view an organisation invitation when looked up by hash",
//            additionalComments = "The hash should be unguessable so the only way to successfully call this method would be to have been given the hash in the first place")
    @NotSecured("TODO")
    ServiceResult<InviteOrganisationResource> getInviteOrganisationByHash(String hash);

    // TODO qqRP
//    @PostFilter("hasPermission(filterObject, 'READ')")
    @NotSecured("TODO")
    ServiceResult<Set<InviteOrganisationResource>> getInvitesByApplication(Long applicationId);

    // TODO qqRP
//    @PreFilter(filterTarget = "inviteResources", value = "hasPermission(filterObject, 'SAVE')")
    @NotSecured("TODO")
    ServiceResult<InviteResultsResource> saveInvites(@P("inviteResources") List<InviteResource> inviteResources);

    // TODO qqRP
//    @PreAuthorize("hasAuthority('system_registrar')")
//    @SecuredBySpring(value = "ACCEPT_INVITE",
//            description = "The System Registration user can accept an invite for a given hash",
//            additionalComments = "The hash should be unguessable so the only way to successfully call this method would be to have been given the hash in the first place")
    @NotSecured("TODO")
    ServiceResult<Void> acceptInvite(String inviteHash, Long userId);

    // TODO qqRP
    @PreAuthorize("hasAuthority('system_registrar')")
//    @SecuredBySpring(value = "READ_INVITE_ON_HASH",
//            description = "The System Registration user can read an invite for a given hash",
//            additionalComments = "The hash should be unguessable so the only way to successfully call this method would be to have been given the hash in the first place")
    @NotSecured("TODO")
    ServiceResult<InviteResource> getInviteByHash(String hash);

    // TODO qqRP
//    @PreAuthorize("hasAuthority('system_registrar')")
//    @SecuredBySpring(value = "CHECK_EXISTENCE_OF_INVITE_ON_HASH",
//            description = "The System Registration user can check to see if there is an invite for a given hash",
//            additionalComments = "The hash should be unguessable so the only way to successfully call this method would be to have been given the hash in the first place")
    @NotSecured("TODO")
    ServiceResult<Void> checkUserExistingByInviteHash(@P("hash") String hash);
}
