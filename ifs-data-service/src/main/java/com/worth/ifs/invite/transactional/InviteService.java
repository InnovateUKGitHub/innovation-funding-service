package com.worth.ifs.invite.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.invite.domain.Invite;
import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.invite.resource.InviteResource;
import com.worth.ifs.invite.resource.InviteResultsResource;
import com.worth.ifs.notifications.resource.Notification;
import com.worth.ifs.security.NotSecured;

import java.util.List;
import java.util.Optional;

public interface InviteService {

    @NotSecured("TODO")
    List<ServiceResult<Notification>> inviteCollaborators(String baseUrl, List<Invite> invites);

    @NotSecured("TODO")
    ServiceResult<Notification> inviteCollaboratorToApplication(String baseUrl, Invite invite);

    @NotSecured("TODO")
    Invite findOne(Long id);

    @NotSecured("TODO")
    List<Invite> findByApplicationId(Long applicationId);

    @NotSecured("TODO")
    Optional<Invite> getByHash(String hash);

    @NotSecured("TODO DW - implement when permissions matrix available")
    ServiceResult<InviteResultsResource> createApplicationInvites(InviteOrganisationResource inviteOrganisationResource);

    @NotSecured("This method is not secured, since the person accepting the invite, is not yet registered. This resource should only contain the most basic data like competition name and application name.")
    ServiceResult<InviteResource> getInviteByHash(String hash);

    @NotSecured("TODO DW - implement when permissions matrix available")
    ServiceResult<InviteOrganisationResource> getInviteOrganisationByHash(String hash);

    @NotSecured("TODO DW - implement when permissions matrix available")
    ServiceResult<List<InviteOrganisationResource>> getInvitesByApplication(Long applicationId);

    @NotSecured("TODO DW - implement when permissions matrix available")
    ServiceResult<InviteResultsResource> saveInvites(List<InviteResource> inviteResources);
}
