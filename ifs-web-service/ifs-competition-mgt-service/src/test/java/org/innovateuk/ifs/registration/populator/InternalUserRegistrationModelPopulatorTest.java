package org.innovateuk.ifs.registration.populator;

import org.innovateuk.ifs.commons.rest.RestResult;
import org.innovateuk.ifs.invite.resource.RoleInviteResource;
import org.innovateuk.ifs.invite.service.InviteUserRestService;
import org.innovateuk.ifs.registration.viewmodel.InternalUserRegistrationViewModel;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Created by rav on 30/06/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class InternalUserRegistrationModelPopulatorTest {

    @InjectMocks
    private InternalUserRegistrationModelPopulator populator;

    @Mock
    private InviteUserRestService inviteUserRestServiceMock;

    @Before
    public void setUp() {
    }

    @Test
    public void testPopulateForm() {
        RoleInviteResource roleInviteResource = new RoleInviteResource(123L, "xyz", "xyz@email.com", 14L, "ifs_administrator", "SomeHashString");

        when(inviteUserRestServiceMock.getInvite("SomeHashString")).thenReturn(RestResult.restSuccess(roleInviteResource));

        InternalUserRegistrationViewModel result = populator.populateModel("SomeHashString");

        assertTrue(result != null);

        assertEquals(result.getRoleName(), roleInviteResource.getRoleDisplayName());

        assertEquals(result.getEmail(), roleInviteResource.getEmail());

        assertEquals(result.getName(), roleInviteResource.getName());
    }
}
