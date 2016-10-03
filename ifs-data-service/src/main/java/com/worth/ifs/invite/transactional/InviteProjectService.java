package com.worth.ifs.invite.transactional;


import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.invite.resource.InviteProjectResource;
import com.worth.ifs.commons.security.SecuredBySpring;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;


public interface InviteProjectService {


    @PreAuthorize("hasPermission(#inviteProjectResource, 'SAVE_PROJECT_INVITE')")
    ServiceResult<Void> saveProjectInvite(@P("inviteProjectResource") InviteProjectResource inviteProjectResource);

    @PostFilter("hasPermission(filterObject, 'READ_PROJECT_INVITE')")
    ServiceResult<List<InviteProjectResource>> getInvitesByProject(Long projectId);

    @PreAuthorize("hasAuthority('system_registrar')")
    @SecuredBySpring(value = "ACCEPT_INVITE",
            description = "The System Registration user can accept an invite for a given hash",
            additionalComments = "The hash should be unguessable so the only way to successfully call this method would be to have been given the hash in the first place")
    ServiceResult<Void> acceptProjectInvite(String inviteHash, Long userId);

    @PreAuthorize("hasAuthority('system_registrar')")
    @SecuredBySpring(value = "READ_INVITE_ON_HASH",
            description = "The System Registration user can read an invite for a given hash",
            additionalComments = "The hash should be unguessable so the only way to successfully call this method would be to have been given the hash in the first place")
    ServiceResult<InviteProjectResource> getInviteByHash(String hash);

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

}
