package org.innovateuk.ifs.project.monitoringofficer.populator;

import org.innovateuk.ifs.BaseUnitTest;
import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.project.monitoringofficer.viewmodel.MonitoringOfficerAssignRoleViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

public class MonitoringOfficerAssignRoleViewModelPopulatorTest extends BaseUnitTest {

    @InjectMocks
    private MonitoringOfficerAssignRoleViewModelPopulator target;

    @Mock
    private UserRestService userRestService;

    @Test
    public void populate() {
        UserResource userResource = new UserResource();
        userResource.setId(999L);
        userResource.setEmail("email@email.email");
        userResource.setFirstName("first");
        userResource.setLastName("last");
        when(userRestService.retrieveUserById(anyLong())).thenReturn(restSuccess(userResource));
        MonitoringOfficerAssignRoleViewModel actual = target.populate(userResource.getId());

        assertThat(actual, instanceOf(MonitoringOfficerAssignRoleViewModel.class));
        assertThat(actual.getUserId(), is(999L));
        assertThat(actual.getEmailAddress(), is("email@email.email"));
        assertThat(actual.getFirstName(), is("first"));
        assertThat(actual.getLastName(), is("last"));
    }

    @Test(expected = ObjectNotFoundException.class)
    public void populateWhenUserNotFound() {
        long userId = 999L;
        when(userRestService.retrieveUserById(userId)).thenReturn(restFailure(notFoundError(UserResource.class, userId)));
        target.populate(userId);
    }

}
