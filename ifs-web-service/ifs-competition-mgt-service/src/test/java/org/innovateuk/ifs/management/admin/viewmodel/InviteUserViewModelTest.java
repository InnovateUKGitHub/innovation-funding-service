package org.innovateuk.ifs.management.admin.viewmodel;

import org.innovateuk.ifs.management.admin.form.InviteUserView;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

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

        InviteUserViewModel viewModel = new InviteUserViewModel(InviteUserView.EXTERNAL_USER, new HashSet<>(List.of(Role.KNOWLEDGE_TRANSFER_ADVISER)));

        assertEquals(InviteUserView.EXTERNAL_USER, viewModel.getType());
        assertEquals("knowledge transfer adviser", viewModel.getTypeName());
        assertTrue(viewModel.isExternal());
        assertEquals(viewModel.getRoles(), new HashSet<>(List.of(Role.KNOWLEDGE_TRANSFER_ADVISER)));
    }

    @Test
    public void inviteExternalUserViewModelSupporter() {

        InviteUserViewModel viewModel = new InviteUserViewModel(InviteUserView.EXTERNAL_USER, new HashSet<Role>(List.of(Role.SUPPORTER)));

        assertEquals(InviteUserView.EXTERNAL_USER, viewModel.getType());
        assertEquals("supporter", viewModel.getTypeName());
        assertTrue(viewModel.isExternal());
        assertEquals(viewModel.getRoles(), new HashSet<>(List.of(Role.SUPPORTER)));
    }

    @Test
    public void inviteExternalUserViewModelAllExternalRoles() {

        InviteUserViewModel viewModel = new InviteUserViewModel(InviteUserView.EXTERNAL_USER, new HashSet<>(Role.externalRolesToInvite()));

        assertEquals(InviteUserView.EXTERNAL_USER, viewModel.getType());
        assertTrue(Arrays.asList("assessor", "knowledge transfer adviser", "supporter").contains(viewModel.getTypeName()));
        assertTrue(viewModel.isExternal());
        assertEquals(viewModel.getRoles(), new HashSet<>(Role.externalRolesToInvite()));
    }

    @Test
    public void inviteExternalUserViewModelAssessor() {

        InviteUserViewModel viewModel = new InviteUserViewModel(InviteUserView.EXTERNAL_USER, new HashSet<>(List.of(Role.ASSESSOR)));

        assertEquals(InviteUserView.EXTERNAL_USER, viewModel.getType());
        assertTrue(Arrays.asList("assessor", "knowledge transfer adviser", "supporter").contains(viewModel.getTypeName()));
        assertTrue(viewModel.isExternal());
        assertEquals(viewModel.getRoles(), new HashSet<>(List.of(Role.ASSESSOR)));
    }

    @Test
    public void inviteExternalUserViewModelExcludingAssessor() {

        InviteUserViewModel viewModel = new InviteUserViewModel(InviteUserView.EXTERNAL_USER, new HashSet<>(Role.externalRolesExcludingAssessor()));
        assertEquals(InviteUserView.EXTERNAL_USER, viewModel.getType());
        assertTrue(Arrays.asList("knowledge transfer adviser", "supporter").contains(viewModel.getTypeName()));
        assertTrue(viewModel.isExternal());

        assertEquals(viewModel.getRoles(), new HashSet<>(Role.externalRolesExcludingAssessor()));
    }
}
