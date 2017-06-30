package org.innovateuk.ifs.registration.populator;

import org.innovateuk.ifs.invite.service.InviteUserRestService;
import org.innovateuk.ifs.registration.viewmodel.InternalUserRegistrationViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * TODO
 */
@Component
public class InternalUserRegistrationModelPopulator {

    @Autowired
    private InviteUserRestService inviteRestService;

    public InternalUserRegistrationViewModel populateModel(String inviteHash) {
        return inviteRestService.getInvite(inviteHash).andOnSuccessReturn(
                roleInviteResource -> new InternalUserRegistrationViewModel(roleInviteResource.getName(),
                        roleInviteResource.getRoleName(), roleInviteResource.getEmail())
        ).getSuccessObjectOrThrowException();
    }
}