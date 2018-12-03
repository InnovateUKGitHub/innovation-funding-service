package org.innovateuk.ifs.login.viewmodel;

import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


@RunWith(MockitoJUnitRunner.class)
public class RoleSelectionViewModelTest {

    private RoleSelectionViewModel viewModel;
    private UserResource user;

    @Before
    public void setUp() throws Exception {
        List<Role> roles = Arrays.asList(ASSESSOR, APPLICANT, STAKEHOLDER);
        user = newUserResource().withRolesGlobal(roles).build();
        viewModel = new RoleSelectionViewModel(user);
    }

    @Test
    public void getAcceptedRoles() {
        assertTrue(viewModel.getAcceptedRoles().contains(STAKEHOLDER));
        assertTrue(viewModel.getAcceptedRoles().contains(APPLICANT));
        assertTrue(viewModel.getAcceptedRoles().contains(ASSESSOR));
    }
    @Test
    public void getRoleDescription(){
        assertThat(viewModel.getRoleDescription(Role.APPLICANT), is(equalTo(RoleSelectionViewModel.APPLICANT_ROLE_DESCRIPTION)));
        assertThat(viewModel.getRoleDescription(Role.ASSESSOR), is(equalTo(RoleSelectionViewModel.ASSESSOR_ROLE_DESCRIPTION)));
        assertThat(viewModel.getRoleDescription(Role.STAKEHOLDER), is(equalTo(RoleSelectionViewModel.STAKEHOLDER_ROLE_DESCRIPTION)));
    }

}