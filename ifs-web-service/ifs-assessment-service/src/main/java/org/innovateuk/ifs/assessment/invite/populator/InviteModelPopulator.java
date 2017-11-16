package org.innovateuk.ifs.assessment.invite.populator;

import org.innovateuk.ifs.assessment.invite.viewmodel.BaseInviteViewModel;

public abstract class InviteModelPopulator<T extends BaseInviteViewModel> {

    protected abstract T populateModel(String inviteHash, boolean userLoggedIn);
}
