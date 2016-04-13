package com.worth.ifs.invite.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.invite.domain.Invite;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.invite.resource.InviteResource;
import com.worth.ifs.invite.resource.InviteResultsResource;
import com.worth.ifs.notifications.resource.Notification;
import com.worth.ifs.security.NotSecured;
import org.springframework.security.access.method.P;

import java.util.List;
import java.util.Set;

public interface InviteService {

    @NotSecured("TODO")
    List<ServiceResult<Notification>> inviteCollaborators(String baseUrl, List<Invite> invites);

    @NotSecured("TODO")
    ServiceResult<Notification> inviteCollaboratorToApplication(String baseUrl, Invite invite);

    @NotSecured("TODO")
    ServiceResult<Invite> findOne(Long id);

    @NotSecured("TODO DW - implement when permissions matrix available")
    ServiceResult<InviteResultsResource> createApplicationInvites(InviteOrganisationResource inviteOrganisationResource);

    @NotSecured("TODO DW - implement when permissions matrix available")
    ServiceResult<InviteOrganisationResource> getInviteOrganisationByHash(String hash);

    @NotSecured("TODO DW - implement when permissions matrix available")
    ServiceResult<Set<InviteOrganisationResource>> getInvitesByApplication(Long applicationId);

    @NotSecured("TODO DW - implement when permissions matrix available")
    ServiceResult<InviteResultsResource> saveInvites(List<InviteResource> inviteResources);

    @NotSecured("TODO DW - implement when permissions matrix available")
    ServiceResult<Void> acceptInvite(String inviteHash, Long userId);

    @NotSecured("TODO DW - implement when permissions matrix available")
    ServiceResult<InviteResource> getInviteByHash(String hash);

    @NotSecured("We need to check if there is already a user with the invited email address")
    ServiceResult<Void> checkUserExistingByInviteHash(@P("hash") String hash);
}
