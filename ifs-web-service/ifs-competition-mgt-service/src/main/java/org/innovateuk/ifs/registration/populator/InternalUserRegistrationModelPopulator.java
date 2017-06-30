package org.innovateuk.ifs.registration.populator;

import org.innovateuk.ifs.invite.service.InviteUserRestService;
import org.innovateuk.ifs.registration.viewmodel.InternalUserRegistrationViewModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Internal user registration model view populator.  For retrieval and display of details about invite with registration form.
 */
@Component
public class InternalUserRegistrationModelPopulator {

    @Autowired
    private InviteUserRestService inviteUserRestService;

    public InternalUserRegistrationViewModel populateModel(String inviteHash) {
        return inviteUserRestService.getInvite(inviteHash).andOnSuccessReturn(
                roleInviteResource -> new InternalUserRegistrationViewModel(roleInviteResource.getName(),
                        roleInviteResource.getRoleDisplayName(), roleInviteResource.getEmail())
        ).getSuccessObjectOrThrowException();
    }
}