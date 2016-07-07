package com.worth.ifs.registration.service;

import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.invite.resource.InviteResource;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.ui.Model;

import java.util.Map;

/**
 * Interface for {@link RegistrationServiceImpl}
 */
public interface RegistrationService {

    Map<String, String> getInvalidInviteMessages(UserResource loggedInUser, InviteResource inviteResource, InviteOrganisationResource inviteOrganisation);
}
