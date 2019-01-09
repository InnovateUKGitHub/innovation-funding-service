package org.innovateuk.ifs.login.viewmodel;

import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.junit.Assert.assertThat;


@RunWith(MockitoJUnitRunner.class)
public class RoleSelectionViewModelTest {

    private DashboardSelectionViewModel viewModel;

    @Before
    public void setUp() throws Exception {
        List<Role> roles = Arrays.asList(ASSESSOR, APPLICANT, STAKEHOLDER);
        UserResource user = newUserResource().withRolesGlobal(roles).build();
        viewModel = new DashboardSelectionViewModel(user);
    }

    @Test
    public void getApplicantRoleDescription() {
        assertThat(viewModel.getRoleDescription(Role.APPLICANT), is(equalTo(DashboardSelectionViewModel.APPLICANT_ROLE_DESCRIPTION)));
    }

    @Test
    public void getAssessorRoleDescription() {
        assertThat(viewModel.getRoleDescription(Role.ASSESSOR), is(equalTo(DashboardSelectionViewModel.ASSESSOR_ROLE_DESCRIPTION)));
    }

    @Test
    public void getStakeholderRoleDescription() {
        assertThat(viewModel.getRoleDescription(Role.STAKEHOLDER), is(equalTo(DashboardSelectionViewModel.STAKEHOLDER_ROLE_DESCRIPTION)));
    }

    @Test
    public void getRoleDescriptionForRoleNotInAcceptedRoles() {
        assertThat(viewModel.getRoleDescription(Role.COMP_ADMIN), is(equalTo(DashboardSelectionViewModel.EMPTY_DESCRIPTION)));
    }

}