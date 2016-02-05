package com.worth.ifs.invite.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.invite.domain.Invite;
import com.worth.ifs.invite.resource.InviteResource;
import com.worth.ifs.notifications.resource.Notification;
import com.worth.ifs.security.NotSecured;

import java.util.List;
import java.util.Optional;

public interface InviteService {

    @NotSecured("This method is not secured, since the person accepting the invite, is not yet registered. This resource should only contain the most basic data like competition name and application name.")
    Optional<InviteResource> getInviteByHash(String hash);
    @NotSecured("TODO")
    List<ServiceResult<Notification>> inviteCollaborators(String baseUrl, List<Invite> invites);
    @NotSecured("TODO")
    ServiceResult<Notification> inviteCollaboratorToApplication(String baseUrl, Invite invite);
    @NotSecured("TODO")
    Invite findOne(Long id);
}
