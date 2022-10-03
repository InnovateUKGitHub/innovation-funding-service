package org.innovateuk.ifs.project.monitoringofficer.populator;

import org.innovateuk.ifs.commons.exception.ObjectNotFoundException;
import org.innovateuk.ifs.project.monitoringofficer.viewmodel.MonitoringOfficerAssignRoleViewModel;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.service.UserRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.rest.RestResult.restFailure;
import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.innovateuk.ifs.user.builder.UserResourceBuilder.newUserResource;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MonitoringOfficerAssignRoleViewModelPopulatorTest {

    @InjectMocks
    private MonitoringOfficerAssignRoleViewModelPopulator target;

    @Mock
    private UserRestService userRestService;

    @Test
    public void populate() {
        UserResource userResource = newUserResource()
                .withId(999L)
                .withEmail("email@email.email")
                .withFirstName("first")
                .withLastName("last")
                .build();
        when(userRestService.retrieveUserById(999L)).thenReturn(restSuccess(userResource));
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
