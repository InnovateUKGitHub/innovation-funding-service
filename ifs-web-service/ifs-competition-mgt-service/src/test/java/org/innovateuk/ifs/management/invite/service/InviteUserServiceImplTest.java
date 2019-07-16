package org.innovateuk.ifs.management.invite.service;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.InviteUserResource;
import org.innovateuk.ifs.invite.service.InviteUserRestService;
import org.innovateuk.ifs.management.invite.service.InviteUserServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.innovateuk.ifs.commons.rest.RestResult.restSuccess;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class InviteUserServiceImplTest {

    @InjectMocks
    private InviteUserServiceImpl service;

    @Mock
    private InviteUserRestService inviteUserRestService;


    @Test
    public void saveProjectInvite() throws Exception {

        InviteUserResource inviteUserResource = new InviteUserResource();
        when(inviteUserRestService.saveUserInvite(inviteUserResource)).thenReturn(restSuccess());

        ServiceResult<Void> result = service.saveUserInvite(inviteUserResource);

        assertTrue(result.isSuccess());
        verify(inviteUserRestService).saveUserInvite(inviteUserResource);

    }
}
