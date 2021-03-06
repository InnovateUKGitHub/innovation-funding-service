package org.innovateuk.ifs.management.admin.viewmodel;

import org.innovateuk.ifs.management.admin.form.InviteUserView;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

import static java.util.stream.Collectors.toSet;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class InviteUserViewModelTest {

    @Test
    public void inviteInternalUserViewModel() {

        InviteUserViewModel viewModel = new InviteUserViewModel(InviteUserView.INTERNAL_USER, Role.internalRoles());

        assertEquals(InviteUserView.INTERNAL_USER, viewModel.getType());
        assertEquals("internal user", viewModel.getTypeName());
        assertTrue(viewModel.isInternal());
        assertEquals(viewModel.getRoles(), Role.internalRoles());
    }

    @Test
    public void inviteExternalUserViewModelKta() {

        InviteUserViewModel viewModel = new InviteUserViewModel(InviteUserView.EXTERNAL_USER, new HashSet<>(Arrays.asList(Role.KNOWLEDGE_TRANSFER_ADVISER)));

        assertEquals(InviteUserView.EXTERNAL_USER, viewModel.getType());
        assertEquals("knowledge transfer adviser", viewModel.getTypeName());
        assertTrue(viewModel.isExternal());
        assertEquals(viewModel.getRoles(), new HashSet<>(Arrays.asList(Role.KNOWLEDGE_TRANSFER_ADVISER)));
    }

    @Test
    public void inviteExternalUserViewModelSupporter() {

        InviteUserViewModel viewModel = new InviteUserViewModel(InviteUserView.EXTERNAL_USER, new HashSet<Role>(Arrays.asList(Role.SUPPORTER)));

        assertEquals(InviteUserView.EXTERNAL_USER, viewModel.getType());
        assertEquals("supporter", viewModel.getTypeName());
        assertTrue(viewModel.isExternal());
        assertEquals(viewModel.getRoles(), new HashSet<>(Arrays.asList(Role.SUPPORTER)));
    }

    @Test
    public void inviteExternalUserViewModelAllExternalRoles() {

        InviteUserViewModel viewModel = new InviteUserViewModel(InviteUserView.EXTERNAL_USER, Role.externalRolesToInvite().stream().collect(toSet()));

        assertEquals(InviteUserView.EXTERNAL_USER, viewModel.getType());
        assertTrue(Arrays.asList("knowledge transfer adviser", "supporter").contains(viewModel.getTypeName()));
        assertTrue(viewModel.isExternal());
        assertEquals(viewModel.getRoles(), Role.externalRolesToInvite().stream().collect(toSet()));
    }
}
