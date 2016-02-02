package com.worth.ifs.invite.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.invite.domain.Invite;
import com.worth.ifs.notifications.resource.Notification;
import com.worth.ifs.security.NotSecured;

import java.util.List;

public interface InviteService {

    @NotSecured("TODO")
    List<ServiceResult<Notification>> inviteCollaborators(String baseUrl, List<Invite> invites);
    @NotSecured("TODO")
    ServiceResult<Notification> inviteCollaboratorToApplication(String baseUrl, Invite invite);
}
