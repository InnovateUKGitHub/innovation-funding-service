package org.innovateuk.ifs.management.admin.populator;

import org.innovateuk.ifs.category.resource.InnovationSectorResource;
import org.innovateuk.ifs.category.service.CategoryRestService;
import org.innovateuk.ifs.management.admin.form.InviteUserView;
import org.innovateuk.ifs.management.admin.viewmodel.InviteUserViewModel;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.EnumSet;
import java.util.List;

import static org.innovateuk.ifs.category.builder.InnovationSectorResourceBuilder.newInnovationSectorResource;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class InviteUserViewModelPopulatorTest {

    @InjectMocks
    private InviteUserModelPopulator populator;

    @Mock
    private CategoryRestService categoryRestService;

    @Test
    public void inviteInternalUserAsIFSAdministrator() {
        UserResource loggedInUser = newUserResource()
                .withRoleGlobal(IFS_ADMINISTRATOR)
                .build();

        List<InnovationSectorResource> expectedInnovationSectorOptions = newInnovationSectorResource().build(4);

        when(categoryRestService.getInnovationSectors()).thenReturn(restSuccess(expectedInnovationSectorOptions));

        InviteUserViewModel viewModel = populator.populate(InviteUserView.INTERNAL_USER, Role.internalRoles(), loggedInUser);

        EnumSet<Role> roles = EnumSet.of(PROJECT_FINANCE, COMP_ADMIN, SUPPORT, INNOVATION_LEAD);

        assertEquals(viewModel.getRoles().size(), 4);
        assertEquals(viewModel.getRoles(), roles);
        assertEquals(expectedInnovationSectorOptions, viewModel.getInnovationSectorOptions());
    }

    @Test
    public void inviteInternalUserAsSuperAdministrator() {
        UserResource loggedInUser = newUserResource()
                .withRoleGlobal(Role.SUPER_ADMIN_USER)
                .build();

        List<InnovationSectorResource> expectedInnovationSectorOptions = newInnovationSectorResource().build(4);

        when(categoryRestService.getInnovationSectors()).thenReturn(restSuccess(expectedInnovationSectorOptions));

        InviteUserViewModel viewModel = populator.populate(InviteUserView.INTERNAL_USER, Role.internalRoles(), loggedInUser);

        EnumSet<Role> roles = EnumSet.of(IFS_ADMINISTRATOR, PROJECT_FINANCE, COMP_ADMIN, SUPPORT, INNOVATION_LEAD, AUDITOR);

        assertEquals(viewModel.getRoles().size(), 6);
        assertEquals(viewModel.getRoles(), roles);
        assertEquals(expectedInnovationSectorOptions, viewModel.getInnovationSectorOptions());
    }
}
