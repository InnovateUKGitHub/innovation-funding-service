package org.innovateuk.ifs.invite;


import org.innovateuk.ifs.invite.resource.ApplicationInviteResource;

import java.util.List;

public interface InviteService {

    List<ApplicationInviteResource> getPendingInvitationsByApplicationId(Long applicationId);
}
