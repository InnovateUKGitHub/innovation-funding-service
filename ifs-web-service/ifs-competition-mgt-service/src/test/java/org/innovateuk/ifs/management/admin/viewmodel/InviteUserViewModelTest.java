package org.innovateuk.ifs.management.admin.viewmodel;

import org.innovateuk.ifs.management.admin.form.InviteUserView;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class InviteUserViewModelTest {

    @Test
    public void InviteInternalUserViewModel() {

        InviteUserViewModel viewModel = new InviteUserViewModel(InviteUserView.INTERNAL_USER, Role.internalRoles());

        assertEquals(InviteUserView.INTERNAL_USER, viewModel.getType());
        assertEquals(InviteUserView.INTERNAL_USER.getName(), viewModel.getTypeName());
        assertTrue(viewModel.isInternal());
        assertEquals(viewModel.getRoles(), Role.internalRoles());
    }

    @Test
    public void InviteExternalUserViewModel() {

        InviteUserViewModel viewModel = new InviteUserViewModel(InviteUserView.EXTERNAL_USER, Role.externalRolesToInvite());

        assertEquals(InviteUserView.EXTERNAL_USER, viewModel.getType());
        assertEquals(InviteUserView.EXTERNAL_USER.getName(), viewModel.getTypeName());
        assertTrue(viewModel.isExternal());
        assertEquals(viewModel.getRoles(), Role.externalRolesToInvite());
    }
}
