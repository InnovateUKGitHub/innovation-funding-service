package com.worth.ifs.registration.service;

import com.worth.ifs.invite.resource.InviteOrganisationResource;
import com.worth.ifs.invite.resource.InviteResource;
import com.worth.ifs.user.resource.UserResource;
import org.springframework.ui.Model;

public interface RegistrationService {

    boolean invalidInvite(Model model, UserResource loggedInUser, InviteResource inviteResource, InviteOrganisationResource inviteOrganisation);
}
