package org.innovateuk.ifs.login.controller.viewModel;

import org.innovateuk.ifs.login.viewmodel.RoleSelectionViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class RoleSelectionViewModelTest {

    @InjectMocks
    private RoleSelectionViewModel viewModel;

    @Mock
    private UserResource user;

    @Test
    public void getAcceptedRoles() {

        assertThat(viewModel.getAcceptedRoles(), equalTo(user.getRoles()));
    }

}
